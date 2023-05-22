package me.aquavit.liquidsense.module.modules.ghost;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.value.IntegerValue;

@ModuleInfo(name = "FastPlace", description = "Allows you to place blocks faster.", category = ModuleCategory.GHOST)
public class FastPlace extends Module {
    public static IntegerValue speedValue = new IntegerValue("Speed", 0, 0, 4);
}
