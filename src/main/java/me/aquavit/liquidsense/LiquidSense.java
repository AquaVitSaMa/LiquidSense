package me.aquavit.liquidsense;

import me.aquavit.liquidsense.modules.exploit.*;
import me.aquavit.liquidsense.modules.misc.*;
import me.aquavit.liquidsense.modules.movement.*;
import me.aquavit.liquidsense.modules.combat.*;
import me.aquavit.liquidsense.modules.player.*;
import me.aquavit.liquidsense.modules.render.*;
import me.aquavit.liquidsense.modules.world.*;
import me.aquavit.liquidsense.modules.fun.*;
import net.ccbluex.liquidbounce.LiquidBounce;
import me.aquavit.liquidsense.utils.client.ClientUtils;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;
import java.util.List;

public class LiquidSense {

    private LiquidBounce liquidBounce;
    private List<Object> liquidSenseModules;

    public LiquidSense(LiquidBounce liquidBounce){
        this.liquidBounce = liquidBounce;
    }

    public void onStarting(){
        Display.setTitle(LiquidBounce.CLIENT_NAME + " | " +  LiquidBounce.CLIENT_VERSION + " | " + LiquidBounce.MINECRAFT_VERSION  + " | " + "By AquaVit" + " | Loading...");
        ClientUtils.getLogger().info(LiquidBounce.CLIENT_NAME +  " | " + "By AquaVit" + " | "+ " 正在启动");
        loadModules();
    }

    public void onStarted(){
        Display.setTitle(LiquidBounce.CLIENT_NAME + " | " + LiquidBounce.CLIENT_VERSION + " | " + LiquidBounce.MINECRAFT_VERSION +" | "+ "By AquaVit");
        ClientUtils.getLogger().info(LiquidBounce.CLIENT_NAME + " | " + "By AquaVit" + " | " + " 加载完成");
    }

    private void loadModules(){
        this.liquidSenseModules = new ArrayList<>();
        liquidSenseModules.add(SuperKnockback.class);
        liquidSenseModules.add(Teams.class);
        liquidSenseModules.add(Projectiles.class);
        liquidSenseModules.add(AbortBreaking.class);
        liquidSenseModules.add(SwingAnimation.class);
        liquidSenseModules.add(FastPlace.class);
        liquidSenseModules.add(Zoot.class);
        liquidSenseModules.add(Regen.class);
        liquidSenseModules.add(ConsoleSpammer.class);
        liquidSenseModules.add(Damage.class);
        liquidSenseModules.add(SkinDerp.class);
        liquidSenseModules.add(Derp.class);
        liquidSenseModules.add(Timer.class);
        liquidSenseModules.add(BlockOverlay.class);
        liquidSenseModules.add(Clip.class);
        liquidSenseModules.add(Ghost.class);
        liquidSenseModules.add(Tracers.class);
        liquidSenseModules.add(GhostHand.class);
        liquidSenseModules.add(Kick.class);
        liquidSenseModules.add(HYTBypass.class);
        liquidSenseModules.add(AntiAFK.class);
        liquidSenseModules.add(Reach.class);
        liquidSenseModules.add(NoClip.class);
        liquidSenseModules.add(NoJumpDelay.class);
        liquidSenseModules.add(NoWeb.class);
        liquidSenseModules.add(SafeWalk.class);
        liquidSenseModules.add(NoRotateSet.class);
        liquidSenseModules.add(ComponentOnHover.class);
        liquidSenseModules.add(AutoWeapon.class);
        liquidSenseModules.add(AntiBlind.class);
        liquidSenseModules.add(NoBob.class);
        liquidSenseModules.add(NoFOV.class);
        liquidSenseModules.add(NoHurtCam.class);
        liquidSenseModules.add(NoSwing.class);
        liquidSenseModules.add(TrueSight.class);
        liquidSenseModules.add(Chams.class);
        liquidSenseModules.add(Particle.class);
        liquidSenseModules.add(EveryThingBlock.class);
        liquidSenseModules.add(FastBreak.class);
        liquidSenseModules.add(ItemPhysic.class);
        liquidSenseModules.add(Ambience.class);
        liquidSenseModules.add(EnchantEffect.class);
        liquidSenseModules.add(AutoFish.class);
        liquidSenseModules.add(AutoWalk.class);
        liquidSenseModules.add(Eagle.class);
        liquidSenseModules.add(AutoSpawn.class);
        liquidSenseModules.add(HitBox.class);
        liquidSenseModules.add(Freeze.class);
        liquidSenseModules.add(Strafe.class);
        liquidSenseModules.add(NoFriends.class);
        liquidSenseModules.add(Animations.class);
        liquidSenseModules.add(ItemRotate.class);
        liquidSenseModules.add(LagBack.class);
        liquidSenseModules.add(MemoryFixer.class);
        liquidSenseModules.add(PointerESP.class);
        liquidSenseModules.add(Disabler.class);
        liquidSenseModules.add(Skeltal.class);
        liquidSenseModules.add(LightningCheck.class);
        liquidSenseModules.add(TargetStrafe.class);
        liquidSenseModules.add(Stair.class);
        liquidSenseModules.add(AutoADL.class);
        liquidSenseModules.add(CameraView.class);
        liquidSenseModules.add(KillESP.class);
        liquidSenseModules.add(Jesus.class);
        liquidSenseModules.add(Cape.class);
        liquidSenseModules.add(AutoApple.class);
        liquidSenseModules.add(LastestHypixelFly.class);
        liquidSenseModules.add(AntiSpam.class);
        liquidSenseModules.add(AutoTool.class);
    }

    public List<Object> getLiquidSenseModules() {
        return liquidSenseModules;
    }


    class TitleRunnable implements Runnable{

        @Override
        public void run() {
            System.out.println("run");
                Display.setTitle(LiquidBounce.CLIENT_NAME + " | " + LiquidBounce.CLIENT_VERSION + " | " + LiquidBounce.MINECRAFT_VERSION +" | "+ "By AquaVit");
        }
    }
}
