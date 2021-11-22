package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import me.aquavit.liquidsense.utils.item.HotbarUtil;
import net.ccbluex.liquidbounce.ui.client.hud.element.Border;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo;
import net.ccbluex.liquidbounce.ui.client.hud.element.Side;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@ElementInfo(name = "Hotbar")
public class Hotbar extends Element {

    public List<HotbarUtil> slot = new ArrayList<>();

    public Hotbar(){
        super(-8,57,1f,new Side(Side.Horizontal.MIDDLE, Side.Vertical.DOWN));

        for (int i = 0; i < 9 ; ++i) {
            HotbarUtil hotbarutils = new HotbarUtil();
            slot.add(hotbarutils);
        }
    }

    @Nullable
    @Override
    public Border drawElement() {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();

        for(int i = 0; i < slot.size(); ++i) {
            HotbarUtil now = slot.get(i);
            float posY;

            if (i == mc.thePlayer.inventory.currentItem && mc.thePlayer.inventory.mainInventory[i] != null) {
                now.size = 1.5f;
                posY = -3;
            } else {
                now.size = 1.0f;
                posY = 0;
            }

            now.translate.translate(now.size, posY);
            float scale = now.translate.getX();
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, scale);
            float x = i * 25f / scale - scale * 2f;
            now.renderHotbarItem(i, (int) x + scale, (int) (now.translate.getY() - scale / 2f - 2f / scale), mc.timer.renderPartialTicks);
            GlStateManager.popMatrix();

            mc.getRenderItem().renderItemOverlays(scale == 1f ? Fonts.font15 : Fonts.font18, mc.thePlayer.inventory.mainInventory[i], (int) (i * 25f), -3);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        return new Border(-6F, 0F, 219F, 17F);
    }
}
