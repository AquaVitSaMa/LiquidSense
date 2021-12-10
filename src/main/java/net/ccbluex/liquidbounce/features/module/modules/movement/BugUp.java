package net.ccbluex.liquidbounce.features.module.modules.movement;

import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.PreUpdateEvent;
import me.aquavit.liquidsense.event.events.UpdateEvent;
import me.aquavit.liquidsense.utils.block.BlockUtils;
import me.aquavit.liquidsense.utils.entity.FallingPlayer;
import me.aquavit.liquidsense.utils.entity.MovementUtils;
import me.aquavit.liquidsense.utils.mc.VoidCheck;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.extend.ColorType;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.extend.Notification;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.Random;

@ModuleInfo(name = "BugUp", description = "automatically setbacks you after falling a certain distance.", category = ModuleCategory.MOVEMENT)
public class BugUp extends Module {
    private final ListValue mode = new ListValue("Mode", new String[]{"TeleportPBack", "GroundSpoof", "FlyFlag", "PacketFlag", "MotionTeleportFlag"}, "FlyFlag");
    private final BoolValue resetFallDist = new BoolValue("ResetFallDistance", true);
    private final BoolValue setGround = new BoolValue("SetGround", true);
    private final BoolValue voidOnly = new BoolValue("VoidOnly", true);
    private final Value<Integer> maxFallDist = new IntegerValue("MaxFallDistance", 20, 2, 255).displayable(() -> !voidOnly.get());
    private final FloatValue setBackDist = new FloatValue("SetBackDistance", 7f, 1f, 30f);

    private float lastFound;
    private double prevX, prevY, prevZ;
    private final Random random = new Random();

    @Override
    public void onDisable() {
        prevX = prevY = prevZ = 0.0;
    }

    @EventTarget
    public void onUpdate(UpdateEvent e) {
        BlockPos detectedLocation = null;
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        if (mc.thePlayer.onGround && !(BlockUtils.getBlock(new BlockPos(x, y - 1.0, z)) instanceof BlockAir)) {
            prevX = mc.thePlayer.prevPosX;
            prevY = mc.thePlayer.prevPosY;
            prevZ = mc.thePlayer.prevPosZ;
        }

        if (!mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater()) {
            final FallingPlayer fallingPlayer = new FallingPlayer(x, y, z,
                    mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ,
                    mc.thePlayer.rotationYaw, mc.thePlayer.moveStrafing, mc.thePlayer.moveForward
            );

            if (fallingPlayer.findCollision(60).getPos() != null)
                detectedLocation = fallingPlayer.findCollision(60).getPos();
        }

        if (detectedLocation != null) {
            if ((!voidOnly.get() && Math.abs(y - detectedLocation.getY()) + mc.thePlayer.fallDistance < maxFallDist.get()) || (voidOnly.get() && !VoidCheck.isBlockUnder())) {
                lastFound = mc.thePlayer.fallDistance;
            }
        }

        if (mc.thePlayer.fallDistance - lastFound > setBackDist.get()) {
            if (mode.get().equals("TeleportBack")) {
                mc.thePlayer.setPositionAndUpdate(prevX, prevY, prevZ);
                resetDist();
                mc.thePlayer.motionY = 0.0;
            }

            if (mode.get().equals("GroundSpoof")) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
            }

            if (mode.get().equals("FlyFlag")) {
                mc.thePlayer.motionY += 0.1;
                resetDist();
            }

            if (mode.get().equals("PacketFlag")) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + setBackDist.get() + 3.0 + MathHelper.getRandomDoubleInRange(random, 0.07, 0.09), z, setGround.get()));
                resetDist();
            }

            if (mode.get().equals("MotionTeleportFlag")) {
                mc.thePlayer.setPositionAndUpdate(x, y + 1f, z);
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, setGround.get()));
                mc.thePlayer.motionY = 0.1;

                MovementUtils.strafe();
                resetDist();
            }

            LiquidBounce.hud.addNotification(new Notification("AntiFall", "saved u from void", ColorType.SUCCESS, 250, 500));
        }
    }

    @EventTarget
    public void onPreUpdate(PreUpdateEvent event) {
        if (mc.thePlayer.fallDistance - lastFound > setBackDist.get()) {
            if (setGround.get())
                event.setOnGround(true);
        }
    }

    private void resetDist() {
        if (resetFallDist.get()) mc.thePlayer.fallDistance = 0;
    }
}
