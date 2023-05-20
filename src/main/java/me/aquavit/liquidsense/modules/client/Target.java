package me.aquavit.liquidsense.modules.client;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.value.BoolValue;

@ModuleInfo(name = "Target", description = "Choose Entity", category = ModuleCategory.CLIENT, canEnable = false)
public class Target extends Module {
    public static final BoolValue player = new BoolValue("Player", true);
    public static final BoolValue animal = new BoolValue("Animal", false);
    public static final BoolValue mob = new BoolValue("Mob", true);
    public static final BoolValue invisible = new BoolValue("Invisible", false);
    public static final BoolValue dead = new BoolValue("Dead", false);

    @Override
    public boolean handleEvents() {
        return true;
    }
}
