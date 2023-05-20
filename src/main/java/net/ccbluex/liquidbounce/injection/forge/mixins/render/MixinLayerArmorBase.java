package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import me.aquavit.liquidsense.modules.blatant.Aura;
import me.aquavit.liquidsense.modules.client.RenderChanger;
import me.aquavit.liquidsense.modules.client.Rotations;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
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
    protected abstract T getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, int slot, T model);

    @Shadow
    protected abstract void setModelPartVisible(T var1, int var2);

    @Shadow
    protected abstract boolean isSlotForLeggings(int p_isSlotForLeggings_1_);

    @Shadow
    public abstract ResourceLocation getArmorResource(Entity entity, ItemStack stack, int slot, String type);

    @Shadow
    private float alpha = 1.0F;

    @Shadow
    private float colorR = 1.0F;

    @Shadow
    private float colorG = 1.0F;

    @Shadow
    private float colorB = 1.0F;

    @Shadow
    private boolean skipRenderGlint;

    @Shadow
    protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");

	/**
	 * @author CCBlueX
	 * @reason CCBlueX
	 */
    @Overwrite
    private void renderLayer(EntityLivingBase p_renderLayer_1_, float p_renderLayer_2_, float p_renderLayer_3_, float p_renderLayer_4_, float p_renderLayer_5_, float p_renderLayer_6_, float p_renderLayer_7_, float p_renderLayer_8_, int p_renderLayer_9_) {
        ItemStack itemstack = this.getCurrentArmor(p_renderLayer_1_, p_renderLayer_9_);
        final RenderChanger rc = (RenderChanger) LiquidBounce.moduleManager.getModule(RenderChanger.class);
        final Rotations rot = (Rotations) LiquidBounce.moduleManager.getModule(Rotations.class);
        final Aura ka = (Aura) LiquidBounce.moduleManager.getModule(Aura.class);

        if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
            ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
            T t = this.getArmorModel(p_renderLayer_9_);
            t.setModelAttributes(this.renderer.getMainModel());
            t.setLivingAnimations(p_renderLayer_1_, p_renderLayer_2_, p_renderLayer_3_, p_renderLayer_4_);
            t = this.getArmorModelHook(p_renderLayer_1_, itemstack, p_renderLayer_9_, t);
            this.setModelPartVisible(t, p_renderLayer_9_);
            boolean flag = this.isSlotForLeggings(p_renderLayer_9_);
            this.renderer.bindTexture(this.getArmorResource(p_renderLayer_1_, itemstack, flag ? 2 : 1, null));

            int i = itemarmor.getColor(itemstack);

            if (i != -1 ) {
                float f = (float)(i >> 16 & 255) / 255.0F;
                float f1 = (float)(i >> 8 & 255) / 255.0F;
                float f2 = (float)(i & 255) / 255.0F;
                GlStateManager.color(this.colorR * f, this.colorG * f1, this.colorB * f2, this.alpha);
                t.render(p_renderLayer_1_, p_renderLayer_2_, p_renderLayer_3_, p_renderLayer_5_, p_renderLayer_6_, p_renderLayer_7_, p_renderLayer_8_);
                this.renderer.bindTexture(this.getArmorResource(p_renderLayer_1_, itemstack, flag ? 2 : 1, "overlay"));
            }

            GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
            if (p_renderLayer_1_.equals(Minecraft.getMinecraft().thePlayer)) {
                float alpha = (ka.getTarget() != null && rot.getState() && Rotations.ghost.get()) ? 15.9375f : RenderChanger.armorAlpha.get();

                if (rc.getState()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, !rc.getState() ? 1f : (alpha / 255f));
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(770, 771);
                    GlStateManager.alphaFunc(516, 0.003921569F);
                }

                //BackgroundShader.BACKGROUND_SHADER.startShader();
                t.render(p_renderLayer_1_, p_renderLayer_2_, p_renderLayer_3_, p_renderLayer_5_, p_renderLayer_6_, p_renderLayer_7_, p_renderLayer_8_);
                if (itemstack.hasEffect()) {
                    this.renderGlint(p_renderLayer_1_, t, p_renderLayer_2_, p_renderLayer_3_, p_renderLayer_4_, p_renderLayer_5_, p_renderLayer_6_, p_renderLayer_7_, p_renderLayer_8_);
                }
                //BackgroundShader.BACKGROUND_SHADER.stopShader();

                if (rc.getState()) {
                    GlStateManager.disableBlend();
                    GlStateManager.alphaFunc(516, 0.1F);
                    GlStateManager.popMatrix();
                }
            } else {
                if (p_renderLayer_1_.hurtTime > 0)
                    GlStateManager.color(1.0f, 0.75f, 0.75f, 1f);

                t.render(p_renderLayer_1_, p_renderLayer_2_, p_renderLayer_3_, p_renderLayer_5_, p_renderLayer_6_, p_renderLayer_7_, p_renderLayer_8_);
                if (itemstack.hasEffect()) {
                    this.renderGlint(p_renderLayer_1_, t, p_renderLayer_2_, p_renderLayer_3_, p_renderLayer_4_, p_renderLayer_5_, p_renderLayer_6_, p_renderLayer_7_, p_renderLayer_8_);
                }
            }
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
