package me.aquavit.liquidsense.injection.forge.mixins.render;

import me.aquavit.liquidsense.module.modules.blatant.Aura;
import me.aquavit.liquidsense.module.modules.client.RenderChanger;
import me.aquavit.liquidsense.module.modules.client.Rotations;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArmorBase.class)
@SideOnly(Side.CLIENT)

public abstract class MixinLayerArmorBase <T extends ModelBase> implements LayerRenderer<EntityLivingBase> {

    @Shadow
    public abstract ItemStack getCurrentArmor(EntityLivingBase p_getCurrentArmor_1_, int p_getCurrentArmor_2_);

    @Shadow
    public abstract T getArmorModel(int p_getArmorModel_1_);

    @Final
    @Shadow
    private RendererLivingEntity<?> renderer;

    @Shadow
    @Final
    protected static ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    @Inject(method = "renderLayer", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/GlStateManager.color(FF FF)V", shift = At.Shift.AFTER, ordinal = 1), cancellable = true)
    private void renderLayer(EntityLivingBase entitylivingbase, float p_renderLayer_2_, float p_renderLayer_3_, float partialTicks, float p_renderLayer_5_, float p_renderLayer_6_, float p_renderLayer_7_, float scale, int armorSlot, CallbackInfo callbackInfo) {

        final RenderChanger renderChanger = (RenderChanger) LiquidBounce.moduleManager.getModule(RenderChanger.class);
        final Rotations rotations = (Rotations) LiquidBounce.moduleManager.getModule(Rotations.class);
        final Aura aura = (Aura) LiquidBounce.moduleManager.getModule(Aura.class);

        ItemStack itemstack = this.getCurrentArmor(entitylivingbase, armorSlot);
        if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
            T t = this.getArmorModel(armorSlot);
            t.setModelAttributes(this.renderer.getMainModel());
            t.setLivingAnimations(entitylivingbase, p_renderLayer_2_, p_renderLayer_3_, partialTicks);

            if (entitylivingbase.equals(Minecraft.getMinecraft().thePlayer)) {
                float alpha = (aura.getTarget() != null && rotations.getState() && Rotations.ghost.get()) ? 15.9375f : RenderChanger.armorAlpha.get();

                if (renderChanger.getState()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, !renderChanger.getState() ? 1f : (alpha / 255f));
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(770, 771);
                    GlStateManager.alphaFunc(516, 0.003921569F);
                }

                //BackgroundShader.BACKGROUND_SHADER.startShader();
                t.render(entitylivingbase, p_renderLayer_2_, p_renderLayer_3_, p_renderLayer_5_, p_renderLayer_6_, p_renderLayer_7_, scale);
                if (itemstack.hasEffect()) {
                    this.renderGlint(entitylivingbase, t, p_renderLayer_2_, p_renderLayer_3_, partialTicks, p_renderLayer_5_, p_renderLayer_6_, p_renderLayer_7_, scale);
                }
                //BackgroundShader.BACKGROUND_SHADER.stopShader();

                if (renderChanger.getState()) {
                    GlStateManager.disableBlend();
                    GlStateManager.alphaFunc(516, 0.1F);
                    GlStateManager.popMatrix();
                }
            } else {
                if (entitylivingbase.hurtTime > 0)
                    GlStateManager.color(1.0f, 0.75f, 0.75f, 1f);

                t.render(entitylivingbase, p_renderLayer_2_, p_renderLayer_3_, p_renderLayer_5_, p_renderLayer_6_, p_renderLayer_7_, scale);
                if (itemstack.hasEffect()) {
                    this.renderGlint(entitylivingbase, t, p_renderLayer_2_, p_renderLayer_3_, partialTicks, p_renderLayer_5_, p_renderLayer_6_, p_renderLayer_7_, scale);
                }
            }

            callbackInfo.cancel();
        }
       
    }
    /**
     * @author CCBlueX
     * @reason CCBlueX
     */
    @Overwrite
    private void renderGlint(EntityLivingBase p_renderGlint_1_, T p_renderGlint_2_, float p_renderGlint_3_, float p_renderGlint_4_, float p_renderGlint_5_, float p_renderGlint_6_, float p_renderGlint_7_, float p_renderGlint_8_, float p_renderGlint_9_) {
        float f = (float)p_renderGlint_1_.ticksExisted + p_renderGlint_5_;
        this.renderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        float f1 = 0.5F;
        GlStateManager.color(f1, f1, f1, 1.0F);

        for(int i = 0; i < 2; ++i) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(768, 1);
            if (LiquidBounce.moduleManager.getModule(RenderChanger.class).getState()) {
                GlStateManager.color(RenderChanger.armorRed.get() / 255f, RenderChanger.armorGreen.get() / 255f, RenderChanger.armorBlue.get() / 255f, 0.66666667F);
            } else {
                float f2 = 0.76F;
                GlStateManager.color(0.5F * f2, 0.25F * f2, 0.8F * f2, 1.0F);
            }
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float f3 = 0.33333334F;
            GlStateManager.scale(f3, f3, f3);
            GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            p_renderGlint_2_.render(p_renderGlint_1_, p_renderGlint_3_, p_renderGlint_4_, p_renderGlint_6_, p_renderGlint_7_, p_renderGlint_8_, p_renderGlint_9_);
        }

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
    }

}
