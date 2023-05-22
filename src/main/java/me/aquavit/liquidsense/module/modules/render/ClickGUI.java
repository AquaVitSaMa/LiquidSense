package me.aquavit.liquidsense.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "ClickGUI", description = "Opens the ClickGUI.", category = ModuleCategory.RENDER, keyBind = Keyboard.KEY_RSHIFT, canEnable = false)
public class ClickGUI extends Module {

    @Override
    public void onEnable() {
        mc.displayGuiScreen(LiquidBounce.neverlose);
    }
}
