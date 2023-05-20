package me.aquavit.liquidsense.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.value.FloatValue;

@ModuleInfo(name = "EveryThingBlock", description = "EveryThingBlock", category = ModuleCategory.RENDER)
public class EveryThingBlock extends Module {

    private final FloatValue x = new FloatValue("X", 0.0f, -1.0f, 1.0f);
    private final FloatValue y = new FloatValue("Y", 0.0f, -1.0f, 1.0f);
    private final FloatValue z = new FloatValue("Z", 0.0f, -1.0f, 1.0f);

    public FloatValue getX() {
        return x;
    }
    public FloatValue getY() {
        return y;
    }
    public FloatValue getZ() {
        return z;
    }

}

