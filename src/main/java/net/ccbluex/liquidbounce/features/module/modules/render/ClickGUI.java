package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.miscible.Miscible;
import net.ccbluex.liquidbounce.ui.client.neverlose.Main;
import net.ccbluex.liquidbounce.value.ListValue;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "ClickGUI", description = "Opens the ClickGUI.", category = ModuleCategory.RENDER, keyBind = Keyboard.KEY_RSHIFT, canEnable = false)
public class ClickGUI extends Module {

    public static ListValue mode = new ListValue("Mode", new String[]{"Miscible", "LiquidSense"}, "LiquidSense");

    @Override
    public void onEnable() {
        if (mode.get().equalsIgnoreCase("Miscible")) {
            mc.displayGuiScreen(LiquidBounce.miscible);
        } else {
            mc.displayGuiScreen(LiquidBounce.neverlose);
        }
    }
}
