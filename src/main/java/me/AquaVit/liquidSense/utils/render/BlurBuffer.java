package me.AquaVit.liquidSense.utils.render;

import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.timer.TimeUtils;
import net.minecraft.client.shader.Shader;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class BlurBuffer{
	private static ShaderGroup blurShader;
	private static Minecraft mc = Minecraft.getMinecraft();
	private static Framebuffer buffer;
	private static float lastScale;
	private static int lastScaleWidth;
	private static int lastScaleHeight;
	private static ResourceLocation shader = new ResourceLocation("shaders/post/blur.json");

	private static TimeUtils updateTimer = new TimeUtils();

	public static void initFboAndShader() {
		try {
			buffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
			buffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);

			blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), buffer, shader);
			blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void setShaderConfigs(float intensity, float blurWidth, float blurHeight){

		try {
			List<Shader> listShaders = (List<Shader>) blurShader.getClass().getField("listShaders").get(blurShader);

			listShaders.get(0).getShaderManager().getShaderUniform("Radius").set(intensity);
			listShaders.get(1).getShaderManager().getShaderUniform("Radius").set(intensity);
			listShaders.get(0).getShaderManager().getShaderUniform("BlurDir").set(blurWidth, blurHeight);
			listShaders.get(1).getShaderManager().getShaderUniform("BlurDir").set(blurHeight, blurWidth);
		} catch (Exception e) {
			e.printStackTrace();
		}




	}

	public static void blurArea(int x, int y, float width, float height, boolean setupOverlay) {
		ScaledResolution scale = new ScaledResolution(mc);
		float factor = scale.getScaleFactor();
		int factor2 = scale.getScaledWidth();
		int factor3 = scale.getScaledHeight();
		if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
			initFboAndShader();
		}
		lastScale = factor;
		lastScaleWidth = factor2;
		lastScaleHeight = factor3;

		// 渲染
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		RenderUtils.doGlScissor(x, y, (int)width, (int)height);
		GL11.glPushMatrix();
		buffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, true);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		if (setupOverlay) {
			mc.entityRenderer.setupOverlayRendering();
		}

		GlStateManager.enableDepth();
	}

	public static void updateBlurBuffer(float amount,boolean setupOverlay) {
		// 以60帧每秒的速度更新 FrameBuffer
		if (updateTimer.delay(1000 / 60f) && blurShader != null) {
			mc.getFramebuffer().unbindFramebuffer();

			setShaderConfigs(amount, 0f, 1f);
			buffer.bindFramebuffer(true);

			mc.getFramebuffer().framebufferRenderExt(mc.displayWidth, mc.displayHeight, true);

			if (OpenGlHelper.shadersSupported) {
				GlStateManager.matrixMode(5890);
				GlStateManager.pushMatrix();
				GlStateManager.loadIdentity();
				blurShader.loadShaderGroup(mc.timer.renderPartialTicks);
				GlStateManager.popMatrix();
			}

			buffer.unbindFramebuffer();
			mc.getFramebuffer().bindFramebuffer(true);

			if (setupOverlay) {
				mc.entityRenderer.setupOverlayRendering();
			}
		}
	}
}
