package net.ccbluex.liquidbounce.ui.client.miscible.`package`

import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.ui.client.miscible.MElement
import net.ccbluex.liquidbounce.ui.client.miscible.Miscible
import net.ccbluex.liquidbounce.ui.font.Fonts
import me.aquavit.liquidsense.utils.render.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color

object CategoryElement {

    fun icon(name: String): String {
        when (name) {
            "combat" -> return "E"
            "player" -> return "G"
            "movement" -> return "F"
            "render" -> return "H"
            "world" -> return "C"
            "misc" -> return "B"
        }
        return ""
    }


    fun drawCategory(category: ModuleCategory, miscible: Miscible, categoryPosY: Float, dropxsize: Float, dropysize: Float, mouseX: Int, mouseY: Int) {
        val yes = miscible.hovertoFloatL(MElement.x + 5f, MElement.y + 45 + categoryPosY, MElement.x + (105f * dropxsize), MElement.y + (225 * dropysize), mouseX, mouseY, false)

        if (miscible.hovertoFloatL(MElement.x + 5f, MElement.y + 45 + categoryPosY, MElement.x + (105f * dropxsize), MElement.y + 75 + categoryPosY, mouseX, mouseY, true) && yes) {
            MElement.hovercategory = category.displayName
            MElement.wheel = 0f
        }

        GlStateManager.pushMatrix()
        RenderUtils.makeScissorBox(MElement.x + 5f, MElement.y + 45 + categoryPosY, MElement.x + (105f * dropxsize), MElement.y + (225 * dropysize))
        GL11.glEnable(GL11.GL_SCISSOR_TEST)

        if (MElement.hovercategory == category.displayName)
            RenderUtils.drawNLRect(MElement.x + 5f, MElement.y + 40 + categoryPosY, MElement.x + (100f * dropxsize).toInt() + 5, MElement.y + 65 + categoryPosY, 2.0f, Color(50, 50, 50, 200).darker().rgb)

        Fonts.icon30.drawString(icon(category.displayName.toLowerCase()), MElement.x + 14.5f, MElement.y + 48 + categoryPosY, if (MElement.hovercategory == category.displayName) Color(255, 255, 255).rgb else Color(150, 150, 150).rgb) //displayName
        Fonts.font17.drawString(category.displayName, MElement.x + 34.5f, MElement.y + 50 + categoryPosY, if (MElement.hovercategory == category.displayName) Color(255, 255, 255).rgb else Color(150, 150, 150).rgb) // ѡ�����
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        GlStateManager.popMatrix()
    }
}