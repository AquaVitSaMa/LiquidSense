package net.ccbluex.liquidbounce.ui.client.miscible.`package`

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.ui.client.miscible.MElement
import net.ccbluex.liquidbounce.ui.client.miscible.Miscible
import net.ccbluex.liquidbounce.ui.font.Fonts
import me.aquavit.liquidsense.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.Value
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color

object ModuleElement {

    val mc = Minecraft.getMinecraft()

    fun drawModule(module: Module, miscible: Miscible, dropxsize: Float, dropysize: Float, mouseX: Int, mouseY: Int
    ) {

        if (module.click != module.clickAnimation.x) module.clickAnimation.translate(
                       module.click, 0f, 1.1
        )

        if (MElement.Search.isEmpty() && (MElement.y + 45.0 + miscible.modulePosY + module.openValue.x + miscible.wheeltranslate.y > MElement.y + 5f || MElement.y + 5.0 + miscible.modulePosY + miscible.wheeltranslate.y > MElement.y + (245f * dropysize)) || MElement.Search.isNotEmpty()) {

            GL11.glPushMatrix()
            RenderUtils.makeScissorBox(
                           MElement.x + (110f * dropxsize), MElement.y + 5f, MElement.x + (405f * dropxsize), MElement.y + (245f * dropysize)
            )
            GL11.glEnable(GL11.GL_SCISSOR_TEST)

            //ģ�鱳����Ӱ
            if (module.openValue.x.equals(0f)) RenderUtils.drawShader(
                           MElement.x + (115f * dropxsize), MElement.y + 5f + miscible.modulePosY + miscible.wheeltranslate.y, 285f * dropxsize, 40f + module.openValue.x
            )

            // ģ�鱳��
            RenderUtils.drawRectBordered(
                           MElement.x.toDouble() + (115.0 * dropxsize).toInt(), MElement.y + 5.0 + miscible.modulePosY + miscible.wheeltranslate.y, MElement.x + (400.0 * dropxsize), MElement.y + 45.0 + miscible.modulePosY + module.openValue.x + miscible.wheeltranslate.y,
                           if (module.openValue.y > 0) 0.5 else 0.0, if (module.state && !module.showSettings) Color(
                           41, 44, 48
            ).rgb
            else Color(30, 30, 30).rgb, Color(55, 55, 55).rgb
            )

            //ģ�鶯��
            if (module.clickAnimation.x < 141 && module.clickAnimation.x > 1) RenderUtils.drawRect(
                           MElement.x + (257.5f - if (module.state) module.clickAnimation.x else (142.5f - module.clickAnimation.x)) * dropxsize, MElement.y + 5f + miscible.modulePosY + miscible.wheeltranslate.y,
                           MElement.x + (257.5f + if (module.state) module.clickAnimation.x else (142.5f - module.clickAnimation.x)) * dropxsize, MElement.y + 45f + miscible.modulePosY + miscible.wheeltranslate.y, Color(255, 255, 255, 30).rgb
            )

            val moduleValues: List<Value<*>> = module.values

            //��������
            Fonts.font18.drawString(
                           module.name, MElement.x + (126f * dropxsize).toInt(), MElement.y + 10f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), if (module.state) -1 else Color(175, 175, 175).rgb
            )

            // ģ�����
            Fonts.font17.drawString(
                           module.description, MElement.x + (126f * dropxsize).toInt(), MElement.y + 20f + miscible.modulePosY + miscible.wheeltranslate.y.toInt(), Color(75, 75, 75).rgb
            )

            //ģ�鿪�ر���
            RenderUtils.drawNLRect(
                           MElement.x + (125.0f * dropxsize), MElement.y + 28.0f + miscible.modulePosY + miscible.wheeltranslate.y, MElement.x + (174.0f * dropxsize), MElement.y + 42.0f + miscible.modulePosY + miscible.wheeltranslate.y, 2f,
                           if (module.state) Color(26, 29, 34).rgb else Color(55, 55, 55).rgb
            )

            //ģ�鿪�ر�ǩ
            Fonts.font16.drawCenteredString(
                           if (module.state) "ENABLED" else "DISABLED", MElement.x + (149f * dropxsize).toInt(), MElement.y + 33f + miscible.modulePosY + miscible.wheeltranslate.y, if (module.state) Color(72, 131, 255).rgb else Color(35, 35, 35).rgb, false
            )

            //������ǩ
            if (module.arrayListName != module.name) {
                RenderUtils.drawNLRect(
                               MElement.x + (184.0f * dropxsize), MElement.y + 28.0f + miscible.modulePosY + miscible.wheeltranslate.y, MElement.x + (184.0f * dropxsize) + Fonts.font30.getStringWidth(
                               module.arrayListName.toUpperCase()
                ) + 14, MElement.y + 42.0f + miscible.modulePosY + miscible.wheeltranslate.y, 2f, Color(26, 29, 34).rgb
                )
                Fonts.font30.drawString(
                               module.arrayListName.toUpperCase(), MElement.x + (185.0f * dropxsize).toInt() + 6, MElement.y + 32.5f + miscible.modulePosY + miscible.wheeltranslate.y, Color(200, 200, 200).rgb, false
                )
            }

            val hoverandLClick = miscible.hovertoFloatL(
                           MElement.x + (110f * dropxsize), MElement.y + 5f, MElement.x + (405f * dropxsize), MElement.y + (245f * dropysize), mouseX, mouseY, false
            )
            if (hoverandLClick) {
                if (miscible.hovertoFloatL(
                                   MElement.x + (115f * dropxsize), MElement.y + 5.0f + miscible.modulePosY + miscible.wheeltranslate.y, MElement.x + (400f * dropxsize), MElement.y + 45.0f + miscible.modulePosY + miscible.wheeltranslate.y, mouseX, mouseY, true
                    )) {
                    module.toggle()
                    mc.soundHandler.playSound(
                                   PositionedSoundRecord.create(
                                                  ResourceLocation("gui.button.press"), 1f
                                   )
                    )
                }
                if (miscible.hovertoFloatR(
                                   MElement.x + (115f * dropxsize), MElement.y + 5.0f + miscible.modulePosY + miscible.wheeltranslate.y, MElement.x + (400f * dropxsize), MElement.y + 45.0f + miscible.modulePosY + miscible.wheeltranslate.y, mouseX, mouseY, true
                    ) && moduleValues.isNotEmpty()) {

                    module.click = 0f
                    module.openValueposy = 0f
                    module.toggleShowSettings()
                    MElement.modulestetting = module.name
                    mc.soundHandler.playSound(
                                   PositionedSoundRecord.create(
                                                  ResourceLocation("gui.button.press"), 5f
                                   )
                    )
                }
                if (moduleValues.isEmpty()) {
                    module.showSettings = false
                }
            }

            if (module.state) {
                module.click = 142.5f
            }
            else {
                module.click = 0f
            }

            //toggle
            module.guiposY = 0f
            for (value in module.values) {
                if (!value.displayable) continue
                ValueElement.drawValue(
                               value, module, miscible, module.openValue.y, dropxsize, dropysize, mouseX, mouseY
                )
            }

            if (module.showSettings) {
                module.openValueposy = 21f
                module.suckDown = module.guiposY
                module.openValue.translate(module.suckDown, module.openValueposy)
            }
            else {
                module.openValueposy = 0f
                module.suckDown = 0f
                module.openValue.translate(module.suckDown, module.openValueposy)
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST)
            GL11.glPopMatrix()
        }
        miscible.modulePosY += 49
    }
}