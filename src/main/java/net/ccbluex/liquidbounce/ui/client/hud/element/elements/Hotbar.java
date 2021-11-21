package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import me.aquavit.liquidsense.utils.client.ClientUtils;
import me.aquavit.liquidsense.utils.item.HotbarUtil;
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
import java.util.ArrayList;
import java.util.List;

@ElementInfo(name = "Hotbar")
public class Hotbar extends Element {

    public List<HotbarUtil> slotlist = new ArrayList<>();
    ScaledResolution sr = new ScaledResolution(mc);

    public Hotbar(){
        super(-8,57,1f,new Side(Side.Horizontal.MIDDLE, Side.Vertical.DOWN));
        for(int i =0; i < 8 ; ++i) {
            HotbarUtil hotbarutils = new HotbarUtil();
            slotlist.add(hotbarutils);
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

        for(int i = 0; i < slotlist.size(); ++i) {
            HotbarUtil now = slotlist.get(i);
            float positony;

            if (i == mc.thePlayer.inventory.currentItem && mc.thePlayer.inventory.mainInventory[i] != null) {
                now.size = 1.5f;
                positony = -3;
            } else {
                now.size = 1.0f;
                positony = 0;
            }

            now.translate.translate(now.size , positony);
            GlStateManager.pushMatrix();
            GlStateManager.scale(now.translate.getX(),now.translate.getX(),now.translate.getX());
            now.renderHotbarItem(i, (int) ((i * 25 / now.translate.getX()) - now.translate.getX()), (int) (now.translate.getY() - (now.translate.getX())), mc.timer.renderPartialTicks);
            GlStateManager.popMatrix();
            RenderUtils.drawTexturedRect(i * 25,0,20,17,"hotbar",sr);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        return new Border(0F, 0F, 180F, 17F);
    }
}
