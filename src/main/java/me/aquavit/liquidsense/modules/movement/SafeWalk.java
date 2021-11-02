package me.aquavit.liquidsense.modules.movement;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;

@ModuleInfo(name = "SafeWalk", description = "Prevents you from falling down as if you were sneaking.", category = ModuleCategory.MOVEMENT)
public class SafeWalk extends Module {
    private BoolValue airSafeValue = new BoolValue("AirSafe", false);

    @EventTarget
    public void onMove(MoveEvent event){
        if (airSafeValue.get() || mc.thePlayer.onGround)
            event.setSafeWalk(true);
    }
}