/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import me.aquavit.liquidsense.modules.`fun`.Derp
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.Aura
import net.ccbluex.liquidbounce.features.module.modules.combat.BowAimbot
import net.ccbluex.liquidbounce.features.module.modules.world.ChestAura
import net.ccbluex.liquidbounce.features.module.modules.world.Fucker
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.features.module.modules.world.Tower
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.network.play.client.C03PacketPlayer

@ModuleInfo(name = "Rotations", description = "Allows you to see server-sided head and body rotations.", category = ModuleCategory.RENDER)
class Rotations : Module() {

    private val modeValue = ListValue("Mode", arrayOf("LiquidBounce", "Other","Ghost"), "Other")
    public val colorRedValue = IntegerValue("R", 0, 0, 255)
    public val colorGreenValue = IntegerValue("G", 160, 0, 255)
    public val colorBlueValue = IntegerValue("B", 255, 0, 255)
    public val alphaValue = IntegerValue("Alpha", 255, 0, 255)
    public val rainbow = BoolValue("RainBow", false)



    fun getModeValue(): ListValue? {
        return modeValue
    }

    private var playerYaw: Float? = null

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (modeValue.get().equals("Other", ignoreCase = true) || modeValue.get().equals("LiquidBounce", ignoreCase = true)){
            if (RotationUtils.serverRotation != null) {
                mc.thePlayer.rotationYawHead = RotationUtils.serverRotation.yaw
            }
        }
        if (modeValue.get().equals("Other", ignoreCase = true) && LiquidBounce.moduleManager.getModule(Aura::class.java)!!.state) {
            mc.thePlayer.rotationYawHead = RotationUtils.serverRotation.yaw
            mc.thePlayer.renderYawOffset = RotationUtils.serverRotation.yaw
        }


    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (modeValue.get().equals("LiquidBounce", ignoreCase = true)) {
            if (!shouldRotate() || mc.thePlayer == null)
                return

            val packet = event.packet
            if (packet is C03PacketPlayer.C06PacketPlayerPosLook || packet is C03PacketPlayer.C05PacketPlayerLook) {
                playerYaw = (packet as C03PacketPlayer).yaw
                mc.thePlayer.renderYawOffset = packet.getYaw()
                mc.thePlayer.rotationYawHead = packet.getYaw()
            } else {
                if (playerYaw != null)
                    mc.thePlayer.renderYawOffset = this.playerYaw!!
                mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset
            }
        }

    }

    private fun getState(module: Class<*>) = LiquidBounce.moduleManager[module]!!.state

    private fun shouldRotate(): Boolean {
        val killAura = LiquidBounce.moduleManager.getModule(Aura::class.java) as Aura
        return getState(Scaffold::class.java) || getState(Tower::class.java) ||
                (getState(Aura::class.java) && killAura.target != null) ||
                getState(Derp::class.java) || getState(BowAimbot::class.java) ||
                getState(Fucker::class.java) ||
                getState(ChestAura::class.java)
    }

    override val tag: String?
        get() = modeValue.get()
}
