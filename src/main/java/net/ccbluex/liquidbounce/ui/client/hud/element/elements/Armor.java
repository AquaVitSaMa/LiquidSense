package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import net.ccbluex.liquidbounce.ui.client.hud.element.Border;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo;
import net.ccbluex.liquidbounce.ui.client.hud.element.Side;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

@ElementInfo(name = "Armor")
public class Armor extends Element {

    private ListValue modeValue = new ListValue("Alignment", new String[] {"Horizontal", "Vertical"}, "Horizontal");

    public Armor(){
        super(-8,57,1f,new Side(Side.Horizontal.MIDDLE, Side.Vertical.DOWN));
    }

    @Nullable
    @Override
    public Border drawElement() {
        if (mc.playerController.isNotCreative()) {
            GL11.glPushMatrix();

            RenderItem renderItem = mc.getRenderItem();
            boolean isInsideWater = mc.thePlayer.isInsideOfMaterial(Material.water);

            int x = 1;
            int y = isInsideWater ? -10 : 0;

            for (int n = 3; n >= 0; n--) {
                ItemStack stack = mc.thePlayer.inventory.armorInventory[n];
                if (stack == null)continue;
                renderItem.renderItemIntoGUI(stack, x, y);
                renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y);
                if (modeValue.get().equalsIgnoreCase("Horizontal")) x += 18;
                else if (modeValue.get().equalsIgnoreCase("Vertical")) y += 18;
            }

            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GL11.glPopMatrix();

        }
        return modeValue.get().equalsIgnoreCase("Horizontal") ? new Border(0F, 0F, 72F, 17F) : new Border(0F, 0F, 18F, 72F);
    }


}
