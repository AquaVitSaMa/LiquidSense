package me.aquavit.liquidsense.modules.render;

import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.Render3DEvent;
import me.aquavit.liquidsense.event.events.UpdateEvent;
import me.aquavit.liquidsense.utils.JumpCircleUitl;
import me.aquavit.liquidsense.utils.render.RenderUtils;
import me.aquavit.liquidsense.utils.render.shader.shaders.RainbowShader;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.Value;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import scala.collection.script.Update;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "JumpCircle", description = "Draw a circle when u jump", category = ModuleCategory.RENDER)
public class JumpCircle extends Module {

	private final Value<Boolean> rainbow = new BoolValue("Rainbow", false);
	private final Value<Float> rainbowX = new FloatValue("Rainbow-X", -1000F, -2000F, 2000F).displayable(rainbow::get);
	private final Value<Float> rainbowY = new FloatValue("Rainbow-Y", -1000F, -2000F, 2000F).displayable(rainbow::get);
	private final FloatValue smoothLineValue = new FloatValue("SmoothLine", 6f, 1f, 10f);
	private final List<JumpCircleUitl> pos = new ArrayList<>();
	private boolean inAir;

	@Override
	public void onEnable() {
		pos.clear();
	}

	@EventTarget
	public void onUpdate(UpdateEvent event) {
		if (!mc.thePlayer.onGround)
			inAir = true;

		if (inAir && mc.thePlayer.onGround) {
			pos.add(new JumpCircleUitl(
					mc.thePlayer.posX,
					mc.thePlayer.posY,
					mc.thePlayer.posZ,
					mc.thePlayer.lastTickPosX,
					mc.thePlayer.lastTickPosY,
					mc.thePlayer.lastTickPosZ));
			inAir = false;
		}
	}

	@EventTarget
	public void onRender3D(Render3DEvent event) {
		int index = 0;
		while (pos.size() > index) {
			JumpCircleUitl the = pos.get(index);
			if (the.tick > 500)
				the.remove = true;
			if(the.remove && the.translate.getY() <= 1)
				pos.remove(the);
			index++;
		}

		for (JumpCircleUitl po : pos) {
			po.translate.translate(3f, (po.remove)? 0 : 254f, -2);
			if (po.translate.getX() > 0 && po.translate.getY() > 0) {
				drawCircle(po.posX, po.posY , po.posZ , po.lastTickPosX , po.lastTickPosY , po.lastTickPosZ, po.translate.getX(), mc.timer.renderPartialTicks, new Color(255, 255, 255,(int) po.translate.getY() ));
			}
			po.tick++;
		}
	}

	private void drawCircle(double posX1 , double posY1 , double posZ1,double lastTickPosX1 ,double lastTickPosY1 ,double lastTickPosZ1, float radius, float partialTicks, Color color) {
		double x = lastTickPosX1 + (posX1 - lastTickPosX1) * partialTicks - mc.getRenderManager().renderPosX;
		double y = lastTickPosY1 + (posY1 - lastTickPosY1) * partialTicks - mc.getRenderManager().renderPosY;
		double z = lastTickPosZ1 + (posZ1 - lastTickPosZ1) * partialTicks - mc.getRenderManager().renderPosZ;
		GL11.glDisable(3553);
		GL11.glEnable(2848);
		GL11.glEnable(2881);
		GL11.glEnable(2832);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glHint(3154, 4354);
		GL11.glHint(3155, 4354);
		GL11.glHint(3153, 4354);
		GL11.glDisable(2929);
		GL11.glDepthMask(false);
		GL11.glLineWidth(3f);
		GL11.glBegin(3);
		for (int i = 0; i < 360; i += 5) {
			RenderUtils.glColor(color);
			GL11.glVertex3d(x - Math.sin(i * Math.PI / 180F) * radius, y, z + Math.cos(i * Math.PI / 180F) * radius);
		}
		GL11.glEnd();
		GL11.glDepthMask(true);
		GL11.glEnable(2929);
		GL11.glDisable(2848);
		GL11.glDisable(2881);
		GL11.glEnable(2832);
		GL11.glEnable(3553);
	}
}
