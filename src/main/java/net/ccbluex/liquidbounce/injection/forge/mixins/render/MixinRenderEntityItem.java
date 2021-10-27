/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import me.aquavitt.liquidssense.modules.render.ItemPhysic;
import me.aquavitt.liquidssense.utils.item.ItemPhysicUtil;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.render.Chams;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(RenderEntityItem.class)
public abstract class MixinRenderEntityItem extends MixinRender{

    @Shadow
    private Random field_177079_e = new Random();

    @Shadow
    @Final
    private RenderItem itemRenderer;

    @Shadow
    public abstract boolean shouldSpreadItems();

    @Shadow
    protected abstract ResourceLocation getEntityTexture(EntityItem p_getEntityTexture_1_);

    @Shadow
    protected abstract int func_177078_a(ItemStack p_177078_1_);

    @Shadow
    public abstract boolean shouldBob();

    /**
     * @author AquaVit QQ:2924270322
     */
    @Overwrite
    private int func_177077_a(EntityItem p_177077_1_, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_){
        ItemStack itemstack = p_177077_1_.getEntityItem();
        Item item = itemstack.getItem();
        if (item == null) {
            return 0;
        } else {
            boolean flag = p_177077_9_.isGui3d();
            int i = this.func_177078_a(itemstack);
            float f = 0.25F;
            float f1 = this.shouldBob() ? MathHelper.sin(((float)p_177077_1_.getAge() + p_177077_8_) / 10.0F + p_177077_1_.hoverStart) * 0.1F + 0.1F : 0.0F;
            float f2 = p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
            GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_ + f1 + 0.25F * f2, (float)p_177077_6_);
            float f6;
            if (flag || this.renderManager.options != null) {
                f6 = (((float)p_177077_1_.getAge() + p_177077_8_) / 20.0F + p_177077_1_.hoverStart) * 57.295776F;
                GlStateManager.rotate(f6, 0.0F, 1.0F, 0.0F);
            }

            if (!flag) {
                f6 = -0.0F * (float)(i - 1) * 0.5F;
                float f4 = -0.0F * (float)(i - 1) * 0.5F;
                float f5 = -0.046875F * (float)(i - 1) * 0.5F;
                GlStateManager.translate(f6, f4, f5);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return i;
        }
    }


    /**
     * @author AquaVit QQ:2924270322
     */
    @Overwrite
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        final Chams chams = (Chams) LiquidBounce.moduleManager.getModule(Chams.class);

        if (chams.getState() && chams.getItemsValue().get()) {
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0F, -1000000F);
        }

        ItemStack itemstack = entity.getEntityItem();
        this.field_177079_e.setSeed(187L);
        boolean flag = false;
        if (this.bindEntityTexture(entity)) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
            flag = true;
        }

        if (LiquidBounce.moduleManager.getModule(ItemPhysic.class).getState()){
            ItemPhysicUtil.doRenderItemPhysic(entity, x, y, z, entityYaw, partialTicks);
            if (chams.getState() && chams.getItemsValue().get()) {
                GL11.glPolygonOffset(1.0F, 1000000F);
                GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            }
            return;
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelMesher().getItemModel(itemstack);
        int i = this.func_177077_a(entity, x, y, z, partialTicks, ibakedmodel);

        for(int j = 0; j < i; ++j) {
            GlStateManager.pushMatrix();
            if (j > 0) {
                float f = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                float f1 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                float f2 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                GlStateManager.translate(this.shouldSpreadItems() ? f : 0.0F, this.shouldSpreadItems() ? f1 : 0.0F, f2);
            }

            if (ibakedmodel.isGui3d()) {
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
            }

            ibakedmodel = ForgeHooksClient.handleCameraTransforms(ibakedmodel, ItemCameraTransforms.TransformType.GROUND);
            this.itemRenderer.renderItem(itemstack, ibakedmodel);
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.bindEntityTexture(entity);
        if (flag) {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
        }

        if (chams.getState() && chams.getItemsValue().get()) {
            GL11.glPolygonOffset(1.0F, 1000000F);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /*
    @Inject(method = "doRender", at = @At("HEAD"))
    private void injectChamsPre(CallbackInfo callbackInfo) {
        final Chams chams = (Chams) LiquidBounce.moduleManager.getModule(Chams.class);

        if (chams.getState() && chams.getItemsValue().get()) {
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0F, -1000000F);
        }

    }

    @Inject(method = "doRender", at = @At("RETURN"))
    private void injectChamsPost(CallbackInfo callbackInfo) {
        final Chams chams = (Chams) LiquidBounce.moduleManager.getModule(Chams.class);

        if (chams.getState() && chams.getItemsValue().get()) {
            GL11.glPolygonOffset(1.0F, 1000000F);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        }
    }
     */
}
