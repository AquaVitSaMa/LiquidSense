package me.aquavit.liquidsense.modules.player;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.events.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

@ModuleInfo(name = "Zoot", description = "Removes all bad potion effects/fire.", category = ModuleCategory.PLAYER)
public class Zoot extends Module {

    private BoolValue badEffectsValue = new BoolValue("BadEffects", true);
    private BoolValue fireValue = new BoolValue("Fire", true);
    private BoolValue noAirValue = new BoolValue("NoAir", false);

    @EventTarget
    public void onUpdate(UpdateEvent event){
        if (noAirValue.get() && !mc.thePlayer.onGround) return;

        if (badEffectsValue.get())
            for (PotionEffect potion : mc.thePlayer.getActivePotionEffects())
                if (potion != null && hasBadEffect()) // TODO: Check current potion
                    for (int i=0; i< potion.getDuration() / 20; ++i)
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer());

        if (fireValue.get())
            if (!mc.thePlayer.capabilities.isCreativeMode && mc.thePlayer.isBurning())
                for (int i=0; i<= 9; i++)
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer());
    }

    private boolean hasBadEffect() {
        return mc.thePlayer.isPotionActive(Potion.hunger) || mc.thePlayer.isPotionActive(Potion.moveSlowdown)
                || mc.thePlayer.isPotionActive(Potion.digSlowdown) || mc.thePlayer.isPotionActive(Potion.harm)
                || mc.thePlayer.isPotionActive(Potion.confusion) || mc.thePlayer.isPotionActive(Potion.blindness)
                || mc.thePlayer.isPotionActive(Potion.weakness) || mc.thePlayer.isPotionActive(Potion.wither)
                || mc.thePlayer.isPotionActive(Potion.poison);
    }
}
