/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package me.aquavit.liquidsense.injection.forge.mixins.render;

import me.aquavit.liquidsense.LiquidBounce;
import me.aquavit.liquidsense.event.events.RenderEntityEvent;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Render.class)
@SideOnly(Side.CLIENT)
public abstract class MixinRender<T extends Entity> {

    @Shadow
    protected abstract <T extends Entity> boolean bindEntityTexture(T entity);

    @Shadow
    protected abstract void renderName(T p_renderName_1_, double p_renderName_2_, double p_renderName_4_, double p_renderName_6_);

    @Shadow
    @Final
    protected RenderManager renderManager;

	/**
	 * @author CCBlueX
	 * @reason CCBlueX
	 */
    @Overwrite
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        LiquidBounce.eventManager.callEvent(new RenderEntityEvent(entity, x, y, z, entityYaw, partialTicks));
        this.renderName(entity, x, y, z);
    }
}
