package net.ccbluex.liquidbounce.ui.client.neverloseimport net.ccbluex.liquidbounce.features.module.Moduleimport net.ccbluex.liquidbounce.ui.client.miscible.`package`.ValueElementimport net.ccbluex.liquidbounce.ui.font.Fontsimport net.ccbluex.liquidbounce.ui.font.GameFontRendererimport net.ccbluex.liquidbounce.utils.render.RenderUtilsimport net.ccbluex.liquidbounce.value.*import net.minecraft.client.audio.PositionedSoundRecordimport net.minecraft.client.gui.FontRendererimport net.minecraft.client.renderer.GlStateManagerimport net.minecraft.util.ResourceLocationimport org.lwjgl.input.Mouseimport org.lwjgl.opengl.GL11import java.awt.Colorobject NModule {    var list = 0    var values: List<Value<*>>? = null    fun drawModule(positionX: Int, positionY: Int, mouseX: Int, mouseY: Int, module: Module, main: Main) {        val yes =            main.hovertoFloatL(Impl.coordinateX.toInt() + 103f, Impl.coordinateY.toInt() + 60f, Impl.coordinateX.toInt() + 430f,                               Impl.coordinateY.toInt() + 335f, mouseX, mouseY, false)        GlStateManager.pushMatrix()        RenderUtils.makeScissorBox(Impl.coordinateX.toInt() + 103f, Impl.coordinateY.toInt() + 60f, Impl.coordinateX.toInt() + 430f,                                   Impl.coordinateY.toInt() + 335f)        GL11.glEnable(GL11.GL_SCISSOR_TEST)        if (module.state) module.clickAnimation.translate(10f, 0f)        else module.clickAnimation.translate(0f, 0f)        if (main.hovertoFloatL(positionX.toFloat() + 140, positionY.toFloat() + 3, positionX + 160f, positionY + 15f, mouseX, mouseY, true) && yes) {            module.state = !module.state        }        RenderUtils.drawRect(positionX.toFloat(), positionY.toFloat(), positionX + 163f,                             positionY + 20f + (20 * (module.values.size + module.outvalue)), Color(0, 11, 22).rgb)        RenderUtils.drawRect(positionX + 3f, positionY + 16f, positionX + 160f, positionY + 17f, Color(7, 24, 34).rgb)        Fonts.font17.drawString(module.name, positionX + 8, positionY + 7, -1)        RenderUtils.drawNLRect(positionX.toFloat() + 143, positionY.toFloat() + 6, positionX + 157f, positionY + 12f, 2.1f,                                    if (module.state) Color(3, 23, 46).rgb else Color(3, 5, 13).rgb)        RenderUtils.drawFullCircle(positionX.toFloat() + 145 + module.clickAnimation.x, positionY.toFloat() + 9, 3.5f, 0f,                                   if (module.state) Color(3, 168, 245) else Color(74, 87, 97))        // Draw settings        val moduleValues: List<Value<*>> = module.values        module.outvalue = 0        if (moduleValues.isNotEmpty()) {            var valuepositionY = 0            module.openValue.translate(0f, 20f)            for (value in module.values) {                if (value is BoolValue) {                    if (value.get()) value.boolvalue.translate(10f, 0f)                    else value.boolvalue.translate(0f, 0f)                    if (main.hovertoFloatL(positionX.toFloat() + 130, positionY.toFloat() + 21 + valuepositionY, positionX + 150f,                                           positionY + 33f + valuepositionY, mouseX, mouseY, true) && yes) {                        value.set(!value.get())                    }                    RenderUtils.drawNLRect(positionX.toFloat() + 133, positionY.toFloat() + 24 + valuepositionY, positionX + 147f,                                                positionY + 30f + valuepositionY, 2.1f,                                                if (value.get()) Color(3, 23, 46).rgb else Color(3, 5, 13).rgb)                    RenderUtils.drawFullCircle(positionX.toFloat() + 135 + value.boolvalue.x, positionY.toFloat() + 27 + valuepositionY, 3.5f, 0f,                                               if (value.get()) Color(3, 168, 245) else Color(74, 87, 97))                    GlStateManager.resetColor()                    Fonts.font16.drawString(value.name, positionX + 8, positionY + 25 + valuepositionY,                                            if (value.get()) Color(255, 255, 255).rgb else Color(175, 175, 175).rgb)                    valuepositionY += module.openValue.y.toInt()                }                else if (value is ListValue) {                    if (main.hovertoFloatL(positionX.toFloat() + 95f, positionY.toFloat() + 22f + valuepositionY, positionX + 155f,                                           positionY.toFloat() + 33f + valuepositionY, mouseX, mouseY, true) && yes) {                        value.openList = !value.openList                    }                    RenderUtils.drawRoundedRect(positionX.toFloat() + 95f, positionY.toFloat() + 22f + valuepositionY, 60f, 11f, 1f,                                                Color(2, 5, 12).rgb, 0.25f, Color(100, 100, 100, if (!value.openList) 100 else 0).rgb)                    Fonts.font16.drawString(value.name, positionX + 8, positionY + 25 + valuepositionY, Color(200, 200, 200).rgb)                    main.drawText(value.get(), 14 , Fonts.font16 ,positionX + 98, positionY + 26 + valuepositionY, Color(200, 200, 200).rgb)                    if (value.openList) {                        RenderUtils.drawRect(positionX.toFloat() + 95f, positionY.toFloat() + 33f + valuepositionY, positionX + 155f,                                             positionY.toFloat() + 35f + valuepositionY, Color(7,24,34).rgb)                    }                    valuepositionY += module.openValue.y.toInt()                    for (valueof in value.values) {                        if (value.openList) {                            val hover =                                main.hovertoFloatL(positionX.toFloat() + 95f, positionY.toFloat() + 13f + valuepositionY, positionX + 155f,                                                   positionY.toFloat() + 33f + valuepositionY, mouseX, mouseY, false)                            if (main.hovertoFloatL(positionX.toFloat() + 95f, positionY.toFloat() + 13f + valuepositionY, positionX + 155f,                                                   positionY.toFloat() + 33f + valuepositionY, mouseX, mouseY, true) && yes) {                                value.set(valueof)                                value.openList = !value.openList                            }                            RenderUtils.drawRect(positionX.toFloat() + 95f, positionY.toFloat() + 14f + valuepositionY, positionX + 155f,                                                 positionY.toFloat() + 34f + valuepositionY, if (hover) Color(0, 22, 33).rgb else Color(2, 5, 12).rgb)                            main.drawText(valueof, 14, Fonts.font16 ,positionX + 98, positionY + 23 + valuepositionY, if(valueof == value.get()) Color(0,55,66).rgb else Color(200, 200, 200).rgb)                            valuepositionY += module.openValue.y.toInt()                            module.outvalue++                        }                    }                }                else if (value is MultiBoolValue) {                    var index = 0                    if (main.hovertoFloatL(positionX.toFloat() + 75f, positionY.toFloat() + 22f + valuepositionY, positionX + 127f,                                           positionY.toFloat() + 31f + valuepositionY, mouseX, mouseY, true) && yes) {                        value.openList = !value.openList                    }                    RenderUtils.drawRoundedRect(positionX.toFloat() + 95f, positionY.toFloat() + 22f + valuepositionY, 60f, 11f, 1f,                                                Color(2, 5, 12).rgb, 0.25f, Color(100, 100, 100, if (!value.openList) 100 else 0).rgb)                    GlStateManager.resetColor()                    Fonts.font16.drawString(value.name, positionX + 8, positionY + 25 + valuepositionY, Color(200, 200, 200).rgb)                    if (value.openList) {                        RenderUtils.drawRect(positionX.toFloat() + 95f, positionY.toFloat() + 33f + valuepositionY, positionX + 155f,                                             positionY.toFloat() + 35f + valuepositionY, Color(7,24,34).rgb)                    }                    valuepositionY += module.openValue.y.toInt()                    var fix = 0                    var enablerstring = ""                    for (valueOfList in 0..value.values.lastIndex) {                        if (value.value[valueOfList]) {                            enablerstring += value.values[valueOfList] + " "                        }                        if (value.openList) {                            val hover =                                main.hovertoFloatL(positionX.toFloat() + 95f, positionY.toFloat() + 13f + valuepositionY, positionX + 155f,                                                   positionY.toFloat() + 33f + valuepositionY, mouseX, mouseY, false)                            if (main.hovertoFloatL(positionX.toFloat() + 95f, positionY.toFloat() + 13f + valuepositionY, positionX + 155f,                                                   positionY.toFloat() + 33f + valuepositionY, mouseX, mouseY, true) && yes) {                                value.value[valueOfList] = !value.value[valueOfList]                            }                            RenderUtils.drawRect(positionX.toFloat() + 95f, positionY.toFloat() + 14f + valuepositionY, positionX + 155f,                                                 positionY.toFloat() + 34f + valuepositionY, if (hover) Color(0, 22, 33).rgb else Color(2, 5, 12).rgb)                            main.drawText(value.values[valueOfList], 14, Fonts.font16, positionX + 98, positionY + 22 + valuepositionY,                                     if (value.value[valueOfList]) Color(70, 111, 255).rgb else Color(175, 175, 175).rgb)                            valuepositionY += module.openValue.y.toInt()                            fix += 20                            module.outvalue++                            index++                        }                    }                    main.drawText(enablerstring, 15, Fonts.font16, positionX + 98, positionY + 6 + valuepositionY - (20 * index),                             Color(175, 175, 175).rgb)                }                else if (value is IntegerValue) {                    value.intvalue.translate(value.int, 0f, 1.0)                    val inc = 1.0                    val max = value.maximum                    val min = value.minimum                    val longValue = 115f                    val valAbs: Double = (mouseX - (positionX + 10f)).toDouble()                    var perc = valAbs / (longValue * Math.max(Math.min(value.get() / max, 0), 1))                    perc = Math.min(Math.max(0.0, perc), 1.0)                    val valRel = (max - min) * perc                    value.int = (longValue * (value.get() - min) / (max - min))                    if (main.hovertoFloatL(positionX + 9f, positionY + 26.5f + valuepositionY, positionX + 126f, positionY + 35.5f + valuepositionY,                                           mouseX, mouseY, false) && Mouse.isButtonDown(0) && yes) {                        var idk = min + valRel                        idk = Math.round(idk * (1 / inc)) / (1 / inc)                        value.set(idk.toInt())                    }                    RenderUtils.drawRoundedRect(positionX.toFloat() + 130f, positionY.toFloat() + 26.5f + valuepositionY, 25f, 9f, 1f,                                                Color(2, 5, 12).rgb, 0.25f, Color(100, 100, 100, 100).rgb)                    main.drawText(value.get().toString(), 5 , Fonts.font16 , positionX + 132, positionY + 29 + valuepositionY, Color(175, 175, 175).rgb)                    RenderUtils.drawRect(positionX + 10f, positionY + 31.5f + valuepositionY, positionX + 125f, positionY + 32.5f + valuepositionY,                                         Color(6, 21, 36).rgb)                    RenderUtils.drawRect(positionX + 10f, positionY + 31.5f + valuepositionY, positionX + 10f + value.intvalue.x,                                         positionY + 32.5f + valuepositionY, Color(34, 92, 123).rgb)                    RenderUtils.drawFullCircle(positionX + 10f + value.intvalue.x, positionY + 31.5f + valuepositionY, 3f, 0f, Color(57, 133, 236))                    Fonts.font14.drawString(value.name, positionX + 8, positionY + 22 + valuepositionY, Color(175, 175, 175).rgb)                    valuepositionY += module.openValue.y.toInt()                }                else if (value is FloatValue) {                    value.floatvalue.translate(value.float, 0f, 1.0)                    val inc = 0.01                    val max = value.maximum                    val min = value.minimum                    val longValue = 115f                    val valAbs: Double = (mouseX - (positionX + 10f)).toDouble()                    var perc = valAbs / (longValue * Math.max(Math.min(value.get() / max, 0f), 1f))                    perc = Math.min(Math.max(0.0, perc), 1.0)                    val valRel = (max - min) * perc                    value.float = (longValue * (value.get() - min) / (max - min))                    if (main.hovertoFloatL(positionX + 9f, positionY + 26.5f + valuepositionY, positionX + 126f, positionY + 35.5f + valuepositionY,                                           mouseX, mouseY, false) && Mouse.isButtonDown(0) && yes) {                        var idk = min + valRel                        idk = Math.round(idk * (1 / inc)) / (1 / inc)                        value.set(idk.toFloat())                    }                    RenderUtils.drawRoundedRect(positionX.toFloat() + 130f, positionY.toFloat() + 26.5f + valuepositionY, 25f, 9f, 1f,                                                Color(2, 5, 12).rgb, 0.25f, Color(100, 100, 100, 100).rgb)                    main.drawText(value.get().toString(), 5, Fonts.font16 , positionX + 132, positionY + 29 + valuepositionY, Color(175, 175, 175).rgb)                    RenderUtils.drawRect(positionX + 10f, positionY + 31.5f + valuepositionY, positionX + 125f, positionY + 32.5f + valuepositionY,                                         Color(6, 21, 36).rgb)                    RenderUtils.drawRect(positionX + 10f, positionY + 31.5f + valuepositionY, positionX + 10f + value.floatvalue.x,                                         positionY + 32.5f + valuepositionY, Color(34, 92, 123).rgb)                    RenderUtils.drawFullCircle(positionX + 10f + value.floatvalue.x, positionY + 31.5f + valuepositionY, 3f, 0f, Color(57, 133, 236))                    Fonts.font14.drawString(value.name, positionX + 8, positionY + 22 + valuepositionY, Color(175, 175, 175).rgb)                    valuepositionY += module.openValue.y.toInt()                }                else if (value is FontValue) {                    val fonts = Fonts.getFonts()                    var displayString = "Font: Unknown"                    if (value.get() is GameFontRenderer) {                        val liquidFontRenderer = value.get() as GameFontRenderer                        displayString = "Font: " + liquidFontRenderer.defaultFont.font.name + " - " + liquidFontRenderer.defaultFont.font.size                    }                    else if (value.get() === Fonts.minecraftFont) displayString = "Font: Minecraft"                    else {                        val objects = Fonts.getFontDetails(value.get())                        if (objects != null) {                            displayString = objects[0].toString() + if (objects[1] as Int != -1) " - " + objects[1] else ""                        }                    }                    if (main.hovertoFloatL(positionX + 8f, positionY + 22f + valuepositionY, positionX + 120f, positionY + 34f + valuepositionY,                                           mouseX, mouseY, true) && yes) {                        ValueElement.mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 5f))                        var i = 0                        while (i < fonts.size) {                            val font: FontRenderer = fonts.get(i)                            if (font === value.get()) {                                i++                                if (i >= fonts.size) i = 0                                value.set(fonts.get(i))                                break                            }                            i++                        }                    }                    if (main.hovertoFloatR(positionX + 8f, positionY + 22f + valuepositionY, positionX + 120f, positionY + 34f + valuepositionY,                                           mouseX, mouseY, true) && yes && module.showSettings) {                        ValueElement.mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("gui.button.press"), 5f))                        var i = fonts.size - 1                        while (i >= 0) {                            val font = fonts[i]                            if (font === value.get()) {                                i--                                if (i >= fonts.size) i = 0                                if (i < 0) i = fonts.size - 1                                value.set(fonts[i])                                break                            }                            i--                        }                    }                    main.drawText(displayString, 100, Fonts.font16,positionX + 8, positionY + 25 + valuepositionY, Color(175, 175, 175).rgb)                    valuepositionY += module.openValue.y.toInt()                }            }        }        GL11.glDisable(GL11.GL_SCISSOR_TEST)        GlStateManager.popMatrix()    }}