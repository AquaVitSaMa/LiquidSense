/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.event.events.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public static final FloatValue shadowX = new FloatValue("ShadowX", 0.5f, 0.25f, 1f);
    public static final FloatValue shadowY = new FloatValue("ShadowY", 0.5f, 0.25f, 1f);
    public static final IntegerValue shadowAlpha = new IntegerValue("ShadowAlpha", 150, 50, 255);

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
