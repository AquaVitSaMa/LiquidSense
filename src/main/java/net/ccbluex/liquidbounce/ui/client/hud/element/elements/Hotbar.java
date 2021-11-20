package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import me.aquavit.liquidsense.utils.client.ClientUtils;
import me.aquavit.liquidsense.utils.render.Translate;
import net.ccbluex.liquidbounce.ui.client.hud.element.Border;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo;
import net.ccbluex.liquidbounce.ui.client.hud.element.Side;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@ElementInfo(name = "Hotbar")
public class Hotbar extends Element {

    public Hotbar(){
        super(-8,57,1f,new Side(Side.Horizontal.MIDDLE, Side.Vertical.DOWN));
    }

    private ListValue modeValue = new ListValue("Alignment", new String[] {"Horizontal", "Vertical"}, "Horizontal");

    ScaledResolution sr = new ScaledResolution(mc);
    private int lastSlot = -1;

    @Nullable
    @Override
    public Border drawElement() {

        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();

        /* 手持物品右边的向右移动
        for(int i = 0; i < 9; ++i) {
            renderHotbarItem(i, (i > mc.thePlayer.inventory.currentItem) ?
                    i * 20 + 10 :
                    i * 20, 0, mc.timer.renderPartialTicks);
        }
         */
        float scale = 1.5f;

        for(int i = 0; i < 9; ++i) {
            if (i == mc.thePlayer.inventory.currentItem && mc.thePlayer.inventory.mainInventory[i] != null) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(scale,scale,scale);
                renderHotbarItem(i, (int) ((i * 25 / scale) - scale), (int) ((-3 / scale) - scale), mc.timer.renderPartialTicks);
                GlStateManager.popMatrix();
            } else {
                renderHotbarItem(i, i * 25, 0, mc.timer.renderPartialTicks);
            }
        }

        lastSlot = mc.thePlayer.inventory.currentItem;

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        return new Border(0F, 0F, 180F, 17F);
    }

    private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks){
        ItemStack itemStack = mc.thePlayer.inventory.mainInventory[index];
        if (itemStack != null) {
            float lvt_7_1_ = (float)itemStack.animationsToGo - partialTicks;
            if (lvt_7_1_ > 0.0F) {
                GlStateManager.pushMatrix();
                float lvt_8_1_ = 1.0F + lvt_7_1_ / 5.0F;
                GlStateManager.translate((float)(xPos + 8), (float)(yPos + 12), 0.0F);
                GlStateManager.scale(1.0F / lvt_8_1_, (lvt_8_1_ + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float)(-(xPos + 8)), (float)(-(yPos + 12)), 0.0F);
            }

            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, xPos, yPos);
            if (lvt_7_1_ > 0.0F) {
                GlStateManager.popMatrix();
            }

            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, itemStack, xPos, yPos);
            RenderUtils.drawTexturedRect(xPos,yPos,20,17,"hotbar",sr);

        } else {
            if (index == mc.thePlayer.inventory.currentItem) {
                Fonts.font30.drawStringWithShadow(String.valueOf(index), xPos +
                        (float)Fonts.font30.getStringWidth(String.valueOf(index)) / 2, yPos -
                        (float)Fonts.font30.getHeight() / 2, Color.WHITE.getRGB());
            } else {
                Fonts.font25.drawStringWithShadow(String.valueOf(index), xPos +
                        (float)Fonts.font25.getStringWidth(String.valueOf(index)) / 2, yPos +
                        (float)Fonts.font25.getHeight(), Color.WHITE.getRGB());
            }
        }
    }
}
