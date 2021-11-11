package me.aquavit.liquidsense.modules.misc;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.utils.timer.TimeUtils;

@ModuleInfo(name = "MemoryFixer", description = "MemoryFixer", category = ModuleCategory.MISC)
public class MemoryFixer extends Module {

    TimeUtils timer = new TimeUtils();
    @EventTarget
    public void onTick(){
        if(timer.hasReached(3000)) {
            System.gc();
            //Runtime.getRuntime().gc();
            timer.reset();
        }

    }
}
