package me.aquavit.liquidsense.injection.forge.mixins.render;

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityParticleEmitter;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

@Mixin(EffectRenderer.class)
@SideOnly(Side.CLIENT)
public abstract class MixinEffectRenderer {

    @Shadow
    protected abstract void updateEffectLayer(int layer);

    @Shadow
    private List<EntityParticleEmitter> particleEmitters;

	/**
	 * @author CCBlueX
	 * @reason CCBlueX
	 */
    @Overwrite
    public void updateEffects() {
        try {
            for(int i = 0; i < 4; ++i)
                this.updateEffectLayer(i);

            for(final Iterator<EntityParticleEmitter> it = this.particleEmitters.iterator(); it.hasNext(); ) {
                final EntityParticleEmitter entityParticleEmitter = it.next();

                entityParticleEmitter.onUpdate();

                if(entityParticleEmitter.isDead)
                    it.remove();
            }
        }catch(final ConcurrentModificationException ignored) {
        }
    }
}