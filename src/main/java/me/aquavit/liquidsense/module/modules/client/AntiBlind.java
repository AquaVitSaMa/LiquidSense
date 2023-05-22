package me.aquavit.liquidsense.module.modules.client;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.value.BoolValue;

@ModuleInfo(name = "AntiBlind", description = "Cancels blindness effects.", category = ModuleCategory.CLIENT)
public class AntiBlind extends Module {
    public static BoolValue confusionEffect = new BoolValue("Confusion", true);
    public static BoolValue pumpkinEffect = new BoolValue("Pumpkin", true);
    public static BoolValue fireEffect = new BoolValue("Fire", false);
}
