package me.aquavit.liquidsense.injection.forge.mixins.gui;

import me.aquavit.liquidsense.module.modules.client.AntiBlind;
import me.aquavit.liquidsense.utils.render.BlurBuffer;
import me.aquavit.liquidsense.LiquidSense;
import me.aquavit.liquidsense.event.events.Render2DEvent;
import me.aquavit.liquidsense.module.modules.client.HUD;
import me.aquavit.liquidsense.utils.mc.ClassUtils;
import me.aquavit.liquidsense.ui.client.hud.element.Element;
import me.aquavit.liquidsense.ui.client.hud.element.elements.Hotbar;
import me.aquavit.liquidsense.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GuiIngame.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiInGame extends Gui {

    @Shadow
    protected RenderItem itemRenderer;

    private double slot = 0.0D;
    double speed = 1.0D;
    int lastSlot = 0;

	/**
	 * @author CCBlueX
	 * @reason CCBlueX
	 */
    @Overwrite
    protected void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player){
        ItemStack itemStack = player.inventory.mainInventory[index];
        if (itemStack != null) {
            float lvt_7_1_ = (float)itemStack.animationsToGo - partialTicks;
            if (lvt_7_1_ > 0.0F) {
                GlStateManager.pushMatrix();
                float lvt_8_1_ = 1.0F + lvt_7_1_ / 5.0F;
                GlStateManager.translate((float)(xPos + 8), (float)(yPos + 12), 0.0F);
                GlStateManager.scale(1.0F / lvt_8_1_, (lvt_8_1_ + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float)(-(xPos + 8)), (float)(-(yPos + 12)), 0.0F);
            }

            this.itemRenderer.renderItemAndEffectIntoGUI(itemStack, xPos, yPos);
            if (lvt_7_1_ > 0.0F) {
                GlStateManager.popMatrix();
            }

            this.itemRenderer.renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, itemStack, xPos, yPos);
        }
    }

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    private void renderScoreboard(CallbackInfo callbackInfo) {
        if (LiquidSense.moduleManager.getModule(HUD.class).getState())
            callbackInfo.cancel();
    }

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        final HUD hud = (HUD) LiquidSense.moduleManager.getModule(HUD.class);
        int ScrollSpeed = hud.hotbarSpeed.get() - 1;

        if (OpenGlHelper.shadersSupported && Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer)
           BlurBuffer.updateBlurBuffer(20f,true);

        for (Element e : LiquidSense.hud.getElements()) {
            if (e instanceof Hotbar) {
                LiquidSense.eventManager.callEvent(new Render2DEvent(partialTicks));
                return;
            }
        }

        if(Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer && hud.getState() && hud.blackHotbarValue.get()) {
            EntityPlayer entityPlayer = (EntityPlayer) Minecraft.getMinecraft().getRenderViewEntity();

            double currentItem = entityPlayer.inventory.currentItem;

            if (hud.moreinventory.get()){
                GlStateManager.pushMatrix();
                GlStateManager.translate(width()/2-90,height()-25,0);
                RenderUtils.drawBorderedRect(0,1,180,-58,1,new Color(0,0,0,255).getRGB(),new Color(0,0,0,130).getRGB());
                RenderHelper.enableGUIStandardItemLighting();
                //renderArmor();
                int x2=1,x3=1,x4=1;
                int i1,i3,i4;
                for (i1 = 27; i1 < 36; ++i1){
                    renderItem(i1, 1+x2, -16, Minecraft.getMinecraft().thePlayer);
                    x2+=20;
                }
                for (i3 = 18; i3 < 27; ++i3){
                    renderItem(i3, 1+x3, -36, Minecraft.getMinecraft().thePlayer);
                    x3+=20;
                }
                for (i4 = 9; i4 < 18; ++i4){
                    renderItem(i4, 1+x4, -56, Minecraft.getMinecraft().thePlayer);
                    x4+=20;
                }
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popMatrix();
            }

            this.speed = (Math.abs(this.slot + 1.0D - (currentItem + 1.0D)) / (100 - ScrollSpeed));

            if (Math.abs(entityPlayer.inventory.currentItem - this.slot) < this.speed) {
                currentItem = entityPlayer.inventory.currentItem;
            } else {
                double motion;

                if (entityPlayer.inventory.currentItem - this.slot > 0.0D) {
                    motion = this.speed;
                } else {
                    motion = -this.speed;
                }

                currentItem = this.slot + motion;
            }

            this.slot = currentItem;
            this.lastSlot = entityPlayer.inventory.currentItem;

            int middleScreen = sr.getScaledWidth() / 2;

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GuiIngame.drawRect(middleScreen - 90, sr.getScaledHeight() - 24, middleScreen + 90, sr.getScaledHeight(), Integer.MIN_VALUE);
            GuiIngame.drawRect((int) (middleScreen - 90 + this.slot * 20), sr.getScaledHeight() - 24, (int) (middleScreen - 94 + this.slot * 20 + 24), sr.getScaledHeight() - 22 - 1 + 24, Integer.MAX_VALUE);

            if (Math.abs(this.slot - this.lastSlot) <= 0.05D) {
                this.slot = this.lastSlot;
            }

            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            for(int j = 0; j < 9; ++j) {
                int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
                int l = sr.getScaledHeight() - 16 - 3;
                this.renderHotbarItem(j, k, l, partialTicks, entityPlayer);
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();

            LiquidSense.eventManager.callEvent(new Render2DEvent(partialTicks));
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void renderTooltipPost(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        if (!ClassUtils.hasClass("net.labymod.api.LabyModAPI")) {
            LiquidSense.eventManager.callEvent(new Render2DEvent(partialTicks));
        }
    }

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    private void renderPumpkinOverlay(final CallbackInfo callbackInfo) {
        if (LiquidSense.moduleManager.getModule(AntiBlind.class).getState() && AntiBlind.pumpkinEffect.get())
            callbackInfo.cancel();
    }

    private int width() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }

    private int height() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }

    private void renderItem(int i, int x, int y , EntityPlayer player) {
        ItemStack itemstack = player.inventory.mainInventory[i];
        if (itemstack != null) {
            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(itemstack, x, y);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, itemstack, x-1, y-1);
        }
    }
}