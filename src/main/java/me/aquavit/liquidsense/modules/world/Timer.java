package me.aquavit.liquidsense.modules.world;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.events.UpdateEvent;
import net.ccbluex.liquidbounce.event.events.WorldEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.FloatValue;

@ModuleInfo(name = "Timer", description = "Changes the speed of the entire game.", category = ModuleCategory.WORLD)
public class Timer extends Module {

    private FloatValue speedValue = new FloatValue("Speed", 2F, 0.1F, 10F);

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) return;
        mc.timer.timerSpeed = 1F;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        mc.timer.timerSpeed = speedValue.get();
    }

    @EventTarget
    public void onWorld(final WorldEvent e) {
        if (e.getWorldClient() != null)
            return;

        this.setState(false);
    }
}
