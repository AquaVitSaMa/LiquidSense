/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element

import me.aquavit.liquidsense.utils.mc.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.Value
import net.minecraft.client.gui.ScaledResolution
import kotlin.math.max
import kotlin.math.min

/**
 * CustomHUD element
 */
abstract class Element(var x: Double = 2.0, var y: Double = 2.0, scale: Float = 1F, var side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.UP)) : MinecraftInstance() {
    val info = javaClass.getAnnotation(ElementInfo::class.java)
            ?: throw IllegalArgumentException("Passed element with missing element info")

    var scale: Float = 1F
        set(value) {
            if (info.disableScale)
                return

            field = value
        }
        get() {
            if (info.disableScale)
                return 1.0f
            return field
        }

    init {
        this.scale = scale
    }

    val name: String
        get() = info.name

    var renderX: Double
        get() = when (side.horizontal) {
            Side.Horizontal.LEFT -> x
            Side.Horizontal.MIDDLE -> (ScaledResolution(mc).scaledWidth / 2) - x
            Side.Horizontal.RIGHT -> ScaledResolution(mc).scaledWidth - x
        }
        set(value) = when (side.horizontal) {
            Side.Horizontal.LEFT -> {
                x += value
            }
            Side.Horizontal.MIDDLE, Side.Horizontal.RIGHT -> {
                x -= value
            }
        }

    var renderY: Double
        get() = when (side.vertical) {
            Side.Vertical.UP -> y
            Side.Vertical.MIDDLE -> (ScaledResolution(mc).scaledHeight / 2) - y
            Side.Vertical.DOWN -> ScaledResolution(mc).scaledHeight - y
        }
        set(value) = when (side.vertical) {
            Side.Vertical.UP -> {
                y += value
            }
            Side.Vertical.MIDDLE, Side.Vertical.DOWN -> {
                y -= value
            }
        }

    var border: Border? = null

    var drag = false
    var prevMouseX = 0F
    var prevMouseY = 0F

    /**
     * Get all values of element
     */

    open val values: List<Value<*>>
        get() = javaClass.declaredFields.map { valueField ->
            valueField.isAccessible = true
            valueField[this]
        }.filterIsInstance<Value<*>>()

    /**
     * Called when element created
     */
    open fun createElement() = true

    /**
     * Called when element destroyed
     */
    open fun destroyElement() {}

    /**
     * Draw element
     */
    abstract fun drawElement(): Border?

    /**
     * Update element
     */
    open fun updateElement() {}

    /**
     * LivingUpdate element
     */
    open fun livingupdateElement(){}

    /**
     * Check if [x] and [y] is in element border
     */
    open fun isInBorder(x: Double, y: Double): Boolean {
        val border = border ?: return false

        val minX = min(border.x, border.x2)
        val minY = min(border.y, border.y2)

        val maxX = max(border.x, border.x2)
        val maxY = max(border.y, border.y2)

        return minX <= x && minY <= y && maxX >= x && maxY >= y
    }

    /**
     * Called when mouse clicked
     */
    open fun handleMouseClick(x: Double, y: Double, mouseButton: Int) {}

    /**
     * Called when key pressed
     */
    open fun handleKey(c: Char, keyCode: Int) {}

}