package me.aquavit.liquidsense.utils.item;

import me.aquavit.liquidsense.utils.mc.MinecraftInstance;
import me.aquavit.liquidsense.utils.render.Translate;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class HotbarUtil extends MinecraftInstance {
    ScaledResolution sr = new ScaledResolution(mc);
    public Translate translate = new Translate(0f , 0f);
    public float size = 1.0f;

    public void renderHotbarItem(int index, float xPos, float yPos, float partialTicks){
        ItemStack itemStack = mc.thePlayer.inventory.mainInventory[index];
        if (itemStack != null) {
            float lvt_7_1_ = (float)itemStack.animationsToGo - partialTicks;
            if (lvt_7_1_ > 0.0F) {
                GlStateManager.pushMatrix();
                float lvt_8_1_ = 1.0F + lvt_7_1_ / 5.0F;
                GlStateManager.translate(xPos + 8F, yPos + 12F, 0.0F);
                GlStateManager.scale(1.0F / lvt_8_1_, (lvt_8_1_ + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate(-xPos - 8F, -yPos - 12F, 0.0F);
            }

            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) xPos, (int) yPos);
            if (lvt_7_1_ > 0.0F) {
                GlStateManager.popMatrix();
            }


            RenderUtils.drawTexturedRect(xPos - 7, yPos -7,30,30,"hotbar", new ScaledResolution(mc));
        } else {
            Fonts.font22.drawStringWithShadow(String.valueOf(index),
                    xPos + (float)Fonts.font22.getStringWidth(String.valueOf(index)) / 2f,
                    yPos + (float)Fonts.font22.getHeight() / 2f, Color.WHITE.getRGB());
        }
    }
}
