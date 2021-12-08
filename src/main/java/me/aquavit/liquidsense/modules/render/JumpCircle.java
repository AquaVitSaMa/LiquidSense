package me.aquavit.liquidsense.modules.render;

import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.Render3DEvent;
import me.aquavit.liquidsense.utils.render.RenderUtils;
import me.aquavit.liquidsense.utils.render.shader.shaders.RainbowShader;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.Value;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "JumpCircle", description = "Draw a circle when u jump", category = ModuleCategory.RENDER)
public class JumpCircle extends Module {

    private final Value<Boolean> rainbow = new BoolValue("Rainbow", false);
    private final Value<Float> rainbowX = new FloatValue("Rainbow-X", -1000F, -2000F, 2000F).displayable(rainbow::get);
    private final Value<Float> rainbowY = new FloatValue("Rainbow-Y", -1000F, -2000F, 2000F).displayable(rainbow::get);
    private final FloatValue smoothLineValue = new FloatValue("SmoothLine", 6f, 1f, 10f);

    private boolean inAir;
    private List<BlockPos> pos = new ArrayList<BlockPos>();
    private List<BlockPos> lastTick = new ArrayList<BlockPos>();

    @Override
    public void onEnable(){
        pos.clear();
        lastTick.clear();
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (!mc.thePlayer.onGround)
            inAir = true;

        if (inAir && mc.thePlayer.onGround) {
            pos.add(new BlockPos(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ));
            lastTick.add(new BlockPos(
                    mc.thePlayer.lastTickPosX,
                    mc.thePlayer.lastTickPosY,
                    mc.thePlayer.lastTickPosZ));
            inAir = false;
        }

        for (int i = 0; i < pos.size(); i++) {

            drawCircle(pos.get(i), lastTick.get(i), 3f, event.getPartialTicks(), Color.white);

        }
    }

    private void drawCircle(BlockPos pos, BlockPos lastPos, float radius, float partialTicks, Color color) {
        double x = lastPos.getX() + (pos.getX() - lastPos.getX()) * partialTicks - mc.getRenderManager().renderPosX;
        double y = lastPos.getY() + (pos.getY() - lastPos.getY()) * partialTicks - mc.getRenderManager().renderPosY;
        double z = lastPos.getZ() + (pos.getZ() - lastPos.getZ()) * partialTicks - mc.getRenderManager().renderPosZ;
        Cylinder cylinder = new Cylinder();
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glRotatef(-90f, 1f, 0f, 0f);
        cylinder.setDrawStyle(100011);

        RenderUtils.glColor(0, 0, 0, 120);

        if (rainbow.get()) {
            RainbowShader.INSTANCE.setStrengthX(rainbowX.get() == 0.0f ? 0.0f : 1.0f / rainbowX.get());
            RainbowShader.INSTANCE.setStrengthY(rainbowY.get() == 0.0f ? 0.0f : 1.0f / rainbowY.get());
            RainbowShader.INSTANCE.setOffset((System.currentTimeMillis() % 10000) / 10000.0f);
            RainbowShader.INSTANCE.startShader();
        }
        RenderUtils.glColor(color);
        RenderUtils.enableSmoothLine(smoothLineValue.get());
        cylinder.draw(radius, radius, 0.0f, 64, 2);
        if (rainbow.get()) RainbowShader.INSTANCE.stopShader();

        RenderUtils.disableSmoothLine();
        GL11.glPopMatrix();
    }
}
