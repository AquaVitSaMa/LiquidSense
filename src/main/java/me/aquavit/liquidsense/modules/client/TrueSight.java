package me.aquavit.liquidsense.modules.client;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;

@ModuleInfo(name = "TrueSight", description = "Allows you to see invisible entities and barriers.", category = ModuleCategory.CLIENT)
public class TrueSight extends Module {
    public static BoolValue barriersValue = new BoolValue("Barriers", true);
    public static BoolValue entitiesValue = new BoolValue("Entities", true);
}
