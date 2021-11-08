package me.aquavit.liquidsense.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.combat.Aura;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@ModuleInfo(name = "KillESP", description = "KillESP", category = ModuleCategory.RENDER)
public class KillESP extends Module {
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Crystal", "Box", "Head", "Mark"}, "Crystal");
    private final IntegerValue colorRedValue = new IntegerValue("R", 0, 0, 255);
    private final IntegerValue colorGreenValue = new IntegerValue("G", 160, 0, 255);
    private final IntegerValue colorBlueValue = new IntegerValue("B", 255, 0, 255);
    private final IntegerValue alphaValue = new IntegerValue("Alpha", 255, 0, 255);
    private final BoolValue killLightningBoltValue = new BoolValue("LightningBolt", true);
    private final BoolValue rainbow = new BoolValue("RainBow", false);
    private final BoolValue hurt = new BoolValue("HurtTime", true);

    private HashMap<EntityLivingBase, Long> targetList = new HashMap<EntityLivingBase, Long>();
    private Aura aura = (Aura) LiquidBounce.moduleManager.getModule(Aura.class);
    Random random = new Random();

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (killLightningBoltValue.get()) {
            for (Map.Entry<EntityLivingBase, Long> entry : targetList.entrySet()) {
                if ((entry.getKey()).isDead || (entry.getKey()).getHealth() == 0F) {
                    if(entry.getValue() > System.currentTimeMillis()){
                        EntityLightningBolt ent = new EntityLightningBolt(mc.theWorld, (entry.getKey()).posX, (entry.getKey()).posY, (entry.getKey()).posZ);
                        mc.theWorld.addEntityToWorld(-1, ent);
                        mc.thePlayer.playSound("random.explode", 0.5f, 0.5f + random.nextFloat() * 0.2f);
                    }
                    targetList.remove(entry.getKey());
                }
            }
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (killLightningBoltValue.get()) {
            if (packet instanceof C02PacketUseEntity) {
                if (((C02PacketUseEntity) packet).getAction() == C02PacketUseEntity.Action.ATTACK) {
                    Entity entity = ((C02PacketUseEntity) packet).getEntityFromWorld(mc.theWorld);
                    if (entity instanceof EntityLivingBase) {
                        ((Map)targetList).put(entity, System.currentTimeMillis() + (long)3000);
                    }
                }
            }
        }
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        targetList.clear();
    }

    @EventTarget
    public void onRender3D(Render3DEvent event){
        Color color = rainbow.get() ? ColorUtils.rainbow() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), alphaValue.get());
        RenderManager renderManager = mc.getRenderManager();
        EntityLivingBase entityLivingBase = aura.getTarget();
        if (entityLivingBase == null) return;
        double pX = (entityLivingBase.lastTickPosX + (entityLivingBase.posX - entityLivingBase.lastTickPosX) * mc.timer.renderPartialTicks
                - renderManager.renderPosX);
        double pY = (entityLivingBase.lastTickPosY + (entityLivingBase.posY - entityLivingBase.lastTickPosY) * mc.timer.renderPartialTicks
                - renderManager.renderPosY);
        double pZ = (entityLivingBase.lastTickPosZ + (entityLivingBase.posZ - entityLivingBase.lastTickPosZ) * mc.timer.renderPartialTicks
                - renderManager.renderPosZ);
        switch (modeValue.get().toLowerCase()){
            case "crystal":
                RenderUtils.drawCrystal(entityLivingBase, pX, pY + 2.25, pZ,(hurt.get() && entityLivingBase.hurtTime > 3) ? new Color(255, 50, 50, 75).getRGB() : color.getRGB(),(hurt.get() && entityLivingBase.hurtTime > 3) ? new Color(255, 50, 50, 75).getRGB() : color.getRGB());
                break;
            case "box":
                RenderUtils.drawEntityBoxESP(entityLivingBase,(hurt.get() && entityLivingBase.hurtTime > 3) ? new Color(255, 50, 50, 75) : color);
                break;
            case "head":
                RenderUtils.drawPlatformESP(entityLivingBase,(hurt.get() && entityLivingBase.hurtTime > 3) ? new Color(255, 50, 50, 75) : color);
                break;
            case "mark":
                RenderUtils.drawPlatform(entityLivingBase,  (hurt.get() && entityLivingBase.hurtTime > 3) ? new Color(255, 50, 50, 75) : color);
                break;
        }

    }
}
