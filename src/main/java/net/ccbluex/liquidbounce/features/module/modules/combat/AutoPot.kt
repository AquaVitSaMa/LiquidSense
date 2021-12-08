/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import me.aquavit.liquidsense.modules.combat.Aura
import net.ccbluex.liquidbounce.LiquidBounce
import me.aquavit.liquidsense.event.EventState.PRE
import me.aquavit.liquidsense.event.EventTarget
import me.aquavit.liquidsense.event.events.MotionEvent
import me.aquavit.liquidsense.event.events.UpdateEvent
import me.aquavit.liquidsense.utils.client.InventoryUtils
import me.aquavit.liquidsense.utils.client.Rotation
import me.aquavit.liquidsense.utils.client.RotationUtils
import me.aquavit.liquidsense.utils.entity.MovementUtils
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import me.aquavit.liquidsense.utils.misc.RandomUtils
import me.aquavit.liquidsense.utils.timer.MSTimer
import me.aquavit.liquidsense.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.features.module.modules.player.Blink
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.extend.ColorType
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.extend.Notification
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.item.ItemPotion
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.*
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.AxisAlignedBB

@ModuleInfo(name = "AutoPot", description = "mum,i want to be supper skidder", category = ModuleCategory.COMBAT)
class AutoPot : Module() {
    private val regenValue = BoolValue("Regen", true)
    private val speedValue = BoolValue("Speed", true)
    private val noMoveValue = BoolValue("NoMove", true)
    private val predictValue = BoolValue("Predict", false)
    private val tagValue = BoolValue("Tag", true)
    private val delayValue = IntegerValue("Delay", 150, 0, 500)
    private val healthValue = IntegerValue("Health", 16, 1, 40).displayable { regenValue.get() }

    var potting = false
    private var slot = 0
    private var last = 0

    private var timer = MSTimer()

    override fun onEnable() {
        super.onEnable()
        potting = false
        slot = -1
        last = -1
        timer.reset()
    }

    override fun onDisable() {
        super.onDisable()
        potting = false
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
            if (LiquidBounce.moduleManager.getModule(Blink::class.java)!!.state || LiquidBounce.moduleManager.getModule(Fly::class.java)!!.state || LiquidBounce.moduleManager.getModule(Scaffold::class.java)!!.state || checkVoid() || mc.thePlayer.isEating)
            return

        slot = getSlot()

        if (!timer.hasTimePassed(delayValue.get().toLong()) && (!noMoveValue.get() || !MovementUtils.isMoving())) {
            val regenId = Potion.regeneration.getId()
            if (!mc.thePlayer.isPotionActive(regenId) && !potting && mc.thePlayer.onGround && regenValue.get() && mc.thePlayer.health <= healthValue.value.toDouble() && hasPot(regenId)) {
                val cum = hasPot(regenId, slot)
                if (cum != -1)
                    swap(cum, slot)
                last = mc.thePlayer.inventory.currentItem
                mc.thePlayer.inventory.currentItem = slot
                //event.pitch = if (MovementUtils.isMoving()) 85f else 90f
                mc.thePlayer.sendQueue.addToSendQueue(C03PacketPlayer.C05PacketPlayerLook(rotations[0], rotations[1], mc.thePlayer.onGround))

                potting = true
                LiquidBounce.hud.addNotification(Notification("AutoPot", "Spilled §d§oregen§r potion", ColorType.INFO, 750, 375))
                timer.reset()
            }

            val speedId = Potion.moveSpeed.getId()
            if (!mc.thePlayer.isPotionActive(speedId) && !potting && mc.thePlayer.onGround && speedValue.get() && hasPot(speedId)) {
                val cum = hasPot(speedId, slot)
                if (cum != -1)
                    swap(cum, slot)
                last = mc.thePlayer.inventory.currentItem
                mc.thePlayer.inventory.currentItem = slot
                //event.pitch = if (MovementUtils.isMoving()) 85f else 90f
                mc.thePlayer.sendQueue.addToSendQueue(C03PacketPlayer.C05PacketPlayerLook(rotations[0], rotations[1], mc.thePlayer.onGround))

                potting = true
                LiquidBounce.hud.addNotification(Notification("AutoPot", "Spilled §b§ospeed§r potion", ColorType.INFO, 750, 375))
                timer.reset()
            }
        }

        if (potting) {
            if (mc.thePlayer.inventory.getCurrentItem() != null && mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem())) {
                mc.entityRenderer.itemRenderer.resetEquippedProgress2()
            }

            if (last != -1)
                mc.thePlayer.inventory.currentItem = last

            potting = false
            last = -1
        }
    }

    val rotations: FloatArray
        get() {
            val movedPosX = mc.thePlayer.posX + mc.thePlayer.motionX * 26.0
            val movedPosY = mc.thePlayer.entityBoundingBox.minY - 3.6
            val movedPosZ = mc.thePlayer.posZ + mc.thePlayer.motionZ * 26.0
            return if (predictValue.get()) RotationUtils.getRotationFromPosition(movedPosX, movedPosZ, movedPosY) else floatArrayOf(mc.thePlayer.rotationYaw, 90f)
        }

    private fun hasPot(id: Int, targetSlot: Int): Int {
        for (i in 9..44) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).hasStack) {
                val `is` = mc.thePlayer.inventoryContainer.getSlot(i).stack
                if (`is`.item is ItemPotion) {
                    val pot = `is`.item as ItemPotion
                    if (pot.getEffects(`is`).isEmpty()) continue
                    val effect = pot.getEffects(`is`)[0]
                    if (effect.potionID == id) {
                        if (ItemPotion.isSplash(`is`.itemDamage) && isBestPot(pot, `is`)) {
                            if (36 + targetSlot != i) return i
                        }
                    }
                }
            }
        }
        return -1
    }

    private fun hasPot(id: Int): Boolean {
        for (i in 9..44) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).hasStack) {
                val `is` = mc.thePlayer.inventoryContainer.getSlot(i).stack
                if (`is`.item is ItemPotion) {
                    val pot = `is`.item as ItemPotion
                    if (pot.getEffects(`is`).isEmpty()) continue
                    val effect = pot.getEffects(`is`)[0]
                    if (effect.potionID == id) {
                        if (ItemPotion.isSplash(`is`.itemDamage) && isBestPot(pot, `is`)) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    private fun isBestPot(potion: ItemPotion, stack: ItemStack): Boolean {
        if (potion.getEffects(stack) == null || potion.getEffects(stack).size != 1) return false
        val effect = potion.getEffects(stack)[0] as PotionEffect
        val potionID = effect.potionID
        val amplifier = effect.amplifier
        val duration = effect.duration

        for (i in 9..44) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).hasStack) {
                val `is` = mc.thePlayer.inventoryContainer.getSlot(i).stack
                if (`is`.item is ItemPotion) {
                    val pot = `is`.item as ItemPotion
                    if (pot.getEffects(`is`) != null) {
                        for (o in pot.getEffects(`is`)) {
                            val effects = o as PotionEffect
                            val id = effects.potionID
                            val ampl = effects.amplifier
                            val dur = effects.duration
                            if (id == potionID && ItemPotion.isSplash(`is`.itemDamage)) {
                                if (ampl > amplifier) {
                                    return false
                                } else if (ampl == amplifier && dur > duration) {
                                    return false
                                }
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    private fun getSlot(): Int {
        var spoofSlot = 4
        for (i in 36..44) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).hasStack) {
                spoofSlot = i - 36
                break
            } else if (mc.thePlayer.inventoryContainer.getSlot(i).stack.item is ItemPotion) {
                spoofSlot = i - 36
                break
            }
        }

        return spoofSlot
    }

    private fun swap(currentSlot: Int, targetSlot: Int) {
        mc.playerController.windowClick(
                mc.thePlayer.inventoryContainer.windowId,
                currentSlot,
                targetSlot,
                2,
                mc.thePlayer
        )
    }

    private fun isStackSplashSpeedPot(stack: ItemStack?): Boolean {
        if (stack == null) {
            return false
        }
        if (stack.item is ItemPotion) {
            val potion = stack.item as ItemPotion
            if (ItemPotion.isSplash(stack.itemDamage)) {
                for (o in potion.getEffects(stack)) {
                    val effect = o as PotionEffect
                    if (stack.displayName.contains("Frog"))
                        return false
                    if (effect.potionID == Potion.moveSpeed.id && effect.potionID != Potion.jump.id) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun isStackSplashHealthPot(stack: ItemStack?): Boolean {
        if (stack == null) {
            return false
        }
        if (stack.item is ItemPotion) {
            val potion = stack.item as ItemPotion
            if (ItemPotion.isSplash(stack.itemDamage)) {
                for (o in potion.getEffects(stack)) {
                    val effect = o as PotionEffect
                    if (effect.potionID == Potion.heal.id) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun isStackSplashRegenPot(stack: ItemStack?): Boolean {
        if (stack == null) {
            return false
        }
        if (stack.item is ItemPotion) {
            val potion = stack.item as ItemPotion
            if (ItemPotion.isSplash(stack.itemDamage)) {
                for (o in potion.getEffects(stack)) {
                    val effect = o as PotionEffect
                    if (effect.potionID == Potion.regeneration.id) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun checkVoid(): Boolean {
        for (x in -1..1) {
            for (z in -1..1) {
                if (isVoid(x.toDouble(), z.toDouble()) ) {
                    return true
                }
            }
        }
        return false
    }

    private fun isVoid(X: Double, Z: Double): Boolean {
        val fly = LiquidBounce.moduleManager.getModule(Fly::class.java) as Fly
        val thePlayer = mc.thePlayer!!

        if (fly.state) return false
        if (mc.thePlayer.posY < 0.0) return true

        var off = 0.0
        while (off < thePlayer.posY.toInt() + 2) {
            val bb: AxisAlignedBB = thePlayer.entityBoundingBox.offset(X, -off, Z)
            if (mc.theWorld.getCollidingBoundingBoxes(thePlayer, bb).isEmpty()) {
                off += 2
                continue
            }
            off += 2
            return false
        }
        return true
    }

    private fun getPotCount(): Int {
        var count = 0
        for (i in 0..44) {
            val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack ?: continue
            if (isStackSplashSpeedPot(stack) || isStackSplashHealthPot(stack) || isStackSplashHealthPot(stack) || isStackSplashRegenPot(stack)) count++
        }
        return count
    }

    override fun getTag(): String {
        return if (tagValue.get()) getPotCount().toString() else ""
    }
}