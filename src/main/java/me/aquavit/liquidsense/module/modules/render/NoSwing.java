package me.aquavit.liquidsense.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.value.BoolValue;

@ModuleInfo(name = "NoSwing", description = "Disabled swing effect when hitting an entity/mining a block.", category = ModuleCategory.RENDER)
public class NoSwing extends Module {
    public static BoolValue serverSideValue = new BoolValue("ServerSide", true);
}
