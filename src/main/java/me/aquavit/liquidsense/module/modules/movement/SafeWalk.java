package me.aquavit.liquidsense.module.modules.movement;

import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.MoveEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.value.BoolValue;

@ModuleInfo(name = "SafeWalk", description = "Prevents you from falling down as if you were sneaking.", category = ModuleCategory.MOVEMENT)
public class SafeWalk extends Module {
    private BoolValue airSafeValue = new BoolValue("AirSafe", false);

    @EventTarget
    public void onMove(MoveEvent event){
        if (airSafeValue.get() || mc.thePlayer.onGround)
            event.setSafeWalk(true);
    }
}
