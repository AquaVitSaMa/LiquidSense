package me.aquavit.liquidsense.modules.player;

import me.aquavit.liquidsense.modules.exploit.Ghost;
import net.ccbluex.liquidbounce.LiquidBounce;
import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.minecraft.client.gui.GuiGameOver;

@ModuleInfo(name = "AutoSpawn", description = "Automatically respawns you after dying.", category = ModuleCategory.PLAYER)
public class AutoSpawn extends Module {
    private BoolValue instantValue = new BoolValue("Instant", true);

    @EventTarget
    public void onUpdate(UpdateEvent event){
        if (LiquidBounce.moduleManager.getModule(Ghost.class).getState())
            return;
        if (instantValue.get() ? mc.thePlayer.getHealth() == 0F || mc.thePlayer.isDead : mc.currentScreen instanceof GuiGameOver && (((GuiGameOver)mc.currentScreen).enableButtonsTimer) >= 20) {
            mc.thePlayer.respawnPlayer();
            mc.displayGuiScreen(null);
        }
    }
}
