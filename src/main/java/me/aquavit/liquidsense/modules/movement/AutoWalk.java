package me.aquavit.liquidsense.modules.movement;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(name = "AutoWalk", description = "Automatically makes you walk.", category = ModuleCategory.MOVEMENT)
public class AutoWalk extends Module {

    @EventTarget
    public void onUpdate(UpdateEvent event){
        mc.gameSettings.keyBindForward.pressed = true;
    }

    @Override
    public void onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindForward))
            mc.gameSettings.keyBindForward.pressed = false;
    }
}
