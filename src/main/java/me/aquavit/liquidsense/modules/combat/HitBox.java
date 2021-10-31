package me.aquavit.liquidsense.modules.combat;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.FloatValue;

@ModuleInfo(name = "HitBox", description = "Makes hitboxes of targets bigger.", category = ModuleCategory.COMBAT)
public class HitBox extends Module {
    public static FloatValue sizeValue = new FloatValue("Size", 0.4F, 0F, 1F);
}
