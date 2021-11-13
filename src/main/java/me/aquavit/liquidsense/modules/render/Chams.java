package me.aquavit.liquidsense.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;

@ModuleInfo(name = "Chams", description = "Allows you to see targets through blocks.", category = ModuleCategory.RENDER)
public class Chams extends Module {
    public static FloatValue colorRedValue = new FloatValue("R", 200.0f, 0.0f, 255.0f);
    public static FloatValue colorGreenValue = new FloatValue("G", 100.0f, 0.0f, 255.0f);
    public static FloatValue colorBlueValue = new FloatValue("B", 100.0f, 0.0f, 255.0f);
    public static FloatValue colorAValue = new FloatValue("A", 100.0f, 0.0f, 255.0f);
    public static FloatValue colorRed2Value = new FloatValue("R2", 100.0f, 0.0f, 255.0f);
    public static FloatValue colorGreen2Value = new FloatValue("G2", 100.0f, 0.0f, 255.0f);
    public static FloatValue colorBlue2Value = new FloatValue("B2", 200.0f, 0.0f, 255.0f);
    public static FloatValue colorA2Value = new FloatValue("A2", 200.0f, 0.0f, 255.0f);

    public static BoolValue rainbow = new BoolValue("Rainbow", false);
    public static BoolValue targetsValue = new BoolValue("Targets", true);
    public static BoolValue chestsValue = new BoolValue("Chests", true);
    public static BoolValue itemsValue = new BoolValue("Items", true);
    public static BoolValue all = new BoolValue("AllEntity", false);
    public static BoolValue onlyhead = new BoolValue("OnlyHead", false);
}
