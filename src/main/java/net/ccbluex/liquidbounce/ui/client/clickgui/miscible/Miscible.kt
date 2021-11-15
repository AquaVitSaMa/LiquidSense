package net.ccbluex.liquidbounce.ui.client.miscible

import me.aquavit.liquidsense.utils.render.Translate
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.miscible.MElement.move
import net.ccbluex.liquidbounce.ui.client.miscible.`package`.CategoryElement
import net.ccbluex.liquidbounce.ui.client.miscible.`package`.ModuleElement
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ChatAllowedCharacters
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.abs

open class Miscible : GuiScreen() {


    private var hoverdrop = false
    private var hovermove = false
    private var ismove = false

    private var lastmouseX = 0
    private var lastmousey = 0

    private var x = 0f
    private var y = 0f

    val wheeltranslate = Translate(0f, 0f)
    val translate = Translate(0f, 0f)

    var modules = emptyList<Module>()
    var modulePosY = 0f


    fun background(dropxsize: Float, dropysize: Float) { //���� + ��Ӱ
        RenderUtils.drawShader(MElement.x, MElement.y, MElement.dropx, MElement.dropy)
        RenderUtils.drawRect(MElement.x, MElement.y, MElement.x + MElement.dropx, MElement.y + MElement.dropy, Color(33, 33, 33).rgb)

        RenderUtils.drawNLRect(MElement.x + 5f, MElement.y + 5f, MElement.x + (105 * dropxsize), MElement.y + (245f * dropysize), 2.5f, Color(44, 44, 44).darker().rgb) // Setting����
        RenderUtils.drawNLRect(MElement.x + 8, MElement.y + (225 * dropysize), MElement.x + (103 * dropxsize), MElement.y + (243f * dropysize), 3f, Color(33, 33, 33).rgb)

        renderHead()

        Fonts.font16.drawString(mc.thePlayer.name.toUpperCase(), MElement.x + 38f, MElement.y + 18f, -1, false)
        RenderUtils.drawRect(MElement.x + 5f, MElement.y + 34f, MElement.x + 105f * dropysize, MElement.y + 35f, Color(33, 33, 33).rgb)
        Fonts.font17.drawCenteredString("Setting", MElement.x + (51.5f * dropxsize), MElement.y + (232 * dropysize), -1, false)

        RenderUtils.drawRect(MElement.x + MElement.dropx - 1, MElement.y + MElement.dropy - 5, MElement.x + MElement.dropx, MElement.y + MElement.dropy, Color(0, 255, 0, 150).rgb)
        RenderUtils.drawRect(MElement.x + MElement.dropx - 5, MElement.y + MElement.dropy - 1, MElement.x + MElement.dropx, MElement.y + MElement.dropy, Color(0, 255, 0, 150).rgb)
    }

    private fun renderHead() {
        glPushMatrix()
        glTranslated(MElement.x + 10.0, MElement.y + 7.5, 0.0)
        glColor3f(1f, 1f, 1f)
        val playerInfo = mc.netHandler.getPlayerInfo(mc.thePlayer.uniqueID)
        if (playerInfo != null) {
            val locationSkin = playerInfo.locationSkin
            mc.textureManager.bindTexture(locationSkin)
            RenderUtils.drawScaledCustomSizeModalRect(2, 2, 8F, 8F, 8, 8, 21, 21, 64F, 64F)
            glColor4f(1F, 1F, 1F, 1F)
        }
        glPopMatrix()
        for (i in 0..16) {
            RenderUtils.drawCircle(MElement.x + 22.5f, MElement.y + 20.0f, 11f + (0.25f * i), -180, 180, Color(44, 44, 44).darker())
            GlStateManager.resetColor()
        }
    }

    private fun dropManager(mouseX: Int, mouseY: Int) {
        if (hovertoFloatL(MElement.x + MElement.dropx - 5, MElement.y + MElement.dropy - 5, MElement.x + MElement.dropx, MElement.y + MElement.dropy, mouseX, mouseY, false) && Mouse.isButtonDown(0)) {
            hoverdrop = true
        }

        if (hoverdrop) {
            if (!Mouse.isButtonDown(0)) {
                hoverdrop = false
            }
            else {
                MElement.dropx = mouseX.toFloat() - MElement.x

                MElement.dropy = mouseY.toFloat() - MElement.y

                val dropxsize = MElement.dropx / 420f

                val dropysize = MElement.dropy / 250f

                if (dropxsize >= 2.0f) MElement.dropx = 840f
                else
                    if (dropxsize <= 1f) MElement.dropx = 420f

                if (dropysize >= 2.0) MElement.dropy = 500f
                else
                    if (dropysize <= 1f) MElement.dropy = 250f
            }
        }
    }

    private fun MoveManager(mouseX: Int, mouseY: Int, dropxsize: Float) {
        if (hovertoFloatL(MElement.x + 5f, MElement.y + 5f, MElement.x + (105 * dropxsize), MElement.y + 35f, mouseX, mouseY, false) && Mouse.isButtonDown(0) && !openSearch) {
            hovermove = true
            if (!ismove) {
                lastmouseX = mouseX
                lastmousey = mouseY
                x = MElement.x
                y = MElement.y
                ismove = true
            }
        }

        if (hovermove) {
            if (!Mouse.isButtonDown(0)) {
                hovermove = false
                ismove = false
            }
            else {
                MElement.x = mouseX.toFloat() - (lastmouseX - x)
                MElement.y = mouseY.toFloat() - (lastmousey - y)
            }
        }

        val scaledResolution = ScaledResolution(mc)
        if (MElement.x <= 0) MElement.x = 0f
        if (MElement.y <= 0) MElement.y = 0f
        if (MElement.x + MElement.dropx >= scaledResolution.scaledWidth) MElement.x = scaledResolution.scaledWidth - MElement.dropx
        if (MElement.y + MElement.dropy >= scaledResolution.scaledHeight) MElement.y = scaledResolution.scaledHeight - MElement.dropy

        moveanimation()
    }

    fun moveanimation() {
        move.translate(MElement.x, MElement.y, 1.0)
        MElement.x = move.x.toInt().toFloat()
        MElement.y = move.y.toInt().toFloat()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {

        val scaledResolution = ScaledResolution(mc)
        Fonts.font18.drawStringWithShadow("[Ctrl + F] open Search", scaledResolution.scaledWidth / 2f - 37, scaledResolution.scaledHeight / 2f - 200, -1)

        dropManager(mouseX, mouseY)

        val dropxsize = MElement.dropx / 420
        val dropysize = MElement.dropy / 250

        MoveManager(mouseX, mouseY, dropxsize)

        if (hovertoFloatL(MElement.x + 8, MElement.y + 225 * dropysize, MElement.x + 102f * dropxsize, MElement.y + 243f * dropysize, mouseX, mouseY, true)) mc.displayGuiScreen(GuiHudDesigner())

        background(dropxsize, dropysize)

        RenderUtils.drawShader(MElement.x + 5, MElement.y + 5, 100f * dropxsize, 240f * dropysize)

        var categoryPosY = 0f
        for (category in ModuleCategory.values()) { //����
            CategoryElement.drawCategory(category, this, categoryPosY, dropxsize, dropysize, mouseX, mouseY)
            categoryPosY += 30
        }

        modulePosY = 0f
        MElement.Search = MElement.Search.replace(" ", "")
        modules = LiquidBounce.moduleManager.modules.filter {
            (MElement.Search == "showsettings" && it.showSettings) ||  ((it.name.indexOf(MElement.Search) != -1 || it.name.toLowerCase().indexOf(MElement.Search) != -1 || it.name == MElement.Search || it.name.toLowerCase() == MElement.Search) && MElement.Search.isNotEmpty()) || (it.category.displayName == MElement.hovercategory && MElement.Search.isEmpty()) || (MElement.Search == "enabler" && it.state)
        }.sortedBy { 0 }
        modules.forEachIndexed { _, module ->
            ModuleElement.drawModule(module, this, dropxsize, dropysize, mouseX, mouseY)
        }

        val v = ((225 * dropysize) * (abs(wheeltranslate.y) / modulePosY) * (modulePosY / (modulePosY - (245 * dropysize))))
        RenderUtils.drawShader(MElement.x + (407.25f * dropxsize), MElement.y + 4, 2f * dropxsize, 238f * dropysize)
        RenderUtils.drawRect(MElement.x + (407f * dropxsize), MElement.y + 4, MElement.x + (409f * dropxsize), MElement.y + (242f * dropysize), Color(15, 15, 15).rgb)
        RenderUtils.drawRect(MElement.x + (406f * dropxsize), MElement.y + (v + (5 * dropysize)), MElement.x + (410f * dropxsize), MElement.y + v + (15 * dropysize), Color(98, 98, 98).rgb)

        wheeltranslate.translate(0f, MElement.wheel)
        if (abs(MElement.wheel) > modulePosY - (245 * dropysize) && modulePosY > (245 * dropysize)) MElement.wheel = -(modulePosY - (245 * dropysize))
        if (hovertoFloatL(MElement.x + 105f, MElement.y + 5f, MElement.x + 410f, MElement.y + (245f * dropysize), mouseX, mouseY, false) && modulePosY > (245 * dropysize)) {
            val dWheel = Mouse.getDWheel()
            for (i in 0 until 10) {
                if (dWheel < 0 && abs(MElement.wheel) < modulePosY - (242 * dropysize)) {
                    MElement.wheel -= i * dropysize
                }
                else if (dWheel > 0) {
                    MElement.wheel += i * dropysize
                    if (MElement.wheel > 0) MElement.wheel = 0f
                }
            }
        }

        mouseLDown = Mouse.isButtonDown(0)
        mouseRDown = Mouse.isButtonDown(1)

        if (isCtrlKeyDown() && Keyboard.isKeyDown(Keyboard.KEY_F)) {
            openSearch = true
        }
        if (openSearch) {
            translate.translate(0f, 55f, 1.0)
        }
        else {
            translate.translate(0f, 0f, 1.0)
        }
        if (translate.y > 0) searchgui()

        if (hovermove) RenderUtils.drawRectBordered(
            MElement.x.toDouble() - 1, MElement.y.toDouble() - 1, MElement.x.toDouble() + MElement.dropx + 1, MElement.y.toDouble() + MElement.dropy + 1, 2.0, Color(35, 35, 35, 150).rgb, Color(75, 75, 75, 255).rgb
                                                   )
    }


    private fun searchgui() {
        val scaledResolution = ScaledResolution(mc)
        RenderUtils.drawFullCircle(scaledResolution.scaledWidth / 2f, scaledResolution.scaledHeight / 2f, translate.y * 10, 0f, Color(35, 35, 35, 150))
        glPushMatrix()
        val size = translate.y / 54f
        glTranslated(scaledResolution.scaledWidth / 2.0, scaledResolution.scaledHeight / 2.0, 0.0)
        glScalef(size, size, size)

        RenderUtils.drawRectBordered(-50.0, -7.0, 50.0, 7.0, 1.0, Color(43, 43, 43).rgb, Color(55, 55, 55).rgb)
        Fonts.font18.drawString(MElement.Search + " _", -45f, -2f, if (checkall) Color(255, 200, 0).rgb else -1)
        glPopMatrix()
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) { //�رմ���
        if (keyCode == Keyboard.KEY_ESCAPE && !openSearch) {
            mc.displayGuiScreen(null)
        }
        else if (keyCode == Keyboard.KEY_ESCAPE && openSearch) {
            openSearch = false

        }
        if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && openSearch) {
            MElement.Search = MElement.Search + typedChar
            MElement.wheel = 0f
        }

        if (keyCode == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown() && openSearch) {
            MElement.Search += getClipboardString()
        }

        if (keyCode == 14 && openSearch) {
            if (checkall) {
                MElement.Search = ""
                checkall = false
            }
            else {
                val length: Int = MElement.Search.length
                if (length != 0) {
                    MElement.Search = MElement.Search.substring(0, length - 1)
                }
            }
        }
        if (isCtrlKeyDown() && Keyboard.isKeyDown(Keyboard.KEY_A) && MElement.Search.length > 1) {
            checkall = !checkall
        }
    }

    private var openSearch = false
    private var checkall = false
    var mouseLDown = false
    private var mouseRDown = false


    fun hovertoFloatL(xOne: Float, yOne: Float, xTwo: Float, yTwo: Float, mouseX: Int, mouseY: Int, click: Boolean): Boolean {
        val hoverSystem = mouseX >= xOne && mouseX <= xTwo && mouseY >= yOne && mouseY <= yTwo
        return ((click && !mouseLDown && Mouse.isButtonDown(0) && hoverSystem) || (!click && hoverSystem)) && !openSearch
    }

    fun hovertoFloatR(xOne: Float, yOne: Float, xTwo: Float, yTwo: Float, mouseX: Int, mouseY: Int, click: Boolean): Boolean {
        val hoverSystem = mouseX >= xOne && mouseX <= xTwo && mouseY >= yOne && mouseY <= yTwo
        return ((click && !mouseRDown && Mouse.isButtonDown(1) && hoverSystem) || (!click && hoverSystem)) && !openSearch
    }
}
