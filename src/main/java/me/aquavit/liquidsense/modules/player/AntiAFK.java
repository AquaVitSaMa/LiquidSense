package me.aquavit.liquidsense.modules.player;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(name = "AntiAFK", description = "Prevents you from getting kicked for being AFK.", category = ModuleCategory.PLAYER)
public class AntiAFK extends Module {
    private MSTimer timer = new MSTimer();

    @EventTarget
    public void onUpdate(UpdateEvent event){
        mc.gameSettings.keyBindForward.pressed = true;

        if (timer.hasTimePassed(500)) {
            mc.thePlayer.rotationYaw += 180F;
            timer.reset();
        }
    }

    @Override
    public void onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindForward))
            mc.gameSettings.keyBindForward.pressed = false;
    }
}