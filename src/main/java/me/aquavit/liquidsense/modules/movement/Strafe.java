package me.aquavit.liquidsense.modules.movement;

import me.aquavit.liquidsense.utils.entity.MovementUtils;
import net.ccbluex.liquidbounce.event.EventState;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.events.MotionEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "Strafe", description = "Allows you to freely move in mid air.", category = ModuleCategory.MOVEMENT)
public class Strafe extends Module {

    @EventTarget
    public void onMotion(MotionEvent event){
        if (event.getEventState() == EventState.POST)
            return;
        MovementUtils.strafe();
    }
}
