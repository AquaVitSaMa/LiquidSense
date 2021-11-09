package me.aquavit.liquidsense.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.neverlose.Main;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "NeverLose", description = "Opens the ClickGUI.", category = ModuleCategory.RENDER, keyBind = Keyboard.KEY_INSERT, canEnable = false)
public class NeverLose extends Module {
    @Override
    public void onEnable(){
        mc.displayGuiScreen(new Main());
    }
}
