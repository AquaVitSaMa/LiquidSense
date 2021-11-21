package me.aquavit.liquidsense.utils.item;

import me.aquavit.liquidsense.utils.render.Translate;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class HotbarUtil {
    public Minecraft mc = Minecraft.getMinecraft();
    ScaledResolution sr = new ScaledResolution(mc);
    public Translate translate = new Translate(0f , 0f);
    public float size = 1.0f;

    public void renderHotbarItem(int index, int xPos, int yPos, float partialTicks){
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

//			mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, itemStack, xPos, yPos);
        } else {
            Fonts.font25.drawStringWithShadow(String.valueOf(index), xPos +
                    (float)Fonts.font25.getStringWidth(String.valueOf(index)) / 2, yPos +
                    (float)Fonts.font25.getHeight(), Color.WHITE.getRGB());
        }
    }
}
