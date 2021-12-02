package net.ccbluex.liquidbounce.ui.client.miscible.`package`

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.ui.client.miscible.MElement
import net.ccbluex.liquidbounce.ui.client.miscible.Miscible
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer
import me.aquavit.liquidsense.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Mouse
import java.awt.Color

object ValueElement {


    val mc = Minecraft.getMinecraft()

    fun drawValue(value: Value<*>, module: Module, miscible: Miscible, posY: Float, dropxsize: Float, dropysize: Float, mouseX: Int, mouseY: Int) {
        val fix = (posY / 21)

        val enabeld = miscible.hovertoFloatL(MElement.x + (115 * dropxsize), MElement.y + 5, MElement.x + (395f * dropxsize), MElement.y + (245 * dropysize), mouseX, mouseY, false)
        val displays = fix > 0.3f

        if (value is BoolValue) {
            value.boolvalue.translate(value.boolean, 0f, 1.0)
            if (value.get()) value.boolean = 275f

            if (miscible.hovertoFloatL(MElement.x + (120f * dropxsize), MElement.y + 45.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (395f * dropxsize), MElement.y + 60.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), mouseX, mouseY, true) && enabeld && module.showSettings) {
                value.boolean = 0f
                value.set(!value.get())
                mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 5f))
            }

            if (displays) {
                RenderUtils.drawNLRect(MElement.x + (120f * dropxsize).toInt(), MElement.y + 45.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + ((120f + value.boolvalue.x) * dropxsize).toInt(), MElement.y + 60.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), if (value.boolvalue.x > 1f) 1f else 0f, Color(72, 131, 255, (255 * fix).toInt()).rgb)
                Fonts.font17.drawString(value.name + " ...", MElement.x + ((123f + value.boolvalue.x / 42f) * dropxsize).toInt(), MElement.y + 50.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), if (value.get()) Color(255, 255, 255, (255 * fix).toInt()).rgb else Color(75, 75, 75, (255 * fix).toInt()).rgb)
            }

            miscible.modulePosY += module.openValue.y
            if (module.showSettings) module.guiposY += 21
        }
        else if (value is ListValue) {
            value.listvalue.translate(0f, value.list, 1.0)
            if (value.openList) value.list = 21f
            val name = value.name + " : §7" + value.get()

            val x = (if (Fonts.font18.getStringWidth(name) > 100) Fonts.font18.getStringWidth(name) * 2f + 14 else 224f) / 2

            if (miscible.hovertoFloatL(MElement.x + (120f * dropxsize), MElement.y + 45.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (570f * dropxsize), MElement.y + 60.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), mouseX, mouseY, true) && enabeld && module.showSettings) {
                value.list = 0f
                value.openList = !value.openList
                mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 5f))
            }


            if (displays) {
                if (value.listvalue.y != 0f) {
                    RenderUtils.drawShader(MElement.x + (120f * dropxsize), MElement.y + 45f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), x * dropxsize, 18 + (value.listvalue.y * value.values.size))
                    RenderUtils.drawRect(MElement.x + (120.0 * dropxsize), MElement.y + 45.0 + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (120.0 + x.toDouble()) * dropxsize, MElement.y + 62.0 + miscible.modulePosY + (value.listvalue.y * value.values.size) + miscible.wheeltranslate.y.toInt(), Color(35, 35, 35, (255 * fix).toInt()).rgb)
                }
                Fonts.font18.drawString(name, MElement.x + (123 * dropxsize).toInt(), MElement.y + 51.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), Color(255, 255, 255, (255 * fix).toInt()).rgb, false)
            }

            miscible.modulePosY += module.openValue.y
            if (module.showSettings) module.guiposY += 21
            for (valueOfList in value.values) {
                if (value.openList && module.showSettings) {

                    val hover = miscible.hovertoFloatL(MElement.x + (120f * dropxsize), MElement.y + 42.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (120.0f + x) * dropxsize, MElement.y + 63.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), mouseX, mouseY, false)

                    if (hover && !miscible.mouseLDown && Mouse.isButtonDown(0) && enabeld) {
                        value.set(valueOfList)
                    }

                    if (hover) RenderUtils.drawRect(MElement.x + (120.0 * dropxsize), MElement.y + 42.0 + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (120.0 + x.toDouble()) * dropxsize, MElement.y + 63.0 + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), Color(75, 75, 75, (255 * fix).toInt()).rgb)
                    Fonts.font17.drawString(valueOfList, MElement.x + (125f * dropxsize).toInt(), MElement.y + 50.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), if (valueOfList == value.get()) Color(70, 111, 255, (255 * fix).toInt()).rgb else Color(255, 255, 255, (255 * fix).toInt()).rgb, false)

                    miscible.modulePosY += value.listvalue.y
                    module.guiposY += 21
                }
                else if (!value.openList) {
                    value.list = 0f
                    miscible.modulePosY += value.listvalue.y
                }
            }
        }
        else if (value is MultiBoolValue) {

            value.MultiBoolvalue.translate(0f, value.MultiBool, 1.0)
            if (value.openList) value.MultiBool = 21f

            val name = value.name

            val openhover = miscible.hovertoFloatL(MElement.x + (120f * dropxsize), MElement.y + 45.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (220f * dropxsize), MElement.y + 60.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), mouseX, mouseY, false)

            if (openhover && !miscible.mouseLDown && Mouse.isButtonDown(0) && enabeld && module.showSettings) {
                value.openList = !value.openList
                mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 5f))
            }

            val x = (if (Fonts.font18.getStringWidth(name) > 112) Fonts.font18.getStringWidth(name) * 2f + 14 else 224f) / 2

            if (displays) {
                if (value.MultiBoolvalue.y != 0f) {
                    RenderUtils.drawShader(MElement.x + (120f * dropxsize), MElement.y + 45f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), x * dropxsize, 18 + (value.MultiBoolvalue.y * value.values.size))
                    RenderUtils.drawRectBordered(MElement.x + (120.0 * dropxsize), MElement.y + 45.0 + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (120.0 + x.toDouble()) * dropxsize, MElement.y + 62.0 + miscible.modulePosY + (value.MultiBoolvalue.y * value.values.size) + miscible.wheeltranslate.y.toInt(), 0.5, Color(40, 40, 40, (255 * fix).toInt()).rgb, Color(55, 55, 55, (255 * fix).toInt()).rgb)
                }
                Fonts.font18.drawString("$name : ", MElement.x + (123 * dropxsize).toInt(), MElement.y + 51.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), Color(255, 255, 255, (255 * fix).toInt()).rgb, false)
            }

            miscible.modulePosY += module.openValue.y
            if (module.showSettings) module.guiposY += 21

            var index = 0
            for (valueOfList in 0..value.values.lastIndex) {
                val hover = miscible.hovertoFloatL(MElement.x + (120f * dropxsize), MElement.y + 42.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (120.0f + x) * dropxsize, MElement.y + 63.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), mouseX, mouseY, false)

                if (value.openList && module.showSettings) {
                    val valueenabler = value.value[valueOfList]

                    if (hover && !miscible.mouseLDown && Mouse.isButtonDown(0) && enabeld) {
                        value.value[valueOfList] = !value.value[valueOfList]
                    }

                    if (hover) RenderUtils.drawRect(MElement.x + (120.0 * dropxsize), MElement.y + 42.0 + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (120.0 + x.toDouble()) * dropxsize, MElement.y + 63.0 + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), Color(75, 75, 75, (255 * fix).toInt()).rgb)
                    Fonts.font17.drawString(if (valueenabler) "§l${value.values[valueOfList]}" else value.values[valueOfList], MElement.x + (125f * dropxsize).toInt(), MElement.y + 50.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), if (valueenabler) Color(70, 111, 255, (255 * fix).toInt()).rgb else Color(255, 255, 255, (255 * fix).toInt()).rgb, false)

                    miscible.modulePosY += value.MultiBoolvalue.y
                    module.guiposY += 21
                }
                else if (!value.openList) {
                    if (module.showSettings && value.MultiBoolvalue.y == 0f) {
                        Fonts.font18.drawString(value.values[valueOfList], MElement.x + (123 * dropxsize).toInt() + Fonts.font18.getStringWidth("$name : ") + 3 + index, MElement.y + 30.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), if (value.value[valueOfList]) Color(70, 111, 255, (255 * fix).toInt()).rgb else Color(65, 65, 65, (255 * fix).toInt()).rgb)
                        index += Fonts.font18.getStringWidth(value.values[valueOfList]) + 2
                    }
                    value.MultiBool = 0f
                    miscible.modulePosY += value.MultiBoolvalue.y
                }
            }
        }
        else if (value is FloatValue) {
            value.floatvalue.translate(value.float, 0f, 1.0)
            val name = value.name

            val inc = 0.01
            val max = value.maximum
            val min = value.minimum
            val longValue = (265 * dropxsize)
            val valAbs: Double = (mouseX - (MElement.x + (125f * dropxsize))).toDouble()
            var perc = valAbs / (longValue * Math.max(Math.min(value.get() / max, 0.0f), 1.0f))
            perc = Math.min(Math.max(0.0, perc), 1.0)
            val valRel = (max - min) * perc

            value.float = (longValue * (value.get() - min) / (max - min))

            if (miscible.hovertoFloatL(MElement.x + (125f * dropxsize), MElement.y + 52.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (390f * dropxsize), MElement.y + 63.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), mouseX, mouseY, false) && Mouse.isButtonDown(0) && enabeld && module.showSettings) {
                var idk = min + valRel
                idk = Math.round(idk * (1 / inc)) / (1 / inc)
                value.set(idk.toFloat())
            }

            if (displays) {
                RenderUtils.drawNLRect(MElement.x + (123f * dropxsize), MElement.y + 56.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (390f * dropxsize), MElement.y + 60.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), 1f, Color(35, 35, 35, (255 * fix).toInt()).rgb)
                RenderUtils.drawNLRect(MElement.x + (123f * dropxsize), MElement.y + 56.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (123f * dropxsize) + value.floatvalue.x, MElement.y + 60.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), 1f, Color(52, 111, 255, (255 * fix).toInt()).rgb)
                RenderUtils.drawFullCircle(MElement.x + (125f * dropxsize) + value.floatvalue.x, MElement.y + 58.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), 6.25F, 0F, Color(30, 30, 30, (255 * fix).toInt()))
                RenderUtils.drawFullCircle(MElement.x + (125f * dropxsize) + value.floatvalue.x, MElement.y + 58.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), 4F, 0F, Color(72, 131, 255, (255 * fix).toInt()))
                GlStateManager.resetColor()
                Fonts.font16.drawString(name + " : §7" + value.get(), MElement.x + (123f * dropxsize).toInt(), MElement.y + 47.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), Color(255, 255, 255, (255 * fix).toInt()).rgb, false)
            }
            miscible.modulePosY += module.openValue.y
            if (module.showSettings) module.guiposY += 21
        }
        else if (value is IntegerValue) {
            value.intvalue.translate(value.int, 0f, 1.0)
            val name = value.name

            val inc = 0.05
            val max = value.maximum
            val min = value.minimum
            val longValue = 265 * dropxsize
            val valAbs: Double = (mouseX - (MElement.x + (125f * dropxsize))).toDouble()
            var perc = valAbs / (longValue * Math.max(Math.min(value.get() / max, 0), 1))
            perc = Math.min(Math.max(0.0, perc), 1.0)
            val valRel = (max - min) * perc

            value.int = (longValue * (value.get() - min) / (max - min)).toFloat()

            if (miscible.hovertoFloatL(MElement.x + (125f * dropxsize), MElement.y + 52.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (390 * dropxsize), MElement.y + 63.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), mouseX, mouseY, false) && Mouse.isButtonDown(0) && enabeld && module.showSettings) {
                var idk = min + valRel
                idk = Math.round(idk * (1 / inc)) / (1 / inc)
                value.set(idk.toInt())
            }

            if (displays) {
                RenderUtils.drawNLRect(MElement.x + (123f * dropxsize), MElement.y + 56.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (390f * dropxsize), MElement.y + 60.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), 1f, Color(35, 35, 35, (255 * fix).toInt()).rgb)
                RenderUtils.drawNLRect(MElement.x + (123f * dropxsize), MElement.y + 56.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (123f * dropxsize) + value.intvalue.x, MElement.y + 60.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), 1f, Color(52, 111, 255, (255 * fix).toInt()).rgb)
                RenderUtils.drawFullCircle(MElement.x + (125f * dropxsize) + value.intvalue.x, MElement.y + 58.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), 6.25F, 0F, Color(30, 30, 30, (255 * fix).toInt()))
                RenderUtils.drawFullCircle(MElement.x + (125f * dropxsize) + value.intvalue.x, MElement.y + 58.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), 4F, 0F, Color(72, 131, 255, (255 * fix).toInt()))

                GlStateManager.resetColor()
                Fonts.font16.drawString(name + " : §7" + value.get(), MElement.x + (123f * dropxsize).toInt(), MElement.y + 47.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), Color(255, 255, 255, (255 * fix).toInt()).rgb, false)
            }

            miscible.modulePosY += module.openValue.y
            if (module.showSettings) module.guiposY += 21
        }
        else if (value is FontValue) {

            val fonts = Fonts.getFonts()

            var displayString = "Font: Unknown"
            if (value.get() is GameFontRenderer) {
                val liquidFontRenderer = value.get() as GameFontRenderer
                displayString = "Font: " + liquidFontRenderer.defaultFont.font.name + " - " + liquidFontRenderer.defaultFont.font.size
            }
            else if (value.get() === Fonts.minecraftFont) displayString = "Font: Minecraft"
            else {
                val objects = Fonts.getFontDetails(value.get())
                if (objects != null) {
                    displayString = objects[0].toString() + if (objects[1] as Int != -1) " - " + objects[1] else ""
                }
            }

            if (miscible.hovertoFloatL(MElement.x + (120 * dropxsize), MElement.y + 45.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (395 * dropxsize), MElement.y + 60.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), mouseX, mouseY, true) && enabeld && module.showSettings) {
                mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 5f))
                var i = 0
                while (i < fonts.size) {
                    val font: FontRenderer = fonts.get(i)
                    if (font === value.get()) {
                        i++
                        if (i >= fonts.size) i = 0
                        value.set(fonts.get(i))
                        break
                    }
                    i++
                }
            }

            if (miscible.hovertoFloatR(MElement.x + (120 * dropxsize), MElement.y + 45.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), MElement.x + (395 * dropxsize), MElement.y + 60.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), mouseX, mouseY, true) && enabeld && module.showSettings) {
                mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 5f))
                var i = fonts.size - 1
                while (i >= 0) {
                    val font = fonts[i]
                    if (font === value.get()) {
                        i--
                        if (i >= fonts.size) i = 0
                        if (i < 0) i = fonts.size - 1
                        value.set(fonts[i])
                        break
                    }
                    i--
                }
            }

            if (displays) Fonts.font17.drawString(displayString, MElement.x + (123f + (value.boolvalue.x / 42f)) * dropxsize, MElement.y + 50.0f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), Color(75, 75, 75, (255 * fix).toInt()).rgb)
            miscible.modulePosY += module.openValue.y
            if (module.showSettings) module.guiposY += 21
        }
    }
}