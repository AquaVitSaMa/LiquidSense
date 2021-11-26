package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import me.aquavit.liquidsense.modules.render.CaveFinder;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockModelRenderer.class)
public abstract class MixinBlockModelRenderer {

    @Shadow
    public abstract boolean renderModelAmbientOcclusion(IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides);

    @Shadow
    public abstract boolean renderModelStandard(IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides);

    /**
     * @author CCBlueX
     * @reason CCBlueX
     */
    @Overwrite
    public boolean renderModel(IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides)
    {
        boolean flag = Minecraft.isAmbientOcclusionEnabled() && blockStateIn.getBlock().getLightValue() == 0 && modelIn.isAmbientOcclusion();

        try
        {
            Block block = blockStateIn.getBlock();

            if (LiquidBounce.moduleManager.getModule(CaveFinder.class).getState()) {
                return this.renderModelAmbientOcclusion(blockAccessIn, modelIn, block, blockPosIn, worldRendererIn, checkSides);
            }else {
                return flag ? this.renderModelAmbientOcclusion(blockAccessIn, modelIn, block, blockPosIn, worldRendererIn, checkSides) : this.renderModelStandard(blockAccessIn, modelIn, block, blockPosIn, worldRendererIn, checkSides);
            }

        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, blockPosIn, blockStateIn);
            crashreportcategory.addCrashSection("Using AO", Boolean.valueOf(flag));
            throw new ReportedException(crashreport);
        }
    }

}
