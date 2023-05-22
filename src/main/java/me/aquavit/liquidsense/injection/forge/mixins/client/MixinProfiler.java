/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package me.aquavit.liquidsense.injection.forge.mixins.client;

import net.ccbluex.liquidbounce.LiquidBounce;
import me.aquavit.liquidsense.event.events.Render2DEvent;
import me.aquavit.liquidsense.utils.mc.ClassUtils;
import net.minecraft.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Profiler.class)
public class MixinProfiler {

    @Inject(method = "startSection", at = @At("HEAD"))
    private void startSection(String name, CallbackInfo callbackInfo) {
        if(name.equals("bossHealth") && ClassUtils.hasClass("net.labymod.api.LabyModAPI"))
            LiquidBounce.eventManager.callEvent(new Render2DEvent(0F));
    }
}