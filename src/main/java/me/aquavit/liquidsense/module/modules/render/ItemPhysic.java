package me.aquavit.liquidsense.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.value.BoolValue;

@ModuleInfo(name = "ItemPhysic", description = "Item Physic", category = ModuleCategory.RENDER)
public class ItemPhysic extends Module {
    public static BoolValue rotateX = new BoolValue("Rotate X", true);
    public static BoolValue rotateY = new BoolValue("Rotate Y", false);
    public static BoolValue rotateZ = new BoolValue("Rotate Z", true);
    public static BoolValue nohover = new BoolValue("No Hover", true);
}
