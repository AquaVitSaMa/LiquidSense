/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.event

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ChatLine
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.model.ModelPlayer
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.Packet
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.IChatComponent
import net.minecraft.world.IBlockAccess

class ChatComponentEvent(val component: IChatComponent,var chatLineId: List<ChatLine>) : CancellableEvent()

/**
 * Called when an other entity moves
 */
data class EntityMovementEvent(val movedEntity: Entity) : Event()

/**
 * Called when player jumps
 *
 * @param motion jump motion (y motion)
 */
class JumpEvent(var motion: Float) : CancellableEvent()

/**
 * Called when user press a key once
 *
 * @param key Pressed key
 */
class KeyEvent(val key: Int) : Event()

/**
 * Called in "onUpdateWalkingPlayer"
 *
 * @param eventState PRE or POST
 */
class MotionEvent(val eventState: EventState) : Event()

/**
 * Called in "onLivingUpdate" when the player is using a use item.
 *
 * @param strafe the applied strafe slow down
 * @param forward the applied forward slow down
 */
class SlowDownEvent(var strafe: Float, var forward: Float) : Event()

/**
 * Called in "moveFlying"
 */
class StrafeEvent(val strafe: Float, val forward: Float, val friction: Float) : CancellableEvent()

/**
 * Called when player moves
 *
 * @param x motion
 * @param y motion
 * @param z motion
 */
class MoveEvent(var x: Double, var y: Double, var z: Double) : CancellableEvent() {
    var isSafeWalk = false

    fun zero() {
        x = 0.0
        y = 0.0
        z = 0.0
    }

    fun zeroXZ() {
        x = 0.0
        z = 0.0
    }
}

/**
 * Called when receive or send a packet
 */
class PacketEvent(val packet: Packet<*>,val eventType: EventType) : CancellableEvent()

/**
 * Called when screen is going to be rendered
 */
class Render2DEvent(val partialTicks: Float) : Event()
/**
 * shabi
 */
class BlockRenderSideEvent(val world: IBlockAccess, val pos: BlockPos, val side: EnumFacing, val maxX: Double, val minX: Double, val maxY: Double, val minY: Double, val maxZ: Double, val minZ: Double) : Event()

/**
 * Called when world is going to be rendered
 */
class Render3DEvent(val partialTicks: Float) : Event()

/**
 * shabi
 */

class UpdateModelEvent(val player: EntityPlayer, val model: ModelPlayer) : Event()


/**
 * Called when entity is going to be rendered
 */
class RenderEntityEvent(val entity: Entity, val x: Double, val y: Double, val z: Double, val entityYaw: Float,
                        val partialTicks: Float) : Event()

/**
 * Called when the screen changes
 */
class ScreenEvent(val guiScreen: GuiScreen?) : Event()

class PacketSenEvent(val packet: Packet<*>) : CancellableEvent()

/**
 * Ni kan ni ma ne?
 */

/**
 * Called when player is going to step
 */
class StepEvent(var stepHeight: Float) : Event()

/**
 * Called when player step is confirmed
 */
class StepConfirmEvent(var stepHeight: Float) : Event()

/**
 * Called when a text is going to be rendered
 */
class TextEvent(var text: String?) : Event()

/**
 * Called when the world changes
 */
class WorldEvent(val worldClient: WorldClient?) : Event()

/**
 * Called when window clicked
 */
class ClickWindowEvent(val windowId: Int, val slotId: Int, val mouseButtonClicked: Int, val mode: Int) : CancellableEvent()

class WallDamageEvent : CancellableEvent()