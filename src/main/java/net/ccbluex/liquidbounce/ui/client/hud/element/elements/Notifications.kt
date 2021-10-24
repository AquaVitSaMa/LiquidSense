package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.AquaVit.liquidSense.utils.render.AnimationUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.FontValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color

@ElementInfo(name = "Notifications", single = true)
class Notifications(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F, side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {


    private val colorModeValue: ListValue = object : ListValue("Text-Color", arrayOf("Custom", "Rainbow", "Type"), "Custom") {
        override fun changeElement() {
            colorRedValue.isSupported = (get() == "Custom").also {
                colorGreenValue.isSupported = it; colorBlueValue.isSupported = it
            }
        }
    }

    private val colorRedValue = IntegerValue("Text-R", 150, 0, 255)
    private val colorGreenValue = IntegerValue("Text-G", 150, 0, 255)
    private val colorBlueValue = IntegerValue("Text-B", 150, 0, 255)


    private val rectColorModeValue: ListValue = object : ListValue("Rect-Color", arrayOf("Custom", "Rainbow", "Type"), "Rainbow") {
        override fun changeElement() {
            rectColorRedValue.isSupported = (get() == "Custom").also {
                rectColorGreenValue.isSupported = it;rectColorBlueValue.isSupported = it;rectColorBlueAlpha.isSupported = it
            }
        }
    }

    private val rectColorRedValue = IntegerValue("Rect-R", 255, 0, 255)
    private val rectColorGreenValue = IntegerValue("Rect-G", 255, 0, 255)
    private val rectColorBlueValue = IntegerValue("Rect-B", 255, 0, 255)
    private val rectColorBlueAlpha = IntegerValue("Rect-Alpha", 255, 0, 255)

    private val backgroundColorModeValue: ListValue = object : ListValue("Background-Color", arrayOf("Custom", "Rainbow"), "Custom") {
        override fun changeElement() {
            backgroundColorRedValue.isSupported = get().equals("Custom").also {
                backgroundColorGreenValue.isSupported = it
                backgroundColorBlueValue.isSupported = it
                backgroundColorAlphaValue.isSupported = it
            }
        }
    }

    private val backgroundColorRedValue = IntegerValue("Background-R", 0, 0, 255)
    private val backgroundColorGreenValue = IntegerValue("Background-G", 0, 0, 255)
    private val backgroundColorBlueValue = IntegerValue("Background-B", 0, 0, 255)
    private val backgroundColorAlphaValue = IntegerValue("Background-Alpha", 255, 0, 255)
    private val fontValue = FontValue("Font", Fonts.font40)

    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("Example Notification", 0f, Notification.Type.info)

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        if (LiquidBounce.hud.notifications.size > 0) {

            val colorMode = colorModeValue.get()
            val rectColorMode = rectColorModeValue.get()
            val backgroundColorMode = backgroundColorModeValue.get()


            LiquidBounce.hud.notifications = LiquidBounce.hud.notifications.filter { it.fadeState != Notification.FadeState.END }.toMutableList()
            LiquidBounce.hud.notifications.forEachIndexed { index, notification ->


                notification.backgroundColor = when {
                    backgroundColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow().rgb
                    else -> Color(backgroundColorRedValue.get(), backgroundColorGreenValue.get(), backgroundColorBlueValue.get(), backgroundColorAlphaValue.get()).rgb
                }

                notification.colorMode = when {
                    colorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow().rgb
                    colorMode.equals("Type", ignoreCase = true) -> notification.color
                    else -> Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), 1).rgb

                }

                notification.rectColorMode = when {
                    rectColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow().rgb
                    rectColorMode.equals("Type", ignoreCase = true) -> notification.color
                    else -> Color(rectColorRedValue.get(), rectColorGreenValue.get(), rectColorBlueValue.get(), rectColorBlueAlpha.get()).rgb
                }

                notification.font = fontValue
                notification.drawNotification()
                val posy = -29f * index

                if (notification.y < posy) {
                    notification.y += 0.15f * RenderUtils.deltaTime
                    posy.also {
                        notification.y = Math.min(it, notification.y)
                    }
                } else {
                    notification.y -= 0.8F * RenderUtils.deltaTime
                    posy.also {
                        notification.y = Math.max(notification.y, it)
                    }
                }
            }
        }

        if (mc.currentScreen is GuiHudDesigner) {
            if (!LiquidBounce.hud.notifications.contains(exampleNotification)) {
                for (i in 0..LiquidBounce.hud.notifications.lastIndex) {
                    LiquidBounce.hud.removeNotification(LiquidBounce.hud.notifications[0])
                }
                LiquidBounce.hud.addNotification(exampleNotification)
            }

            exampleNotification.fadeState = Notification.FadeState.STAY
            exampleNotification.x = exampleNotification.textLength + 8F

            return Border(-exampleNotification.x + 12 + exampleNotification.textLength, -29F, -exampleNotification.x - 35, 2F)
        }

        return null
    }

}

class Notification {

    lateinit var font: FontValue


    var backgroundColor = 0
    var colorMode = 0
    var rectColorMode = 0

    var message: String? = null
    var text: String? = null
    var timer: Float = 0f
    var type: Type = Type.None


    var message2: String? = null
    var text2: String? = null
    var timer2: Float = 0f
    var type2: Type = Type.None

    var switch = false

    var color: Int = 0
    var start = 0f

    constructor(Message: String, Timer: Float, T: Type) {
        this.message = Message
        this.timer = Timer
        this.type = T
    }

    constructor(Text: String, Message: String, Timer: Float) {
        this.message = Message
        this.timer = Timer
        this.text = Text
    }

    constructor(Text: String, Message: String, Timer: Float, T: Type) {
        this.message = Message
        this.timer = Timer
        this.text = Text
        this.type = T
    }

    constructor(Text: String, Message: String, Timer: Float, T: Type, Text2: String, Message2: String, Timer2: Float, T2: Type) {
        this.message = Message
        this.timer = Timer
        this.text = Text
        this.type = T

        this.message2 = Message2
        this.timer2 = Timer2
        this.text2 = Text2
        this.type2 = T2
    }

    constructor(Text: String, Message: String, Timer: Float, T: Type, Text2: String, Message2: String, switch: Boolean, T2: Type) {
        this.message = Message
        this.timer = Timer
        this.text = Text
        this.type = T

        this.message2 = Message2
        this.text2 = Text2
        this.type2 = T2
        this.switch = switch
    }

    var x = 0F
    var y = 0F
    var textLength = 0
    private var fadeStep = 0F

    private var stay = 0F

    var fadeState = FadeState.IN

    /**
     * Fade state for animation
     */
    enum class FadeState { IN, STAY, OUT, END }

    enum class Type { error, warning, success, info, None }

    /**
     * Draw notification
     */
    fun drawNotification() {

        if ((timer2 > stay || switch) && stay != 0f && message2 != null && text2 != null && type2 != Type.None) {
            this.message = message2
            this.text = text2
            this.type = type2
        }

        var hudIcon: ResourceLocation? = null
        val delta = RenderUtils.deltaTime
        val time = getTime(delta).toInt()

        when (type) {
            Type.info -> {
                //hudIcon = MinecraftInstance.classProvider.createResourceLocation("liquidbounce/icon/info.png")
                color = Color(0, 155, 255, time).rgb
            }
            Type.success -> {
                //hudIcon = MinecraftInstance.classProvider.createResourceLocation("liquidbounce/icon/success.png")
                color = Color(0, 255, 0, time).rgb
            }
            Type.warning -> {
                //hudIcon = MinecraftInstance.classProvider.createResourceLocation("liquidbounce/icon/warning.png")
                color = Color(255, 255, 0, time).rgb
            }
            Type.error -> {
                //hudIcon = MinecraftInstance.classProvider.createResourceLocation("liquidbounce/icon/error.png")
                color = Color(255, 25, 25, time).rgb
            }
        }

        textLength = Math.max(font.get().getStringWidth(message!!), if (text != null) font.get().getStringWidth(text!!) else 0)
        val width = textLength + 8F

        RenderUtils.drawRect(-x + 8 + textLength, y, -x - 32, y - 27F, backgroundColor)

        RenderUtils.drawRect(-x - 32f, y, -x - 35f, y - 27F, rectColorMode)

        if(!text.isNullOrEmpty())
            font.get().drawStringWithShadow(text!!, -x - 5, y - 23F, colorMode)
        if(!message.isNullOrEmpty())
            font.get().drawStringWithShadow(message!!, -x - 5, y - if (text != null) 12F else 18F, colorMode)

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        if (type != Type.None) {
//            RenderUtils.drawImage(hudIcon, (-x.toInt() - 31), y.toInt() - 25, 22, 22)
        }

        when (fadeState) {
            FadeState.IN -> {
                if (x < width) {
                    x = AnimationUtils.easeOut(fadeStep, width) * width
                    fadeStep += delta / 2F
                }
                if (x >= width) {
                    fadeState = FadeState.STAY
                    x = width
                    fadeStep = width
                }
                stay = timer
            }

            FadeState.STAY -> if (stay > 0) {
                stay -= delta
                RenderUtils.drawRect((-x + 8 + textLength), y, -x - 32, y - 1F, color)
                RenderUtils.drawRect((-x + 8 + textLength), y, -x - 32, y - 1F, Color(0, 0, 0, 100))
                RenderUtils.drawRect((-x + 8 + textLength), y, (-x - 32) / (timer / stay), y - 1F, color)
            } else fadeState = FadeState.OUT

            FadeState.OUT -> if (x > 0) {
                x = AnimationUtils.easeOut(fadeStep, width) * width
                fadeStep -= delta / 2F
            } else {
                fadeState = FadeState.END
            }
            FadeState.END -> {
                LiquidBounce.hud.removeNotification(this)
            }
        }
    }

    var up = false
    var down = false

    private fun getTime(delta: Int): Float {
        if (start <= 30f && !up) {
            down = false; up = true
        }
        if (start >= 255f && !down) {
            down = true; up = false
        }

        if (up) {
            start += 0.5f * delta
        }

        if (down) {
            start -= 0.5f * delta
        }
        start = start.coerceIn(30f, 255f)
        return start
    }
}

