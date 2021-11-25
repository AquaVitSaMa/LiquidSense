package net.ccbluex.liquidbounce.injection.forge.mixins.block;

import me.aquavit.liquidsense.modules.render.CaveFinder;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.block.BlockGrass;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockGrass.class)
@SideOnly(Side.CLIENT)
public abstract class MixinBlockGrass extends MixinBlock {

    @Overwrite
    public EnumWorldBlockLayer getBlockLayer() {
        if (LiquidBounce.moduleManager.getModule(CaveFinder.class).getState()) {
            return super.getBlockLayer();
        }
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

}
