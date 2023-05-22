package me.aquavit.liquidsense.module.modules.client;

import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "NoBob", description = "Disables the view bobbing effect.", category = ModuleCategory.CLIENT)
public class NoBob extends Module {
    @EventTarget
    public void onUpdate(UpdateEvent event){
        mc.thePlayer.distanceWalkedModified = 0f;
    }
}
