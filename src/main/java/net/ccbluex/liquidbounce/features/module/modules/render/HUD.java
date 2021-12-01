/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import me.aquavit.liquidsense.event.EventTarget;
import net.ccbluex.liquidbounce.LiquidBounce;
import me.aquavit.liquidsense.event.events.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner;
import me.aquavit.liquidsense.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.", category = ModuleCategory.RENDER,array = false)
@SideOnly(Side.CLIENT)
public class HUD extends Module {

    public final BoolValue blackHotbarValue = new BoolValue("BlackHotbar", true);
    public final IntegerValue hotbarSpeed = new IntegerValue("HotbarSpeed",95,0,100);
    public final BoolValue inventoryParticle = new BoolValue("InventoryParticle", false);
    public final BoolValue moreinventory = new BoolValue("MoreInventory",false);
    private final BoolValue blurValue = new BoolValue("Blur", false);
    public final FontValue chatFont = new FontValue("ChatFont", mc.fontRendererObj) {
        @Override
        protected void onChanged(FontRenderer oldValue, FontRenderer newValue) {
            mc.ingameGUI.getChatGUI().refreshChat();
        }
    };
    public static final ListValue fontShadow = new ListValue("FontShadow", new String[] {
            "LiquidBounce",
            "Outline",
            "Default",
            "Autumn"
    }, "Default");
    public static final IntegerValue fontWidth = new IntegerValue("FontWidth", 8, 5, 10);
    private static final IntegerValue colorRedValue = new IntegerValue("R", 0, 0, 255);
    private static final IntegerValue colorGreenValue = new IntegerValue("G", 160, 0, 255);
    private static final IntegerValue colorBlueValue = new IntegerValue("B", 255, 0, 255);
    private static final BoolValue colorRainbow = new BoolValue("Rainbow", false);

    public static Color generateColor() {
        return colorRainbow.get() ? ColorUtils.rainbow() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
    }

    public HUD() {
        setState(true);
        setArray(false);
    }

    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        if (mc.currentScreen instanceof GuiHudDesigner)
            return;

        LiquidBounce.hud.render(false);
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        LiquidBounce.hud.update();
    }

    @EventTarget
    public void onLivingUpdate(final LivingUpdateEvent event){
        LiquidBounce.hud.livingupdate();
    }

    @EventTarget
    public void onKey(final KeyEvent event) {
        LiquidBounce.hud.handleKey('a', event.getKey());
    }

    @EventTarget(ignoreCondition = true)
    public void onScreen(final ScreenEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null)
            return;

        if (getState() && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.getGuiScreen() != null &&
                !(event.getGuiScreen() instanceof GuiChat || event.getGuiScreen() instanceof GuiHudDesigner))
            mc.entityRenderer.loadShader(new ResourceLocation("liquidbounce/blur.json"));
        else if (mc.entityRenderer.getShaderGroup() != null &&
                mc.entityRenderer.getShaderGroup().getShaderGroupName().contains("liquidbounce/blur.json"))
            mc.entityRenderer.stopUseShader();
    }
}
