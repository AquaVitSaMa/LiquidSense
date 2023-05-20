package me.aquavit.liquidsense.modules.ghost;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.value.FloatValue;

@ModuleInfo(name = "HitBox", description = "Makes hitboxes of targets bigger.", category = ModuleCategory.GHOST)
public class HitBox extends Module {
    public static FloatValue sizeValue = new FloatValue("Size", 0.4F, 0F, 1F);
}
