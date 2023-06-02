package me.aquavit.liquidsense.injection.forge.mixins.render;

import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadDownloadImageData.class)
@SideOnly(Side.CLIENT)
public class MixinThreadDownloadImageData {

    /*
    @Inject(method = "loadTextureFromServer", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/client/renderer/ThreadDownloadImageData;org/apache/logging/log4j/Logger;org/apache/logging/log4j/error(Ljava/lang/String;Ljava/lang/Throwable;)V", shift = At.Shift.AFTER, ordinal = 1), cancellable = true)
    private void loadTextureFromServerInject(CallbackInfo callbackInfo) {

    }
     */

}
