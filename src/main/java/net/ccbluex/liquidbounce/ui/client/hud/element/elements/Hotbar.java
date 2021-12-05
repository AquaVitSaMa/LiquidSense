package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import me.aquavit.liquidsense.utils.item.HotbarUtil;
import me.aquavit.liquidsense.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.ui.client.hud.element.Border;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo;
import net.ccbluex.liquidbounce.ui.client.hud.element.Side;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ElementInfo(name = "Hotbar")
public class Hotbar extends Element {

	private final ListValue modeValue = new ListValue("Mode", new String[]{"X", "Y"}, "X");
	public List<HotbarUtil> slot = new ArrayList<>();

	public Hotbar() {
		super(-8, 57, 1f, new Side(Side.Horizontal.MIDDLE, Side.Vertical.DOWN));

		for (int i = 0; i < 9; ++i) {
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

		float positionY = 0f;
		for (int i = 0; i < slot.size(); ++i) {
			HotbarUtil now = slot.get(i);

			switch (modeValue.get()) {
				case "X": {
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
					RenderHelper.enableGUIStandardItemLighting();
					now.renderXHotbarItem(i, x + scale, now.translate.getY() - scale / 2f - 2f / scale, mc.timer.renderPartialTicks);
					RenderHelper.disableStandardItemLighting();
					GlStateManager.popMatrix();
					mc.getRenderItem().renderItemOverlays(scale == 1f ? Fonts.font15 : Fonts.font18, mc.thePlayer.inventory.mainInventory[i], (int) (i * 25f), -3);
					break;
				}
				case "Y" :{
					int backgounde = (i == mc.thePlayer.inventory.currentItem) ? new Color(255, 255, 255, 200).getRGB() : new Color(200, 200, 200, 150).getRGB();
					RenderUtils.drawRoundedRect(-5f, positionY + 0f, 42f, 18f, 2f, backgounde, 0f, backgounde);
					RenderUtils.drawRoundedRect(25f, positionY + 5f, 10f, 10f, 2f, new Color(195, 195, 195, 200).getRGB(), 0f, new Color(195, 195, 195, 200).getRGB());
					Fonts.font18.drawString(String.valueOf(i + 1), 27f,
						positionY + 7 , Color.BLACK.getRGB());
					RenderHelper.enableGUIStandardItemLighting();
					//now.renderYHotbarItem(i, 0f ,  positionY + 1, mc.timer.renderPartialTicks);
					RenderHelper.disableStandardItemLighting();
					mc.getRenderItem().renderItemOverlays(Fonts.font18, mc.thePlayer.inventory.mainInventory[i],  0, (int) positionY + 1);
					positionY += 20;
					break;
				}
			}
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		return new Border(-6F, 0F, 219F, 17F);
	}
}
