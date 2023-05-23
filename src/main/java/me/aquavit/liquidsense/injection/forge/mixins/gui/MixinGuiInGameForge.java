package me.aquavit.liquidsense.injection.forge.mixins.gui;

import me.aquavit.liquidsense.LiquidBounce;
import me.aquavit.liquidsense.ui.client.hud.element.Element;
import me.aquavit.liquidsense.ui.client.hud.element.elements.HeadLogo;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
@SideOnly(Side.CLIENT)
public class MixinGuiInGameForge extends MixinGuiInGame {

    @Inject(method = "renderHealth", at = @At("HEAD"), cancellable = true, remap = false)
    public void renderPlayerStats(CallbackInfo callbackInfo) {
        for (Element e : LiquidBounce.hud.getElements()){
            if(e instanceof HeadLogo){
                callbackInfo.cancel();
            }
        }

    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true, remap = false)
    public void renderFood(CallbackInfo callbackInfo) {
        for (Element e : LiquidBounce.hud.getElements()){
            if(e instanceof HeadLogo){
                callbackInfo.cancel();
            }
        }
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true, remap = false)
    public void renderArmor(CallbackInfo callbackInfo) {
        for (Element e : LiquidBounce.hud.getElements()){
            if(e instanceof HeadLogo){
                callbackInfo.cancel();
            }
        }
    }

}
