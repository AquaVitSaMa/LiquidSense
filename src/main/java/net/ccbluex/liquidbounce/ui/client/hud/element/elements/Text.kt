/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.aquavit.liquidsense.event.EventTarget
import me.aquavit.liquidsense.event.events.PacketEvent
import me.aquavit.liquidsense.utils.entity.EntityUtils
import me.aquavit.liquidsense.utils.misc.ServerUtils
import me.aquavit.liquidsense.utils.module.CPSCounter
import me.aquavit.liquidsense.utils.timer.MSTimer
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import me.aquavit.liquidsense.utils.render.ColorUtils
import me.aquavit.liquidsense.utils.render.RenderUtils
import me.aquavit.liquidsense.utils.render.shader.shaders.RainbowFontShader
import me.aquavit.liquidsense.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.network.play.server.S03PacketTimeUpdate
import net.minecraft.util.ChatAllowedCharacters
import net.minecraft.util.EnumChatFormatting
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * CustomHUD text element
 *
 * Allows to draw custom text
 */
@ElementInfo(name = "Text")
class Text(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F, side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.UP)) : Element(x, y, scale, side) {

    companion object {

        val DATE_FORMAT = SimpleDateFormat("MMddyy")
        val HOUR_FORMAT = SimpleDateFormat("HH:mm")
        val Y_FORMAT = DecimalFormat("0.00")
        val DECIMAL_FORMAT = DecimalFormat("0.00")

        /**
         * Create default element
         */
        fun defaultClient(): Text {
            val text = Text(x = 26.0, y = 26.0, scale = 0.5F)

            text.rectMode.set("Skeet")
            text.fontValue.set(Fonts.minecraftFont)
            text.setColor(Color(50, 175, 255))

            return text
        }

    }

    private var fontValue = FontValue("Font", Fonts.minecraftFont)
    private val displayString = TextValue("DisplayText", "")

    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)

    private val rainbow = BoolValue("Rainbow", false)
    private val rainbowX = FloatValue("Rainbow-X", -1000F, -2000F, 2000F)
    private val rainbowY = FloatValue("Rainbow-Y", -1000F, -2000F, 2000F)

    private val animation = BoolValue("Animation", false)
    private val animationDelay = FloatValue("AnimationDelay", 1F, 0.1F, 5F)

    private val shadow = BoolValue("Shadow", false)
    private val outline = BoolValue("Outline",false)
    private val rectMode = ListValue("RectMode", arrayOf("Custom", "OneTap", "Skeet","OnlyWhite","NeverLose"), "Custom")

    private var fps = BoolValue("FPS",true).displayable{rectMode.get() == "Skeet" || rectMode.get() == "NeverLose"}
    private var ping = BoolValue("Ping",true).displayable{rectMode.get() == "Skeet" || rectMode.get() == "NeverLose"}
    private var ip = BoolValue("IP",true).displayable{rectMode.get() == "Skeet" || rectMode.get() == "NeverLose"}
    private var tps = BoolValue("TPS",true).displayable{rectMode.get() == "Skeet" || rectMode.get() == "NeverLose"}
    private var username = BoolValue("Username",true).displayable{rectMode.get() == "Skeet" || rectMode.get() == "NeverLose"}
    private var time = BoolValue("Time",true).displayable{rectMode.get() == "Skeet" || rectMode.get() == "NeverLose"}


    private var editMode = false
    private var editTicks = 0
    private var prevClick = 0L
    private var lastTick = -1
    private var lastTPS = 20.0

    private var displaySpeed = 0.0
    private var displayText = display

    private val display: String
        get() {
            val textContent = when {
                rectMode.get() == "Skeet" -> {
                    "L%f%iquidSense" +
                            (if (fps.get()) " %7%|| [%f%%fps%FPS%7%]" else "") +
                            (if (ping.get()) " %7%|| [%f%%ping%ms%7%]" else "") +
                            (if (ip.get()) " %7%|| %serverip%" else "") +
                            (if (tps.get()) " %7%|| %f%%tps%Ticks" else "") +
                            (if (username.get()) " %7%|| %r%%username%" else "") +
                            (if (time.get()) " %7%|| (%f%%time%%7%)" else "")
                }
                rectMode.get() == "NeverLose" -> {
                    (if (fps.get()) "%7%[%f%%fps%FPS%7%]" else "") +
                            (if (ping.get()) " %7%[%f%%ping%ms%7%]" else "") +
                            (if (ip.get()) " %7%%serverip%" else "") +
                            (if (tps.get()) " %7%%f%%tps%Ticks" else "") +
                            (if (username.get()) " %r%%username%" else "") +
                            (if (time.get()) " %7%(%f%%time%%7%)" else "")
                }
                displayString.get().isEmpty() && !editMode -> "Text Element"
                else -> displayString.get()
            }

            return multiReplace(textContent)
        }

    private fun getReplacement(str: String): String? {
        val thePlayer = mc.thePlayer ?: return null

        when (str) {
            "x" -> return DECIMAL_FORMAT.format(thePlayer.posX)
            "y" -> return Y_FORMAT.format(thePlayer.posY)
            "z" -> return DECIMAL_FORMAT.format(thePlayer.posZ)
            "xdp" -> return thePlayer.posX.toString()
            "ydp" -> return thePlayer.posY.toString()
            "zdp" -> return thePlayer.posZ.toString()
            "velocity" -> return DECIMAL_FORMAT.format(sqrt(thePlayer.motionX * thePlayer.motionX + thePlayer.motionZ * thePlayer.motionZ))
            "ping" -> return EntityUtils.getPing(thePlayer).toString()
            "speed" -> return String.format("%.2f", displaySpeed)

            "0" -> return EnumChatFormatting.BLACK.toString()
            "1" -> return EnumChatFormatting.DARK_BLUE.toString()
            "2" -> return EnumChatFormatting.DARK_GREEN.toString()
            "3" -> return EnumChatFormatting.DARK_AQUA.toString()
            "4" -> return EnumChatFormatting.DARK_RED.toString()
            "5" -> return EnumChatFormatting.DARK_PURPLE.toString()
            "6" -> return EnumChatFormatting.GOLD.toString()
            "7" -> return EnumChatFormatting.GRAY.toString()
            "8" -> return EnumChatFormatting.DARK_GRAY.toString()
            "9" -> return EnumChatFormatting.BLUE.toString()

            "a" -> return EnumChatFormatting.GREEN.toString()
            "b" -> return EnumChatFormatting.AQUA.toString()
            "c" -> return EnumChatFormatting.RED.toString()
            "d" -> return EnumChatFormatting.LIGHT_PURPLE.toString()
            "e" -> return EnumChatFormatting.YELLOW.toString()
            "f" -> return EnumChatFormatting.WHITE.toString()
            "n" -> return EnumChatFormatting.UNDERLINE.toString()
            "m" -> return EnumChatFormatting.STRIKETHROUGH.toString()
            "l" -> return EnumChatFormatting.BOLD.toString()
            "k" -> return EnumChatFormatting.OBFUSCATED.toString()
            "o" -> return EnumChatFormatting.ITALIC.toString()
            "r" -> return EnumChatFormatting.RESET.toString()
        }

        return when (str) {
            "username" -> mc.getSession().username
            "clientname" -> LiquidBounce.CLIENT_NAME
            "clientversion" -> "b${LiquidBounce.CLIENT_VERSION}"
            "clientcreator" -> LiquidBounce.CLIENT_CREATOR
            "fps" -> Minecraft.getDebugFPS().toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "tps" -> return DecimalFormat("0.0").format((lastTPS * 10.0).roundToInt() / 10.0)
            "serverip" -> ServerUtils.getRemoteIp()
            "cps", "lcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.LEFT).toString()
            "mcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.MIDDLE).toString()
            "rcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.RIGHT).toString()

            else -> null // Null = don't replace
        }
    }

    @EventTarget
    fun onPacket(modPacket: PacketEvent) {
        val times = ArrayList<Long>()
        val tpsTimer = TimeUtils()

        if (modPacket.packet is S03PacketTimeUpdate) {
            times.add(max(1000, tpsTimer.currentMS))
            var timesAdded: Long = 0
            if (times.size > 5) {
                times.removeAt(0)
            }
            for (l in times) {
                timesAdded += l
            }
            val roundedTps = timesAdded / times.size
            lastTPS = 20.0 / roundedTps * 1000.0
            tpsTimer.reset()
        }
    }

    private fun multiReplace(str: String): String {
        var lastPercent = -1
        val result = StringBuilder()

        for (i in str.indices) {
            if (str[i] == '%') {
                if (lastPercent != -1) {
                    if (lastPercent + 1 != i) {
                        val replacement = getReplacement(str.substring(lastPercent + 1, i))

                        if (replacement != null) {
                            result.append(replacement)
                            lastPercent = -1
                            continue
                        }
                    }
                    result.append(str, lastPercent, i)
                }
                lastPercent = i
            } else if (lastPercent == -1) {
                result.append(str[i])
            }
        }

        if (lastPercent != -1) {
            result.append(str, lastPercent, str.length)
        }

        return result.toString()
    }

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        val thePlayer = mc.thePlayer ?: return null

        if (lastTick != thePlayer.ticksExisted) {
            lastTick = thePlayer.ticksExisted
            val xDist = thePlayer.posX - thePlayer.prevPosX
            val zDist = thePlayer.posZ - thePlayer.prevPosZ
            var lastDist = sqrt(xDist * xDist + zDist * zDist)

            if (lastDist < 0)
                lastDist = -lastDist

            displaySpeed = lastDist * 20
        }

        val color = Color(redValue.get(), greenValue.get(), blueValue.get()).rgb
        val colord = Color(redValue.get(), greenValue.get(), blueValue.get()).rgb + Color(0,0,0,50).rgb
        val fontRenderer = fontValue.get()

        when (this.rectMode.get().toLowerCase()) {
            "custom" -> {

            }
            "onetap" -> {
                RenderUtils.drawRect(-4.0f, -8.0f, (fontRenderer.getStringWidth(displayText) + 3).toFloat(), fontRenderer.FONT_HEIGHT.toFloat(), Color(43,43,43).rgb)
                RenderUtils.drawGradientSideways(-3.0, -7.0, (fontRenderer.getStringWidth(displayText) + 2.0), -3.0,
                    if (rainbow.get()) ColorUtils.rainbow(400000000L).rgb + Color(0,0,0,40).rgb else colord,
                    if (rainbow.get()) ColorUtils.rainbow(400000000L).rgb else color)
            }
            "skeet" -> {
                RenderUtils.drawRect(-11.0, -9.5, (fontRenderer.getStringWidth(displayText) + 9).toDouble(), fontRenderer.FONT_HEIGHT.toDouble() + 6,Color(0,0,0).rgb)
                RenderUtils.skeetRect(-10.0, -8.5, (fontRenderer.getStringWidth(displayText) + 8).toDouble(), fontRenderer.FONT_HEIGHT.toDouble() + 5,8.0, Color(59,59,59).rgb,Color(59,59,59).rgb)
                RenderUtils.skeetRect(-9.0, -7.5, (fontRenderer.getStringWidth(displayText) + 7).toDouble(), fontRenderer.FONT_HEIGHT.toDouble() + 4,4.0, Color(59,59,59).rgb,Color(40,40,40).rgb)
                RenderUtils.skeetRect(-4.0, -3.0, (fontRenderer.getStringWidth(displayText) + 2).toDouble(), fontRenderer.FONT_HEIGHT.toDouble() + 0,1.0, Color(18,18,18).rgb,Color(0,0,0).rgb)
            }
            "onlywhite" ->{
                RenderUtils.drawRect(-2f,-2f,(fontRenderer.getStringWidth(displayText)+1).toFloat(),fontRenderer.FONT_HEIGHT.toFloat(),Color(0,0,0,150).rgb)

            }
            "neverlose" -> {
                var index = "LS"
                val list = displayText.split(" ")

                RenderUtils.drawRoundedRect(-5f, -4F, Fonts.font20.getStringWidth(index + list.toString()).toFloat() + 5f, 5f + Fonts.font20.FONT_HEIGHT, 2f, Color(16, 25, 32).rgb, 1f, Color(16, 25, 32).rgb)
                //RenderUtils.drawBorderedRect(-5.5F, -3.5F, (Fonts.font20.getStringWidth("NL    $displayText").toFloat() + 8.5F), Fonts.font20.FONT_HEIGHT.toFloat() + 0.5F, 3F, Color(16, 25, 32).rgb, Color(16, 25, 32).rgb)

                Fonts.font20.drawString("LS", 0F, 0.6F, Color(50, 175, 255, 120).rgb, false)
                Fonts.font20.drawString("LS", 0F, -0.6F, Color(50, 175, 255, 120).rgb, false)
                Fonts.font20.drawString("LS", -0.75F, -0.6F, Color(50, 175, 255, 120).rgb, false)
                Fonts.font20.drawString("LS", -0.75F, 0.6F, Color(50, 175, 255, 120).rgb, false)
                Fonts.font20.drawString("LS", 0F, 0F, Color(255, 255, 255, 200).rgb, false)


                for ((count, text: String) in list.withIndex()) {
                    Fonts.font20.drawString(text, Fonts.font20.getStringWidth(index).toFloat() + 5f, 0F, Color(155, 155, 155).rgb, false)
                    if (count + 1 == list.size)
                        break
                    Fonts.font20.drawString("|", Fonts.font20.getStringWidth(index + text).toFloat() + 1.5F, 0F, Color(6, 32, 55).rgb, false)
                    index += "$text   "
                }
            }

        }

        if(this.outline.get() && rectMode.get() != "NeverLose"){
            GlStateManager.resetColor()
            fontRenderer.drawString(displayText, (fontRenderer.getStringWidth(displayText) - fontRenderer.getStringWidth(displayText) - 1.0f).toInt(), fontRenderer.FONT_HEIGHT - fontRenderer.FONT_HEIGHT, Color.BLACK.rgb)
            fontRenderer.drawString(displayText, (fontRenderer.getStringWidth(displayText) - fontRenderer.getStringWidth(displayText) + 1.0f).toInt(), fontRenderer.FONT_HEIGHT - fontRenderer.FONT_HEIGHT, Color.BLACK.rgb)
            fontRenderer.drawString(displayText, fontRenderer.getStringWidth(displayText) - fontRenderer.getStringWidth(displayText), (fontRenderer.FONT_HEIGHT - fontRenderer.FONT_HEIGHT + 1.0f).toInt(), Color.BLACK.rgb)
            fontRenderer.drawString(displayText, fontRenderer.getStringWidth(displayText) - fontRenderer.getStringWidth(displayText), (fontRenderer.FONT_HEIGHT - fontRenderer.FONT_HEIGHT - 1.0f).toInt(), Color.BLACK.rgb)
            fontRenderer.drawString(displayText, fontRenderer.getStringWidth(displayText) - fontRenderer.getStringWidth(displayText), fontRenderer.FONT_HEIGHT - fontRenderer.FONT_HEIGHT, 0)
        }

        val rainbow = rainbow.get()
        RainbowFontShader.begin(rainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
            if (rectMode.get() != "NeverLose")
                fontRenderer.drawString(displayText, 0F, 0F, if (rainbow)
                0 else color, shadow.get())

            if (editMode && mc.currentScreen is GuiHudDesigner && editTicks <= 40) {
                fontRenderer.drawString("_", fontRenderer.getStringWidth(displayText) + if (rectMode.get() == "NeverLose") 5F else 2F, 0F, if (rainbow) ColorUtils.rainbow(400000000L).rgb else color, shadow.get())
            }
        }

        if (editMode && mc.currentScreen !is GuiHudDesigner) {
            editMode = false
            updateElement()
        }

        return Border(
                -2F,
                -2F,
                fontRenderer.getStringWidth(displayText) + 2F,
                fontRenderer.FONT_HEIGHT.toFloat()
        )
    }

    private var count = 0
    private var reverse = false

    private var timer = MSTimer()

    override fun updateElement() {

        editTicks += 5
        if (editTicks > 80) editTicks = 0

        if (count < 0 || count > display.length) {
            count = 0
            reverse = false
        }

        if (!editMode && animation.get() && rectMode.get() != "NeverLose" && timer.hasTimePassed((animationDelay.get() * 1000).toLong())) {
            if (reverse) count -= 1 else count += 1
            if (count == display.length || count == 0) reverse = !reverse
            timer.reset()
        }

        val tempDisplayText = if (animation.get() && rectMode.get() != "NeverLose") display.substring(0, count) else display

        displayText = if (editMode) displayString.get() else tempDisplayText

    }

    override fun handleMouseClick(x: Double, y: Double, mouseButton: Int) {
        if (isInBorder(x, y) && mouseButton == 0) {
            if (System.currentTimeMillis() - prevClick <= 250L)
                editMode = true

            prevClick = System.currentTimeMillis()
        } else {
            editMode = false
        }
    }

    override fun handleKey(c: Char, keyCode: Int) {
        if (editMode && mc.currentScreen is GuiHudDesigner) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (displayString.get().isNotEmpty())
                    displayString.set(displayString.get().substring(0, displayString.get().length - 1))

                updateElement()
                return
            }

            if (ChatAllowedCharacters.isAllowedCharacter(c) || c == '§')
                displayString.set(displayString.get() + c)

            updateElement()
        }
    }

    fun setColor(c: Color): Text {
        redValue.set(c.red)
        greenValue.set(c.green)
        blueValue.set(c.blue)
        return this
    }
}