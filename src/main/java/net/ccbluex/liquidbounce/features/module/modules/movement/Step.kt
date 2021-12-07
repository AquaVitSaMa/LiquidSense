/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import me.aquavit.liquidsense.event.EventTarget
import me.aquavit.liquidsense.event.events.StepConfirmEvent
import me.aquavit.liquidsense.event.events.StepEvent
import me.aquavit.liquidsense.event.events.UpdateEvent
import me.aquavit.liquidsense.utils.entity.MovementUtils
import me.aquavit.liquidsense.utils.timer.MSTimer
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.exploit.Phase
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

@ModuleInfo(name = "Step", description = "Allows you to step up blocks.", category = ModuleCategory.MOVEMENT)
class Step : Module() {

    /**
     * OPTIONS
     */
    private val modeValue = ListValue("Mode", arrayOf("Vanilla", "NCP", "MotionNCP"), "NCP")
    private val heightValue = FloatValue("Height", 1F, 0.6F, 10F)
    private val timerValue = FloatValue("Timer", 0.5f, 0.1f, 1f)
    private val delayValue = IntegerValue("Delay", 0, 0, 500)

    /**
     * VALUES
     */
    var isStep = false
    private var posX = 0.0
    private var posY = 0.0
    private var posZ = 0.0

    private val timer = MSTimer()

    override fun onDisable() {
        val thePlayer = mc.thePlayer ?: return

        // Change step height back to default (0.5 is default)
        thePlayer.stepHeight = 0.5F
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!isStep && posY == BigDecimal(posY).setScale(3, RoundingMode.HALF_DOWN).toDouble())
            mc.timer.timerSpeed = 1.0f
    }

    @EventTarget
    fun onStep(event: StepEvent) {
        val thePlayer = mc.thePlayer ?: return

        // Phase should disable step
        if (LiquidBounce.moduleManager[Phase::class.java]!!.state) {
            event.stepHeight = 0F
            return
        }

        // Some fly modes should disable step
        val fly = LiquidBounce.moduleManager[Fly::class.java] as Fly
        if (fly.state) {
            val flyMode = fly.modeValue.get()

            if (flyMode.equals("Hypixel", ignoreCase = true) || flyMode.equals("Fast", ignoreCase = true) && mc.thePlayer.inventory.getCurrentItem() == null) {
                event.stepHeight = 0F
                return
            }
        }

        // Set step to default in some cases
        if (!thePlayer.onGround || !timer.hasTimePassed(delayValue.get().toLong())) {
            thePlayer.stepHeight = 0.5F
            event.stepHeight = 0.5F
            return
        }

        // Set step height
        thePlayer.stepHeight = heightValue.get()
        event.stepHeight = heightValue.get()

        // Detect possible step
        if (event.stepHeight > 0.5F) {
            isStep = true
            posX = thePlayer.posX
            posY = thePlayer.posY
            posZ = thePlayer.posZ
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onStepConfirm(event: StepConfirmEvent) {
        val thePlayer = mc.thePlayer ?: return
        val height = thePlayer.entityBoundingBox.minY - thePlayer.posY

        if (!isStep) // Check if step
            return

        if (height > 0.5) { // Check if full block step
            val mode = modeValue.get()

            when {
                mode.equals("NCP", ignoreCase = true) -> {
                    MovementUtils.fakeJump()
                    // Half legit step (1 packet missing) [COULD TRIGGER TOO MANY PACKETS]
                    mc.timer.timerSpeed = (timerValue.get() - if (height >= 1) abs(0.6f - height.toFloat()) * 0.25f else 0.1f).coerceAtLeast(0.1f)
                    ncpOffsets(height)
                    timer.reset()
                }
            }
        }

        isStep = false
        posX = 0.0
        posY = 0.0
        posZ = 0.0
    }

    private fun ncpOffsets(height: Double) {
        when {
            height < 1.1 -> {
                var first = 0.41999998688698
                var second = 0.75

                if (height != 1.0) {
                    first *= height
                    second *= height
                    if (first > 0.425) {
                        first = 0.425
                    }
                    if (second > 0.78) {
                        second = 0.78
                    }
                    if (second < 0.49) {
                        second = 0.49
                    }
                }

                mc.netHandler.addToSendQueue(C04PacketPlayerPosition(posX, posY + first, posZ, false))

                if (posY + second < posY + height)
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(posX, posY + second, posZ, false))

                return
            }

            height < 1.6 -> {
                val heights = doubleArrayOf(0.4199999465123445, 0.7531999761231256, 1.00133512465, 1.060831264587, 0.982431237844645)
                for (off in heights) {
                    mc.thePlayer.sendQueue.addToSendQueue(C04PacketPlayerPosition(posX, posY + off, posZ, false))
                }
            }

            height < 2.1 -> {
                val heights = doubleArrayOf(0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869)
                for (off in heights) {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(posX, posY + off, posZ, false))
                }

                return
            }

            else -> {
                val heights = doubleArrayOf(0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907)
                for (off in heights) {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(posX, posY + off, posZ, false))
                }
            }
        }
    }

    override fun getTag(): String {
        return modeValue.get()
    }
}