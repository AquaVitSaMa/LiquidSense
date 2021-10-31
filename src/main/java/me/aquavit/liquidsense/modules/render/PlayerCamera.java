package me.aquavit.liquidsense.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;

@ModuleInfo(name = "PlayerCamera", description = "PlayerCamera", category = ModuleCategory.RENDER)
public class PlayerCamera extends Module {
    public static BoolValue moreinventory = new BoolValue("MoreInventory",true);
    //public static BoolValue betterhurt = new BoolValue("BetterHurt",true);
}
