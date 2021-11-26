package me.aquavit.liquidsense.modules.movement;

import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "Freeze", description = "Allows you to stay stuck in mid air.", category = ModuleCategory.MOVEMENT)
public class Freeze extends Module {

    @EventTarget
    public void onUpdate(UpdateEvent event){
        mc.thePlayer.isDead = true;
        mc.thePlayer.rotationYaw = mc.thePlayer.cameraYaw;
        mc.thePlayer.rotationPitch = mc.thePlayer.cameraPitch;
    }

    @Override
    public void onDisable() {
        mc.thePlayer.isDead = false;
    }
}
