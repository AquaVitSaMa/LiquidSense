/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import com.google.gson.JsonElement
import me.aquavit.liquidsense.utils.render.shader.shaders.RainbowFontShader
import me.aquavit.liquidsense.utils.render.shader.shaders.RainbowShader
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.*
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.client.hud.element.Side.*
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.*
import net.ccbluex.liquidbounce.utils.render.Replacement.multiReplace
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color
import kotlin.math.abs
import kotlin.math.sin

/**
 * CustomHUD Arraylist element
 *
 * Shows a list of enabled modules
 */
@ElementInfo(name = "Arraylist")
class  Arraylist(x: Double = 0.0, y: Double = 5.0, scale: Float = 1F, side: Side = Side(Horizontal.RIGHT, Vertical.UP)) : Element(x, y, scale, side) {

    private val alphaSort = BoolValue("AlphaSort", false)
    private val animationXspeedValue = IntegerValue("ToggleSpeed", 50, 10, 100)
    private val lowerCaseValue = BoolValue("LowerCase", true)
    private val namebreak: BoolValue = object : BoolValue("NameBreak", false) {
        override fun onChanged(oldValue: Boolean, newValue: Boolean) {
            if (newValue)
                LiquidBounce.moduleManager.modules.forEach { it.arrayListName = getBreakName(it.name) }
            else
                LiquidBounce.moduleManager.modules.forEach { it.arrayListName = it.name }
        }

        override fun fromJson(element: JsonElement) {
            super.fromJson(element)
            onChanged(value, value)
        }
    }
    private val shadowValue = BoolValue("Shadow",true)
    private val spaceValue = FloatValue("Space", 0F, 0F, 5F)

    private val tags = BoolValue("Tags", true)
    private val tagsMode = ListValue("Tags-Mode", arrayOf("Space","[]", "()", "<>", "-", "Bold", "White"), "Space").displayable { tags.get() }
    private val tagsArrayColor = BoolValue("TagsArrayColor", false).displayable { tags.get() }
    private val textHeightValue = IntegerValue("TextHeight", 12, 1, 20)
    private val textXValue = IntegerValue("TextX", 2, -5, 10)
    private val textYValue = IntegerValue("TextY", 2, 0, 5)

    private val rainbowX = FloatValue("ShaderRainbow-X", 750F, -2000F, 2000F).displayable { colorModeValue.get() == "ShaderRainbow" || rectColorModeValue.get() == "ShaderRainbow" || backgroundColorModeValue.get() == "ShaderRainbow" }
    private val rainbowY = FloatValue("ShaderRainbow-Y", -500F, -2000F, 2000F).displayable { colorModeValue.get() == "ShaderRainbow" || rectColorModeValue.get() == "ShaderRainbow" || backgroundColorModeValue.get() == "ShaderRainbow" }

    private val colorModeValue = ListValue("Text-Color", arrayOf("Custom","Random","Rainbow","ShaderRainbow","OneRainbow","Astolfo","Exhibition"), "OneRainbow")
    private val colorRedValue = IntegerValue("Text-R", 100, 0, 255).displayable { colorModeValue.get() == "Custom" }
    private val colorGreenValue = IntegerValue("Text-G", 100, 0, 255).displayable { colorModeValue.get() == "Custom" }
    private val colorBlueValue = IntegerValue("Text-B", 200, 0, 255).displayable { colorModeValue.get() == "Custom" }
    private val colorAlphaValue = IntegerValue("Text-A", 255, 0, 255).displayable { colorModeValue.get() == "Custom" || colorModeValue.get() == "Rainbow"  || colorModeValue.get() == "OneRainbow" }

    private val rectTopValue = BoolValue("TobRect", false)
    private val rectValue = ListValue("Rect-Mode", arrayOf("None", "Left", "ShortLeft" ,"Right","OutLine"), "Right")
    private val rectWidthValue = IntegerValue("Rect-Width", 0, -1, 5)
    private val rectColorModeValue = ListValue("Rect-Color", arrayOf("Custom", "Random", "Rainbow","ShaderRainbow","OneRainbow","Astolfo","Exhibition"), "Alpha")
    private val rectColorRedValue = IntegerValue("Rect-R", 255, 0, 255).displayable { rectColorModeValue.get() == "Custom" }
    private val rectColorGreenValue = IntegerValue("Rect-G", 255, 0, 255).displayable { rectColorModeValue.get() == "Custom" }
    private val rectColorBlueValue = IntegerValue("Rect-B", 255, 0, 255).displayable { rectColorModeValue.get() == "Custom" }
    private val rectColorAlpha = IntegerValue("Rect-A", 255, 0, 255).displayable { rectColorModeValue.get() == "Custom" || rectColorModeValue.get() == "Rainbow" || rectColorModeValue.get() == "OneRainbow" }

    private val backgroundColorModeValue = ListValue("Background-Color", arrayOf("Custom", "Random", "Rainbow","ShaderRainbow","OneRainbow","Astolfo","Exhibition"), "Custom")
    private val backgroundWidthValue = IntegerValue("Background-Width", 2, -2, 10)
    private val backgroundColorRedValue = IntegerValue("Background-R", 0, 0, 255).displayable { backgroundColorModeValue.get() == "Custom" }
    private val backgroundColorGreenValue = IntegerValue("Background-G", 0, 0, 255).displayable { backgroundColorModeValue.get() == "Custom" }
    private val backgroundColorBlueValue = IntegerValue("Background-B", 0, 0, 255).displayable { backgroundColorModeValue.get() == "Custom" }
    private val backgroundColorAlphaValue = IntegerValue("Background-A", 50, 0, 255).displayable { backgroundColorModeValue.get() == "Custom" || backgroundColorModeValue.get() == "Rainbow" || backgroundColorModeValue.get() == "OneRainbow"}

    private val saturationValue = FloatValue("Saturation", 0.35f, 0f, 1f).displayable { colorModeValue.get() == "OneRainbow" || colorModeValue.get() == "Random" || colorModeValue.get() == "Astolfo" || rectColorModeValue.get() == "OneRainbow" || rectColorModeValue.get() == "Random" || rectColorModeValue.get() == "Astolfo" || backgroundColorModeValue.get() == "OneRainbow" || backgroundColorModeValue.get() == "Random" || backgroundColorModeValue.get() == "Astolfo" }
    private val brightnessValue = FloatValue("Brightness", 1.0f, 0f, 1f).displayable { colorModeValue.get() == "Random" || colorModeValue.get() == "Astolfo" || rectColorModeValue.get() == "Random" || rectColorModeValue.get() == "Astolfo" || backgroundColorModeValue.get() == "Random" || backgroundColorModeValue.get() == "Astolfo" }
    private val rainbowOffsetValue = IntegerValue("RainbowOffset", 40, 10, 100).displayable { colorModeValue.get() == "Rainbow" || rectColorModeValue.get() == "Rainbow" || backgroundColorModeValue.get() == "Rainbow" }

    private val oneSpeed = IntegerValue("One-Speed", 6, 0, 20).displayable { colorModeValue.get() == "OneRainbow" || rectColorModeValue.get() == "OneRainbow" || backgroundColorModeValue.get() == "OneRainbow" }
    private val oneWidth = IntegerValue("One-Width", 2, 0, 5).displayable { colorModeValue.get() == "OneRainbow" || rectColorModeValue.get() == "OneRainbow" || backgroundColorModeValue.get() == "OneRainbow" }
    private val oneOffset = FloatValue("One-Offset", 2f, 1f, 5f).displayable { colorModeValue.get() == "OneRainbow" || rectColorModeValue.get() == "OneRainbow" || backgroundColorModeValue.get() == "OneRainbow" }

    private val oneRedValue = IntegerValue("One-R", 2, 0, 20).displayable { colorModeValue.get() == "OneRainbow" || rectColorModeValue.get() == "OneRainbow" || backgroundColorModeValue.get() == "OneRainbow" }
    private val oneGreenValue = IntegerValue("One-G", 8, 0, 20).displayable { colorModeValue.get() == "OneRainbow" || rectColorModeValue.get() == "OneRainbow" || backgroundColorModeValue.get() == "OneRainbow" }
    private val oneBlueValue = IntegerValue("One-B", 8, 0, 20).displayable { colorModeValue.get() == "OneRainbow" || rectColorModeValue.get() == "OneRainbow" || backgroundColorModeValue.get() == "OneRainbow" }

    private val motionRedValue = IntegerValue("Motion-R", 128, 0, 255).displayable { colorModeValue.get() == "OneRainbow" || rectColorModeValue.get() == "OneRainbow" || backgroundColorModeValue.get() == "OneRainbow" }
    private val motionGreenValue = IntegerValue("Motion-G", 40, 0, 255).displayable { colorModeValue.get() == "OneRainbow" || rectColorModeValue.get() == "OneRainbow" || backgroundColorModeValue.get() == "OneRainbow" }
    private val motionBlueValue = IntegerValue("Motion-B", 40, 0, 255).displayable { colorModeValue.get() == "OneRainbow" || rectColorModeValue.get() == "OneRainbow" || backgroundColorModeValue.get() == "OneRainbow" }

    private val fontValue = FontValue("Font", Fonts.minecraftFont)

    private var x2 = 0
    private var y2 = 0F
    private var modules = emptyList<Module>()

    override fun drawElement(): Border? {
        val fontRenderer = fontValue.get()

        // Slide animation - update every render
        val delta = RenderUtils.deltaTime

        for (module in LiquidBounce.moduleManager.modules) {
            if (!module.array || (!module.state && module.slide == 2F)) continue

            var displayString = multiReplace(when {
                tagsArrayColor.get() -> module.colorlessTagName
                tags.get() && tagsMode.get() == "Space" -> module.tagName
                tags.get() && tagsMode.get() == "[]" -> module.tagName2
                tags.get() && tagsMode.get() == "()" -> module.tagName3
                tags.get() && tagsMode.get() == "<>" -> module.tagName4
                tags.get() && tagsMode.get() == "-" -> module.tagName5
                tags.get() && tagsMode.get() == "Bold" -> module.tagName6
                tags.get() && tagsMode.get() == "White" -> module.tagName7
                else -> module.arrayListName
            })

            if (lowerCaseValue.get())
                displayString = displayString.toLowerCase()

            val width = fontRenderer.getStringWidth(displayString)
            val deltaX = abs(width - module.slide)

            if (module.state) {
                if (module.slide < width) {
                    module.slide += abs((width - module.slideStep) / animationXspeedValue.get()) * delta
                    module.slideStep = width - deltaX
                }
            } else if (module.slide > 0) {
                module.slide -= abs((width - module.slideStep) / animationXspeedValue.get()) * delta
                module.slideStep = abs(-1 + deltaX)
            }

            module.slide = module.slide.coerceIn(0F, width.toFloat())
            module.slideStep = module.slideStep.coerceIn(0F, width.toFloat())
        }

        // Draw arraylist
        val colorMode = colorModeValue.get()
        val rectColorMode = rectColorModeValue.get()
        val backgroundColorMode = backgroundColorModeValue.get()
        val customColor = Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), colorAlphaValue.get()).rgb
        val rectCustomColor = Color(rectColorRedValue.get(), rectColorGreenValue.get(), rectColorBlueValue.get(), rectColorAlpha.get()).rgb

        val space = spaceValue.get()
        val textHeight = textHeightValue.get()
        val textY = textYValue.get()
        val textX = textXValue.get()
        val rectMode = rectValue.get()

        val backgroundCustomColor = Color(backgroundColorRedValue.get(), backgroundColorGreenValue.get(), backgroundColorBlueValue.get(), backgroundColorAlphaValue.get()).rgb

        val textShadow = shadowValue.get()
        val textSpacer = textHeight + space

        val saturation = saturationValue.get()
        val brightness = brightnessValue.get()

        val counter = intArrayOf(0)
        var cou = 0

        when (side.horizontal) {
            Horizontal.RIGHT, Horizontal.MIDDLE -> {
                modules.forEachIndexed { index, module ->
                    var displayString = multiReplace(when {
                        tagsArrayColor.get() -> module.colorlessTagName
                        tags.get() && tagsMode.get() == "Space" -> module.tagName
                        tags.get() && tagsMode.get() == "[]" -> module.tagName2
                        tags.get() && tagsMode.get() == "()" -> module.tagName3
                        tags.get() && tagsMode.get() == "<>" -> module.tagName4
                        tags.get() && tagsMode.get() == "-" -> module.tagName5
                        tags.get() && tagsMode.get() == "Bold" -> module.tagName6
                        tags.get() && tagsMode.get() == "White" -> module.tagName7
                        else -> module.arrayListName
                    })

                    if (lowerCaseValue.get())
                        displayString = displayString.toLowerCase()

                    val xPos = -module.slide - 4
                    val yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) * if (side.vertical == Vertical.DOWN) index + 1 else index
                    val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb
                    val rainbowCol = ColorUtils.rainbow((System.nanoTime() * oneSpeed.get()) * 1f, (counter[0] * oneWidth.get()) * 1f, oneOffset.get() * 1e8f, saturation, 1.0f).rgb

                    val col = Color(rainbowCol)
                    counter[0] = counter[0] + 1
                    val astolfo: Int = ColorUtils.Astolfo(counter[0] * 100, saturationValue.get(), brightnessValue.get())
                    val exhibition = Color(Color.HSBtoRGB((mc.thePlayer.ticksExisted / 50.0 + sin(index / 50.0 * 1.6)).toFloat() % 1f, 0.5f, 1f))
                    val width = fontRenderer.getStringWidth(displayString) + 1

                    if (module.state) {
                        if (module.slide < width) {
                            module.translate.translate(xPos, yPos)
                        }
                    } else if (!module.state) {
                        module.translate.translate(xPos, -25F)
                    }

                    // Background
                    val backgroundRectRainbow = backgroundColorMode.equals("ShaderRainbow", ignoreCase = true)
                    RainbowShader.begin(backgroundRectRainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 1250F).use {
                        RenderUtils.drawRect(
                                xPos - if (rectMode.equals("right", true)) 2 + backgroundWidthValue.get() else 0 + backgroundWidthValue.get(),
                                module.translate.y - if (index == 0) 1 else 0,
                                if (rectMode.equals("right", true)) -1F else 1F,
                                module.translate.y + textHeight, when {
                            backgroundRectRainbow -> 0xFF shl 24
                            backgroundColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(rainbowOffsetValue.get() * 10000000L * index, backgroundColorAlphaValue.get()).rgb
                            backgroundColorMode.equals("Random", ignoreCase = true) -> moduleColor
                            backgroundColorMode.equals("Astolfo", ignoreCase = true) -> astolfo
                            backgroundColorMode.equals("Exhibition", ignoreCase = true) -> exhibition.rgb
                            backgroundColorMode.equals("OneRainbow", ignoreCase = true) ->
                                Color(
                                        col.red / oneRedValue.get() + motionRedValue.get(),
                                        col.green / oneGreenValue.get() + motionGreenValue.get(),
                                        col.blue / oneBlueValue.get() + motionBlueValue.get(),
                                        colorAlphaValue.get()).rgb

                            else -> backgroundCustomColor
                        })
                    }

                    // Text
                    val rainbow2 = colorMode.equals("ShaderRainbow", ignoreCase = true)
                    RainbowFontShader.begin(rainbow2, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 1250F).use {
                        fontRenderer.drawString(
                                displayString, xPos + textX - if (rectMode.equals("right", true)) 1 + backgroundWidthValue.get() else -1 + backgroundWidthValue.get(), module.translate.y + textY,
                                when {
                                    rainbow2 -> 0
                                    colorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(rainbowOffsetValue.get() * 10000000L * index, colorAlphaValue.get()).rgb
                                    colorMode.equals("Random", ignoreCase = true) -> moduleColor
                                    colorMode.equals("Astolfo", ignoreCase = true) -> astolfo
                                    colorMode.equals("Exhibition", ignoreCase = true) -> exhibition.rgb
                                    colorMode.equals("OneRainbow", ignoreCase = true) ->
                                        Color(
                                                col.red / oneRedValue.get() + motionRedValue.get(),
                                                col.green / oneGreenValue.get() + motionGreenValue.get(),
                                                col.blue / oneBlueValue.get() + motionBlueValue.get(),
                                                colorAlphaValue.get()).rgb
                                    else -> customColor
                                },
                                textShadow
                        )
                    }

                    // Rect
                    val rectRainbow = rectColorMode.equals("ShaderRainbow", ignoreCase = true)
                    RainbowShader.begin(rectRainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 1250F).use {
                        val rectColor = when {
                            rectRainbow -> 0
                            rectColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(rainbowOffsetValue.get() * 10000000L * index, rectColorAlpha.get()).rgb
                            rectColorMode.equals("Random", ignoreCase = true) -> moduleColor
                            rectColorMode.equals("Astolfo", ignoreCase = true) -> astolfo
                            rectColorMode.equals("Exhibition", ignoreCase = true) -> exhibition.rgb
                            rectColorMode.equals("OneRainbow", ignoreCase = true) ->
                                Color(
                                        col.red / oneRedValue.get() + motionRedValue.get(),
                                        col.green / oneGreenValue.get() + motionGreenValue.get(),
                                        col.blue / oneBlueValue.get() + motionBlueValue.get(),
                                        colorAlphaValue.get()).rgb
                            else -> rectCustomColor
                        }

                        if (rectTopValue.get()) {
                            if (module == modules[modules.size - (modules.size)])
                                RenderUtils.drawRect(xPos - backgroundWidthValue.get(), module.translate.y - 1.77F, 1F, module.translate.y - 1, rectColor)
                        }

                        when {
                            rectMode.equals("left", true) -> RenderUtils.drawRect(xPos - 1 - backgroundWidthValue.get(), module.translate.y + (if (index == 1) 0.25f else 0f) - 0.25F, xPos - rectWidthValue.get() - backgroundWidthValue.get(), module.translate.y + textHeight, rectColor)

                            rectMode.equals("shortleft", true) -> RenderUtils.drawRect(xPos - 2 - backgroundWidthValue.get(), module.translate.y + 2.5F, xPos - rectWidthValue.get() - 1 - backgroundWidthValue.get(), module.translate.y + textHeight - 2.5F, rectColor)

                            rectMode.equals("right", true) -> RenderUtils.drawRect(-2F, module.translate.y - 2F, rectWidthValue.get() - 1F, module.translate.y + textHeight, rectColor)

                            rectMode.equals("outline", true) -> {

                                // Left
                                RenderUtils.drawRect(xPos - 1 - backgroundWidthValue.get(),
                                        module.translate.y,
                                        xPos - rectWidthValue.get()  - backgroundWidthValue.get(),
                                        module.translate.y + textHeight + 0.5F,
                                        rectColor)

                                // Junction
                                if (module != modules[0]) {
                                    val tags = updateTags(modules[index - 1])

                                    RenderUtils.drawRect(
                                            xPos - 1 - backgroundWidthValue.get() - (fontRenderer.getStringWidth(tags) - fontRenderer.getStringWidth(displayString)),
                                            module.translate.y,
                                            xPos - rectWidthValue.get() - 1 - backgroundWidthValue.get(),
                                            module.translate.y + 1,
                                            rectColor)
                                }

                                // Bottom
                                if (module == modules[modules.size - 1]) {
                                    RenderUtils.drawRect(
                                            xPos - 1 - backgroundWidthValue.get(),
                                            module.translate.y + textHeight,
                                            rectWidthValue.get() + 0f,
                                            module.translate.y + textHeight + 1,
                                            rectColor
                                    )
                                }
                            }
                        }
                    }
                    counter[0]++
                    cou++
                }
            }

            Horizontal.LEFT -> {
                modules.forEachIndexed { index, module ->
                    var displayString = multiReplace(when {
                        tagsArrayColor.get() -> module.colorlessTagName
                        tags.get() && tagsMode.get() == "Space" -> module.tagName
                        tags.get() && tagsMode.get() == "[]" -> module.tagName2
                        tags.get() && tagsMode.get() == "()" -> module.tagName3
                        tags.get() && tagsMode.get() == "<>" -> module.tagName4
                        tags.get() && tagsMode.get() == "-" -> module.tagName5
                        tags.get() && tagsMode.get() == "Bold" -> module.tagName6
                        tags.get() && tagsMode.get() == "White" -> module.tagName7
                        else -> module.arrayListName
                    })

                    if (lowerCaseValue.get())
                        displayString = displayString.toLowerCase()

                    val width = fontRenderer.getStringWidth(displayString)
                    val xPos = -(width - module.slide) + if (rectMode.equals("left", true)) 5 else 2
                    val yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) *
                            if (side.vertical == Vertical.DOWN) index + 1 else index
                    val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb

                    RenderUtils.drawRect(
                            0F,
                            yPos - if (index == 0) 1 else 0,
                            xPos + width + if (rectMode.equals("right", true)) 5 else 2,
                            yPos + textHeight, when {
                        backgroundColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(rainbowOffsetValue.get() * 10000000L * index,rectColorAlpha.get()).rgb
                        backgroundColorMode.equals("Random", ignoreCase = true) -> moduleColor
                        else -> backgroundCustomColor
                    }
                    )

                    fontRenderer.drawString(displayString, xPos + textX, yPos + textY, when {
                        colorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(rainbowOffsetValue.get() * 10000000L * index).rgb
                        colorMode.equals("Random", ignoreCase = true) -> moduleColor
                        else -> customColor
                    }, textShadow)

                    if (!rectMode.equals("none", true)) {
                        val rectColor = when {
                            rectColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(rainbowOffsetValue.get() * 10000000L * index, rectColorAlpha.get()).rgb
                            rectColorMode.equals("Random", ignoreCase = true) -> moduleColor
                            else -> rectCustomColor
                        }

                        when {
                            rectMode.equals("left", true) -> RenderUtils.drawRect(0F, yPos - 1, 3F, yPos + textHeight, rectColor)
                            rectMode.equals("right", true) ->
                                RenderUtils.drawRect(xPos + width + 2, yPos - 1, xPos + width + 2 + 3, yPos + textHeight, rectColor)
                        }
                    }
                }
            }
        }

        // Draw border
        if (mc.currentScreen is GuiHudDesigner) {
            x2 = Int.MIN_VALUE

            if (modules.isEmpty()) {
                return if (side.horizontal == Horizontal.LEFT)
                    Border(0F, -1F, 20F, 20F)
                else
                    Border(0F, -1F, -20F, 20F)
            }

            for (module in modules) {
                when (side.horizontal) {
                    Horizontal.RIGHT, Horizontal.MIDDLE -> {
                        val xPos = -module.slide.toInt() - 2
                        if (x2 == Int.MIN_VALUE || xPos < x2) x2 = xPos
                    }
                    Horizontal.LEFT -> {
                        val xPos = module.slide.toInt() + 14
                        if (x2 == Int.MIN_VALUE || xPos > x2) x2 = xPos
                    }
                }
            }
            y2 = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) * modules.size

            return Border(-1F, -1F, x2 - 7F, y2)
        }
        GlStateManager.resetColor()
        return null
    }

    override fun updateElement() {
        modules = LiquidBounce.moduleManager.modules
                .filter { it.array && it.slide > 0 }
                .sortedBy { -fontValue.get().getStringWidth(
                        multiReplace(
                                when {
                                    lowerCaseValue.get() -> (
                                            when {
                                                tagsArrayColor.get() -> it.colorlessTagName
                                                tags.get() && tagsMode.get() == "Space" -> it.tagName
                                                tags.get() && tagsMode.get() == "[]" -> it.tagName2
                                                tags.get() && tagsMode.get() == "()" -> it.tagName3
                                                tags.get() && tagsMode.get() == "<>" -> it.tagName4
                                                tags.get() && tagsMode.get() == "-" -> it.tagName5
                                                tags.get() && tagsMode.get() == "Bold" -> it.tagName6
                                                tags.get() && tagsMode.get() == "White" -> it.tagName7
                                                else -> it.arrayListName
                                            }).toLowerCase()
                                    tagsArrayColor.get() -> it.colorlessTagName
                                    tags.get() && tagsMode.get() == "Space" -> it.tagName
                                    tags.get() && tagsMode.get() == "[]" -> it.tagName2
                                    tags.get() && tagsMode.get() == "()" -> it.tagName3
                                    tags.get() && tagsMode.get() == "<>" -> it.tagName4
                                    tags.get() && tagsMode.get() == "-" -> it.tagName5
                                    tags.get() && tagsMode.get() == "Bold" -> it.tagName6
                                    tags.get() && tagsMode.get() == "White" -> it.tagName7
                                    else -> it.arrayListName
                                }
                        ))}

        if (alphaSort.get()) {
            modules = LiquidBounce.moduleManager.modules
                    .filter { it.array && it.slide > 0 }
                    .sortedBy { -fontValue.get().getStringWidth(0.toString()) }
        }
    }

    private fun updateTags(it: Module): String {
        var displayString = multiReplace(if (!tags.get()) it.arrayListName
        else if (tagsArrayColor.get()) when (tagsMode.get()) {
            "[]" -> it.tagName2
            "()" -> it.tagName3
            "<>" -> it.tagName4
            "-" -> it.tagName5
            "Bold" -> it.tagName6
            "White" -> it.tagName7
            else -> it.colorlessTagName
        } else when (tagsMode.get()) {
            "[]" -> it.tagName2
            "()" -> it.tagName3
            "<>" -> it.tagName4
            "-" -> it.tagName5
            "Bold" -> it.tagName6
            "White" -> it.tagName7
            else -> it.tagName
        })

        if (lowerCaseValue.get()) displayString = displayString.toLowerCase()
        return displayString
    }

    private fun getBreakName(string: String): String {
        var outPut = ""

        if (string.isNotEmpty()) outPut += string[0]
        for (i in 1 until string.length) {
            if (string[i].isUpperCase() && i < string.length - 1 && (string[i + 1].isLowerCase() || string[i - 1].isLowerCase()))
                outPut += " "
            outPut += string[i]
        }
        return outPut
    }
}