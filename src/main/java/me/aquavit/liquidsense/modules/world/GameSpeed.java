package me.aquavit.liquidsense.modules.world;

import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.UpdateEvent;
import me.aquavit.liquidsense.utils.entity.MovementUtils;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;

@ModuleInfo(name = "GameSpeed", description = "Changes the speed of the entire game.", category = ModuleCategory.WORLD)
public class GameSpeed extends Module {

    private final IntegerValue tickValue = new IntegerValue("TicksExisted", 1, 1, 5);
    private final FloatValue speedValue = new FloatValue("Speed", 2F, 0.1F, 10F);
    private final BoolValue onMoveValue = new BoolValue("OnMove", true);

    @Override
    public void onEnable() {
        if (mc.thePlayer == null)
            return;

        mc.timer.timerSpeed = speedValue.get();
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null)
            return;

        mc.timer.timerSpeed = 1F;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (MovementUtils.isMoving() || !onMoveValue.get()) {
            if (mc.thePlayer.ticksExisted % tickValue.get() == 0) {
                mc.timer.timerSpeed = speedValue.get();
            }
        }
    }

    @Override
    public String getTag(){
        return String.valueOf(mc.timer.timerSpeed);
    }
}
