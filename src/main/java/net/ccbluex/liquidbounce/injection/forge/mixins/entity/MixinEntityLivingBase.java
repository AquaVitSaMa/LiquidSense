/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.entity;

import me.aquavit.liquidsense.modules.misc.Animations;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventLivingUpdate;
import net.ccbluex.liquidbounce.event.JumpEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.LiquidWalk;
import net.ccbluex.liquidbounce.features.module.modules.movement.NoJumpDelay;
import net.ccbluex.liquidbounce.features.module.modules.render.AntiBlind;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {

    @Shadow
    protected abstract float getJumpUpwardsMotion();

    @Shadow
    public abstract PotionEffect getActivePotionEffect(Potion potionIn);

    @Shadow
    public abstract boolean isPotionActive(Potion potionIn);

    @Shadow
    private int jumpTicks;

    @Shadow
    protected boolean isJumping;

    @Shadow
    public void onLivingUpdate() {
    }

    @Shadow
    protected abstract void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos);

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract ItemStack getHeldItem();

    @Shadow protected abstract void updateAITick();

    /**
     * @author CCBlueX
     */
    @Overwrite
    protected void jump() {
        final JumpEvent jumpEvent = new JumpEvent(this.getJumpUpwardsMotion());
        LiquidBounce.eventManager.callEvent(jumpEvent);
        if(jumpEvent.isCancelled())
            return;

        this.motionY = jumpEvent.getMotion();

        if(this.isPotionActive(Potion.jump))
            this.motionY += (double) ((float) (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);

        if(this.isSprinting()) {
            float f = this.rotationYaw * 0.017453292F;
            this.motionX -= (double) (MathHelper.sin(f) * 0.2F);
            this.motionZ += (double) (MathHelper.cos(f) * 0.2F);
        }

        this.isAirBorne = true;
    }

    @Overwrite
    private int getArmSwingAnimationEnd() {
        Animations sb = (Animations) LiquidBounce.moduleManager.getModule(Animations.class);
        Boolean nm = sb.getSB().get() > 0;
        return this.isPotionActive(Potion.digSpeed)? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1 : nm ? 6  + sb.getSB().get() * 2 : ( this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void headLiving(CallbackInfo callbackInfo) {
        if (LiquidBounce.moduleManager.getModule(NoJumpDelay.class).getState())
            jumpTicks = 0;
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;isJumping:Z", ordinal = 1))
    private void onJumpSection(CallbackInfo callbackInfo) {

        final LiquidWalk liquidWalk = (LiquidWalk) LiquidBounce.moduleManager.getModule(LiquidWalk.class);

        if(liquidWalk.getState() && !isJumping && !isSneaking() && isInWater() &&
                liquidWalk.modeValue.get().equalsIgnoreCase("Swim")) {
            this.updateAITick();
        }
    }

    @Inject(method = "getLook", at = @At("HEAD"), cancellable = true)
    private void getLook(CallbackInfoReturnable<Vec3> callbackInfoReturnable) {
        if(((EntityLivingBase) (Object) this) instanceof EntityPlayerSP)
            callbackInfoReturnable.setReturnValue(getVectorForRotation(this.rotationPitch, this.rotationYaw));
    }

    @Inject(method = "isPotionActive(Lnet/minecraft/potion/Potion;)Z", at = @At("HEAD"), cancellable = true)
    private void isPotionActive(Potion p_isPotionActive_1_, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final AntiBlind antiBlind = (AntiBlind) LiquidBounce.moduleManager.getModule(AntiBlind.class);

        if((p_isPotionActive_1_ == Potion.confusion || p_isPotionActive_1_ == Potion.blindness) && antiBlind.getState() && antiBlind.getConfusionEffect().get())
            callbackInfoReturnable.setReturnValue(false);
    }

    @Inject(method = "onEntityUpdate", at = { @At("HEAD") })
    public void onEntityUpdate(CallbackInfo info) {
        EventLivingUpdate eventLivingUpdate = new EventLivingUpdate((EntityLivingBase) (Object) this);
        LiquidBounce.eventManager.callEvent(eventLivingUpdate);
    }
}
