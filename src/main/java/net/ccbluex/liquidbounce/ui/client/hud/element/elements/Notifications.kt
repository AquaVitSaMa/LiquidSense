package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max
import kotlin.math.pow

/**
 * CustomHUD Notification element
 */
@ElementInfo(name = "Notifications", single = true)
class Notifications(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F, side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)): Element(x, y, scale, side) {

    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("Notification", "This is an example notification.", NotifyType.INFO)

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val notifications = mutableListOf<Notification>()
        for ((index, notify) in LiquidBounce.hud.notifications.withIndex()) {
            GL11.glPushMatrix()

            if (notify.drawNotification(index)) {
                notifications.add(notify)
            }

            GL11.glPopMatrix()
        }
        for (notify in notifications) {
            LiquidBounce.hud.notifications.remove(notify)
        }

        if (mc.currentScreen is GuiHudDesigner) {
            if (!LiquidBounce.hud.notifications.contains(exampleNotification))
                LiquidBounce.hud.addNotification(exampleNotification)

            exampleNotification.fadeState = FadeState.STAY
            exampleNotification.displayTime = System.currentTimeMillis()

            return Border(-exampleNotification.width.toFloat() - 21.5F, -exampleNotification.height.toFloat(), 0.5F, 0F)
        }

        return null
    }
}

class Notification(
        val title: String,
        val content: String,
        val type: NotifyType,
        val time: Int = 1500,
        val animeTime: Int = 500
) {
    val width = 100 .coerceAtLeast(Fonts.font18.getStringWidth(this.title).coerceAtLeast(Fonts.font18.getStringWidth(this.content)) + 10)
    val height = 30

    var fadeState = FadeState.IN
    var nowY = -height
    var displayTime = System.currentTimeMillis()
    var animeXTime = System.currentTimeMillis()
    var animeYTime = System.currentTimeMillis()

    fun easeInExpo(x: Double): Double {
        return if(x == 0.0){0.0}else{2.0.pow(10 * x - 10)}
    }

    fun easeOutExpo(x: Double): Double {
        return if(x == 1.0){1.0}else{1 - 2.0.pow(-10 * x)};
    }
    /**
     * Draw notification
     */
    fun drawNotification(index: Int): Boolean {
        val realY = -(index + 1) * height
        val nowTime = System.currentTimeMillis()
        val image = ResourceLocation("liquidbounce/notification/" + type.name + ".png")
        //Y-Axis Animation
        if (nowY != realY) {
            var pct = (nowTime - animeYTime) / animeTime.toDouble()
            if (pct > 1) {
                nowY = realY
                pct = 1.0
            } else {
                pct = easeOutExpo(pct)
            }
            GL11.glTranslated(0.0, (realY - nowY) * pct, 0.0)
        } else {
            animeYTime = nowTime
        }
        GL11.glTranslated(0.0, nowY.toDouble(), 0.0)

        //X-Axis Animation
        var pct = (nowTime - animeXTime) / animeTime.toDouble()
        when (fadeState) {
            FadeState.IN -> {
                if (pct > 1) {
                    fadeState = FadeState.STAY
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = easeOutExpo(pct)
            }

            FadeState.STAY -> {
                pct = 1.0
                if ((nowTime - animeXTime) > time) {
                    fadeState = FadeState.OUT
                    animeXTime = nowTime
                }
            }

            FadeState.OUT -> {
                if (pct > 1) {
                    fadeState = FadeState.END
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = 1 - easeInExpo(pct)
            }

            FadeState.END -> {
                return true
            }
        }
        GL11.glTranslated(width - (width * pct), 0.0, 0.0)
        GL11.glTranslatef(-width.toFloat(), 0F, 0F)

        RenderUtils.drawShader(-22F, 0F, width.toFloat() + 22, height.toFloat())
        RenderUtils.drawRect(-22F, 0F, width.toFloat(), height.toFloat(), type.renderColor)
        RenderUtils.drawRect(-22F, 0F, width.toFloat(), height.toFloat(), Color(0, 0, 0, 150))
        RenderUtils.drawRect(
                -22F,
                height - 2F,
                max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), -22F),
                height.toFloat(),
                type.renderColor
        )
        Fonts.font18.drawString(title, 7F, 6F, -1)
        Fonts.font16.drawString(content, 7F, 17F, -1)
        //RenderUtils.drawImage(ResourceLocation("liquidbounce/notification/INFO.png"), -19, 6, 16, 16)
        RenderUtils.drawImage(image, -19,  3, 22, 22)
        GlStateManager.resetColor()
        return false
    }
}

enum class NotifyType(var renderColor: Color) {
    SUCCESS(Color(0x60E066)),
    ERROR(Color(0xFF2F3A)),
    WARNING(Color(0xF5FD00)),
    INFO(Color(0x6490A7)),
    WTF(Color(0x6490A7));
}


enum class FadeState { IN, STAY, OUT, END }