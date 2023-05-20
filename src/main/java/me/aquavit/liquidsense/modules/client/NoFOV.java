package me.aquavit.liquidsense.modules.client;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.value.FloatValue;

@ModuleInfo(name = "NoFOV", description = "Disables FOV changes caused by speed effect, etc.", category = ModuleCategory.CLIENT)
public class NoFOV extends Module {
    public static FloatValue fovValue = new FloatValue("FOV", 1f, 0f, 1.5f);
}
