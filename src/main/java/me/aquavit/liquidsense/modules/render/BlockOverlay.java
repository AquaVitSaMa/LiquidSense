package me.aquavit.liquidsense.modules.render;

import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.Render2DEvent;
import me.aquavit.liquidsense.event.events.Render3DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import me.aquavit.liquidsense.utils.block.BlockUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "BlockOverlay", description = "Allows you to change the design of the block overlay.", category = ModuleCategory.RENDER)
public class BlockOverlay extends Module {

    private IntegerValue colorRedValue = new IntegerValue("R", 68, 0, 255);
    private IntegerValue colorGreenValue = new IntegerValue("G", 117, 0, 255);
    private IntegerValue colorBlueValue = new IntegerValue("B", 255, 0, 255);
    private BoolValue colorRainbow = new BoolValue("Rainbow", false);

    public static BoolValue infoValue = new BoolValue("Info", false);

    public BlockPos getCurrentBlock() {
        BlockPos blockPos = mc.objectMouseOver.getBlockPos();
        if (blockPos == null)return null;

        if (BlockUtils.canBeClicked(blockPos) && mc.theWorld.getWorldBorder().contains(blockPos))
            return blockPos;

        return null;
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        BlockPos blockPos = getCurrentBlock();
        if (blockPos == null) return;
        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
        if (block == null) return;
        float partialTicks = event.getPartialTicks();
        Color color = colorRainbow.get() ? ColorUtils.rainbow((long) 0.4f) : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), 102);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        RenderUtils.glColor(color);
        GL11.glLineWidth(2F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        block.setBlockBoundsBasedOnState(mc.theWorld, blockPos);

        double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTicks;
        double y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTicks;
        double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTicks;

        AxisAlignedBB axisAlignedBB = block.getSelectedBoundingBox(mc.theWorld, blockPos)
                .expand(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026)
                .offset(-x, -y, -z);

        RenderGlobal.drawSelectionBoundingBox(axisAlignedBB);
        RenderUtils.drawFilledBox(axisAlignedBB);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (infoValue.get()) {
            BlockPos blockPos = getCurrentBlock();
            if (blockPos == null) return;
            Block block = mc.theWorld.getBlockState(blockPos).getBlock();
            if (block == null) return;

            String info = block.getLocalizedName() + " §7ID: " + Block.getIdFromBlock(block);
            ScaledResolution scaledResolution = new ScaledResolution(mc);

            RenderUtils.drawBorderedRect(
                    (float) scaledResolution.getScaledWidth() / 2 - 2F,
                    (float) scaledResolution.getScaledHeight() / 2 + 5F,
                    (float) scaledResolution.getScaledWidth() / 2 + Fonts.font20.getStringWidth(info) + 2F,
                    (float) scaledResolution.getScaledHeight() / 2 + 16F,
                    3F, Color.BLACK.getRGB(), Color.BLACK.getRGB()
            );
            GlStateManager.resetColor();
            Fonts.font20.drawString(info, scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() / 2 + 7,
                    Color.WHITE.getRGB());
        }
    }
}
