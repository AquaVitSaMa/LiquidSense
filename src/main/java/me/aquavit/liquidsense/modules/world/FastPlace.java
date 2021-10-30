package me.aquavit.liquidsense.modules.world;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.IntegerValue;

@ModuleInfo(name = "FastPlace", description = "Allows you to place blocks faster.", category = ModuleCategory.WORLD)
public class FastPlace extends Module {
    public static IntegerValue speedValue = new IntegerValue("Speed", 0, 0, 4);
}
