/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import me.aquavit.liquidsense.modules.render.Chams;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityChestRenderer.class)
public class MixinTileEntityChestRenderer {

    @Inject(method = "renderTileEntityAt", at = @At("HEAD"))
    private void injectChamsPre(CallbackInfo callbackInfo) {
        if (LiquidBounce.moduleManager.getModule(Chams.class).getState() && Chams.chestsValue.get()) {
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0F, -1000000F);
        }
    }

    @Inject(method = "renderTileEntityAt", at = @At("RETURN"))
    private void injectChamsPost(CallbackInfo callbackInfo) {
        if (LiquidBounce.moduleManager.getModule(Chams.class).getState() && Chams.chestsValue.get()) {
            GL11.glPolygonOffset(1.0F, 1000000F);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        }
    }
}
