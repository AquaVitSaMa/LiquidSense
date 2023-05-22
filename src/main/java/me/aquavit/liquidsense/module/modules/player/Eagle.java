package me.aquavit.liquidsense.module.modules.player;

import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

@ModuleInfo(name = "Eagle", description = "Makes you eagle (aka. FastBridge).", category = ModuleCategory.PLAYER)
public class Eagle extends Module {
    @EventTarget
    public void onUpdate(UpdateEvent event){
        mc.gameSettings.keyBindSneak.pressed = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)).getBlock() == Blocks.air;
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null)
            return;

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false;
    }
}
