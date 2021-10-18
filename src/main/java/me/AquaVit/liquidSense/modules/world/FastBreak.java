package me.AquaVit.liquidSense.modules.world;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.world.Fucker;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "FastBreak", description = "Allows you to break blocks faster.", category = ModuleCategory.WORLD)
public class FastBreak extends Module {
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Normal", "SpeedMine"}, "SpeedMine");
    private FloatValue breakSpeed = new FloatValue("BreakSpeed", 1.4F, 0.1F, 2F);

    private boolean bzs = false;
    private float bzx = 0.0f;
    public BlockPos blockPos;
    public EnumFacing facing;

    @EventTarget
    public void onPacket(PacketEvent event) {
        final Packet<?> packet = event.getPacket();
        if (modeValue.get().equalsIgnoreCase("Normal")) return;
        if(packet instanceof C07PacketPlayerDigging && mc.playerController != null) {
            C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging)packet;
            if(c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                bzs = true;
                blockPos = c07PacketPlayerDigging.getPosition();
                facing = c07PacketPlayerDigging.getFacing();
                bzx = 0.0f;
            }
            else if(c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                bzs = false;
                blockPos = null;
                facing = null;
            }
        }

    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if(mc.playerController.extendedReach()) {
            mc.playerController.blockHitDelay = 0;
        }
        switch (modeValue.get()){
            case "Normal": {
                if (mc.playerController.curBlockDamageMP > breakSpeed.get())
                    mc.playerController.curBlockDamageMP = 1F;
                /*
                if (Fucker.currentDamage > breakDamage.get())
                    Fucker.currentDamage = 1F
                 */
                break;
            }

            case "SpeedMine": {
                if(bzs) {
                    Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                    bzx += (block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, blockPos) * breakSpeed.get());
                    if (bzx >= 1.0f) {
                        mc.theWorld.setBlockState(blockPos, Blocks.air.getDefaultState(), 11);
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, facing));
                        bzx = 0.0f;
                        bzs = false;
                    }
                }
                break;
            }

        }

    }

}
