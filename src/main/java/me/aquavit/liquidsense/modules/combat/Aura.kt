package me.aquavit.liquidsense.modules.combat

import me.aquavit.liquidsense.modules.misc.Teams
import me.aquavit.liquidsense.utils.entity.EntityUtils
import me.aquavit.liquidsense.utils.extensions.PlayerExtensionUtils
import me.aquavit.liquidsense.utils.misc.RandomUtils
import me.aquavit.liquidsense.utils.client.RotationUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot
import net.ccbluex.liquidbounce.features.module.modules.player.Blink
import net.ccbluex.liquidbounce.features.module.modules.render.FreeCam
import me.aquavit.liquidsense.utils.entity.RaycastUtils
import me.aquavit.liquidsense.utils.timer.MSTimer
import me.aquavit.liquidsense.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.event.events.AttackEvent
import net.ccbluex.liquidbounce.event.events.MotionEvent
import net.ccbluex.liquidbounce.event.events.StrafeEvent
import net.ccbluex.liquidbounce.event.events.UpdateEvent
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.boss.EntityWither
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.*
import net.minecraft.potion.Potion
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper
import net.minecraft.world.WorldSettings
import org.lwjgl.input.Keyboard
import java.util.*
import kotlin.math.max
import kotlin.math.min

@ModuleInfo(name = "Aura", description = "Automatically attacks targets around you.", category = ModuleCategory.COMBAT, keyBind = Keyboard.KEY_R)
class Aura : Module() {

    // 点击速度
    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 10, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), this.get())
        }
    }
    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 8, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
        }
    }
    // 伤害时间
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    // NCP更多设置
    val hitBoxValue = FloatValue("HitBox", 0.4f, 0.0f, 0.4f).displayable{modeValue.get() == "NCP"}
    // 距离
    private val rangeValue = FloatValue("Range", 4.6f, 1f, 6f)
    private val blockRangeValue = FloatValue("BlockRange", 8.0f, 1f, 12f)
    private val throughWallsRangeValue = FloatValue("ThroughWallsRange", 4.0f, 0f, 6f)
    // 锁敌模式
    private val priorityValue = ListValue("Priority", arrayOf("Health", "Distance", "Direction", "LivingTime", "Armor", "HurtTime", "HurtResistantTime"), "Distance")
    private val targetModeValue = ListValue("TargetMode", arrayOf("Single", "Switch", "Multi"), "Switch")
    // Switch更多设置
    private val switchDelayValue = IntegerValue("SwitchDelay", 250, 0, 2000).displayable{targetModeValue.get() == "Switch"}
    // Multi更多设置
    private val limitedMultiTargetsValue = IntegerValue("LimitedMultiTargets", 0, 0, 50).displayable{targetModeValue.get() == "Multi"}
    // 摇头速度
    private val maxTurnSpeed: IntegerValue = object : IntegerValue("MaxTurnSpeed", 180, 0, 180) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minTurnSpeed.get()
            if (i > newValue) set(i)
        }
    }
    private val minTurnSpeed: IntegerValue = object : IntegerValue("MinTurnSpeed", 180, 0, 180) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxTurnSpeed.get()
            if (i < newValue) set(i)
        }
    }
    // 摇头
    private val rotationMode = ListValue("RotationMode", arrayOf("Vanilla", "RayCast", "BackTrack", "None"), "Vanilla")
    private val silentRotationValue = BoolValue("SilentRotation", true)
    private val faceHitableValue = BoolValue("FaceHitable", false).displayable{rotationMode.get() != "None" || !raycastValue.get()}
    // AAC摇头设置
    private val rotationStrafeValue = ListValue("Strafe", arrayOf("Off", "Strict", "Silent"), "Off").displayable{modeValue.get() == "AAC"}
    private val randomCenterValue = BoolValue("RandomCenter", true).displayable{modeValue.get() == "AAC"}
    private val outborderValue = BoolValue("Outborder", false).displayable{modeValue.get() == "AAC"}
    private val fovValue = FloatValue("FOV", 180f, 0f, 180f).displayable{modeValue.get() == "AAC"}
    // 格挡
    private val AutoBlockValue = ListValue("AutoBlockMode", arrayOf("Off", "Normal", "Pos", "Click", "AfterTick","AAC"), "Normal")
    // NCP格挡设置
    private val interactAutoBlockValue = BoolValue("InteractAutoBlock", true).displayable{modeValue.get() == "NCP" && AutoBlockValue.get() != "Off"}
    // AAC格挡设置
    private val delayedBlockValue = BoolValue("DelayedBlock", true).displayable{modeValue.get() == "AAC" && AutoBlockValue.get() != "Off"}
    // 格挡速度
    private val blockRate = IntegerValue("BlockRate", 100, 1, 100).displayable{AutoBlockValue.get() != "Off"}
    // 其他设置
    private val swingValue = BoolValue("Swing", true)
    private val keepSprintValue = BoolValue("KeepSprint", true)
    // AAC预测设置
    private val predictValue = BoolValue("Predict", true).displayable{modeValue.get() == "AAC"}
    private val maxPredictSize: FloatValue = object : FloatValue("MaxPredictSize", 1f, 0f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minPredictSize.get()
            if (v > newValue) set(v)
        }

    }.displayable{modeValue.get() == "AAC" && predictValue.get()} as FloatValue
    private val minPredictSize: FloatValue = object : FloatValue("MinPredictSize", 1f, 0f, 5f) {

        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxPredictSize.get()
            if (v < newValue) set(v)
        }
    }.displayable{modeValue.get() == "AAC" && predictValue.get()} as FloatValue
    // AAC更多设置
    private val raycastValue = BoolValue("RayCast", true).displayable{modeValue.get() == "AAC"}
    private val raycastIgnoredValue = BoolValue("RayCastIgnored", false).displayable{modeValue.get() == "AAC" && raycastValue.get()}
    private val livingRaycastValue = BoolValue("LivingRayCast", true).displayable{modeValue.get() == "AAC" && raycastValue.get()}
    private val fakeSwingValue = BoolValue("FakeSwing", true).displayable{modeValue.get() == "AAC"}

    // 其他设置
    private val failRateValue = FloatValue("FailRate", 0f, 0f, 100f)
    private val witherValue = BoolValue("TargetWither", true)
    private val noInventoryAttackValue = BoolValue("NoInvAttack", false)
    private val autoDisableValue = BoolValue("AutoDisable", true)
    // 主绕过设置
    private val modeValue = ListValue("BypassMode", arrayOf("NCP", "AAC"), "NCP")

    // 目标
    var target: EntityLivingBase? = null
    private var currentTarget: EntityLivingBase? = null
    private var useItemEntity: EntityLivingBase? = null
    private var hitable = false
    private val prevTargetEntities = mutableListOf<Int>()
    private var flag = false

    // 攻击延迟
    private val attackTimer = MSTimer()
    private val switchDelay = MSTimer()
    private var attackDelay = 0L
    private var clicks = 0

    // Fake block status
    var blockingStatus = false

    // 凋零
    private var preBoss : EntityLivingBase? = null
    private var boss = false

    //懒狗
    val aacMode = modeValue.get().equals("AAC", ignoreCase = true)
    val turnSpeed = (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()


    /**
     * Enable killAura module
     */
    override fun onEnable() {
        mc.thePlayer ?: return
        mc.theWorld ?: return

        updateTarget()
    }

    /**
     * Disable killAura module
     */
    override fun onDisable() {
        target = null
        currentTarget = null
        hitable = false
        prevTargetEntities.clear()
        attackTimer.reset()
        clicks = 0

        stopBlocking()
    }

    /**
     * Motion event
     */
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.POST) {

            if (autoDisableValue.get() && (mc.thePlayer!!.isDead || mc.thePlayer!!.ticksExisted <= 1))
                state = false

            target ?: return
            currentTarget ?: return

            // Update hitable
            updateHitable()

            // AutoBlock
            if (useItemEntity != null) {
                // AutoBlock 防砍
                if (AutoBlockValue.get().equals("AfterTick", true) && canBlock)
                    startBlocking(currentTarget!!,hitable)

                // Start blocking after attack 攻击之后开始防砍
                if (AutoBlockValue.get().equals("Packet", true) && !blockingStatus && canBlock) {
                    startBlocking(useItemEntity!!,hitable)
                }
            }

            if(target != null && currentTarget != null && attackTimer.hasTimePassed(TimeUtils.randomClickDelay(maxCPS.get(), minCPS.get())) && currentTarget!!.hurtTime <= 10) {
                clicks++
                attackTimer.reset()
            }
        } else {
            if (!aacMode || rotationStrafeValue.get().equals("Off", true))
                update()

            useItemEntity = addtarget()
        }
    }

    /**
     * Strafe event
     */
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (!aacMode || rotationStrafeValue.get().equals("Off", true))
            return

        update()

        if (!silentRotationValue.get())
            return

        when (rotationStrafeValue.get().toLowerCase()) {
            "strict" -> {
                currentTarget ?: return

                val (yaw) = RotationUtils.targetRotation ?: return
                var strafe = event.strafe
                var forward = event.forward
                val friction = event.friction

                var f = strafe * strafe + forward * forward

                if (f >= 1.0E-4F) {
                    f = MathHelper.sqrt_float(f)

                    if (f < 1.0F)
                        f = 1.0F

                    f = friction / f
                    strafe *= f
                    forward *= f

                    val yawSin = MathHelper.sin((yaw * Math.PI / 180F).toFloat())
                    val yawCos = MathHelper.cos((yaw * Math.PI / 180F).toFloat())

                    mc.thePlayer.motionX += strafe * yawCos - forward * yawSin
                    mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin
                }
                event.cancelEvent()
            }
            "silent" -> {
                currentTarget ?: return

                update()

                RotationUtils.targetRotation.applyStrafeToPlayer(event)
                event.cancelEvent()
            }
        }
    }

    /**
     * Update event
     */
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(AutoBlockValue.get().equals("AAC", ignoreCase = true) && mc.thePlayer.heldItem.item is ItemSword) return
        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            return
        }

        if (noInventoryAttackValue.get() && mc.currentScreen is GuiContainer) {
            target = null
            currentTarget = null
            hitable = false
            return
        }

        if (target != null && currentTarget != null) {
            while (clicks > 0) {
                runAttack()
                clicks--
            }
        }
    }

    /**
     * Attack enemy 发起进攻~
     */
    private fun runAttack() {
        target ?: return
        currentTarget ?: return

        val thePlayer = mc.thePlayer ?: return
        val theWorld = mc.theWorld ?: return

        // Settings 设置
        val failRate = failRateValue.get()
        val swing = swingValue.get()
        val multi = targetModeValue.get().equals("Multi", ignoreCase = true)
        val switchmode = targetModeValue.get().equals("Switch", ignoreCase = true)
        val openInventory = aacMode && mc.currentScreen is GuiContainer
        val failHit = failRate > 0 && Random().nextInt(100) <= failRate

        // Close inventory when open 打开杀戮时关闭背包
        if (openInventory)
            mc.netHandler.addToSendQueue(C0EPacketClickWindow())

        // Check is not hitable or check failrate 检测是否可以攻击和空刀率
        if (!hitable || failHit) {
            if (swing && (fakeSwingValue.get() || failHit)) mc.thePlayer.swingItem()
        } else {
            // Attack 攻击
            if (!multi) {
                attackEntity(currentTarget!!)
            } else {
                var targets = 0

                for (entity in theWorld.loadedEntityList) {
                    val distance = PlayerExtensionUtils.getDistanceToEntityBox(mc.thePlayer, entity)

                    if (entity is EntityLivingBase && isEnemy(entity) && distance <= getRange(entity)) {
                        attackEntity(entity)

                        targets += 1

                        if (limitedMultiTargetsValue.get() != 0 && limitedMultiTargetsValue.get() <= targets)
                            break
                    }
                }
            }

            if (switchmode) {
                if (switchDelay.hasTimePassed(switchDelayValue.get().toLong())) {
                    if (switchDelayValue.get() != 0) {
                        prevTargetEntities.add(if (aacMode) target!!.entityId else currentTarget!!.entityId)
                        switchDelay.reset()
                    }
                }
                /*if (target == currentTarget)
                    target = null*/
            } else {
                prevTargetEntities.add(if (aacMode) target!!.entityId else currentTarget!!.entityId)
            }
        }

        // Open inventory
        if (openInventory)
            mc.netHandler.addToSendQueue(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
    }

    /**
     * Attack [entity] 攻击实体
     */
    private fun attackEntity(entity: EntityLivingBase) {
        // Stop blocking 停止防砍

        if (mc.thePlayer!!.isBlocking || blockingStatus)
            stopBlocking()

        val distance = PlayerExtensionUtils.getDistanceToEntityBox(mc.thePlayer, entity)

        if (hitable && distance < rangeValue.get()) {
            // Call attack event 调用攻击事件
            LiquidBounce.eventManager.callEvent(AttackEvent(entity))

            // Attack target
            if (swingValue.get())
                mc.thePlayer.swingItem()
            mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))

            if (keepSprintValue.get()) {
                // Critical Effect
                if (mc.thePlayer.fallDistance > 0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder &&
                        !mc.thePlayer.isInWater && !mc.thePlayer.isPotionActive(Potion.blindness) && !mc.thePlayer.isRiding)
                    mc.thePlayer.onCriticalHit(entity)

                // Enchant Effect
                if (EnchantmentHelper.getModifierForCreature(mc.thePlayer.heldItem, entity.creatureAttribute) > 0F)
                    mc.thePlayer.onEnchantmentCritical(entity)
            } else {
                if (mc.playerController.currentGameType != WorldSettings.GameType.SPECTATOR)
                    mc.thePlayer.attackTargetEntityWithCurrentItem(entity)
            }

            // Extra critical effects
            val criticals = LiquidBounce.moduleManager[Criticals::class.java] as Criticals

            for (i in 0..2) {
                // Critical Effect
                if (mc.thePlayer.fallDistance > 0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null || criticals.state && criticals.msTimer.hasTimePassed(criticals.delayValue.get().toLong()) && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava && !mc.thePlayer.isInWeb)
                    mc.thePlayer.onCriticalHit(target)

                // Extra critical effects 更多的暴击粒子
                if (EnchantmentHelper.getModifierForCreature(mc.thePlayer.heldItem, target!!.creatureAttribute) > 0.0f)
                    mc.thePlayer.onEnchantmentCritical(target)
            }
        }

        // Start blocking after attack
        if (mc.thePlayer.isBlocking || (!AutoBlockValue.get().equals("Off", ignoreCase = true) && canBlock)) {
            if (!(blockRate.get() > 0 && Random().nextInt(100) <= blockRate.get()))
                return

            if (aacMode && delayedBlockValue.get())
                return

            startBlocking(entity, !aacMode && interactAutoBlockValue.get())
        }
    }

    /**
     * Add target 添加目标
     */
    private fun addtarget(): EntityLivingBase? {
        // Find possible targets 寻找可能的目标
        val targets = mutableListOf<EntityLivingBase>()

        targets.clear()

        for (entity in mc.theWorld!!.loadedEntityList) {
            if (entity !is EntityLivingBase || !isEnemy(entity))
                continue

            if (mc.thePlayer!!.getDistanceToEntity(entity) <= blockRangeValue.get()) {
                targets.add(entity)
                useItemEntity = entity
            }
        }

        return if (targets.isEmpty()) {
            null
        } else targets[0]
    }

    fun update() {
        if (cancelRun || (noInventoryAttackValue.get() && mc.currentScreen is GuiContainer))
            return

        // Update target
        updateTarget()

        if (target == null) {
            stopBlocking()
            return
        }

        // Target
        currentTarget = target

        if (!targetModeValue.get().equals("Switch", ignoreCase = true) && isEnemy(currentTarget))
            target = currentTarget
    }

    /**
     * Check if enemy is hitable with current rotations 检测敌人是否是当前摇头可攻击的
     */
    private fun updateHitable() {
        if (maxTurnSpeed.get() <= 0F) {
            hitable = true
            return
        }

        val reach = if (aacMode) min(maxRange.toDouble(), PlayerExtensionUtils.getDistanceToEntityBox(mc.thePlayer, target!!)) + 1.4 else
            min(maxRange.toDouble(), PlayerExtensionUtils.getDistanceToEntityBox(mc.thePlayer, target!!) + hitBoxValue.get()) + 1

        if (aacMode && raycastValue.get()) {
            val raycastedEntity = RaycastUtils.raycastEntity(reach) {
                (!livingRaycastValue.get() || it is EntityLivingBase && it !is EntityArmorStand) &&
                        (isEnemy(it) || raycastIgnoredValue.get() || aacMode && mc.theWorld.getEntitiesWithinAABBExcludingEntity(it, it.entityBoundingBox).isNotEmpty())
            }

            if (raycastValue.get() && raycastedEntity is EntityLivingBase
                    && (LiquidBounce.moduleManager[NoFriends::class.java]!!.state || !EntityUtils.isFriend(raycastedEntity)))
                currentTarget = raycastedEntity

            hitable = if (maxTurnSpeed.get() > 0F) currentTarget == raycastedEntity else true
        } else {
            when (rotationMode.get()) {
                "Normal" -> {
                    hitable = if (!faceHitableValue.get()) true else RotationUtils.isFaced(currentTarget, reach)
                }

                "RayCast" -> {
                    val raycastedEntity = RaycastUtils.raycastEntity(reach) {
                        (it is EntityLivingBase && it !is EntityArmorStand) && (isEnemy(it) || aacMode && mc.theWorld.getEntitiesWithinAABBExcludingEntity(it, it.entityBoundingBox).isNotEmpty())
                    }

                    if (raycastedEntity is EntityLivingBase
                            && (LiquidBounce.moduleManager[NoFriends::class.java]!!.state || !EntityUtils.isFriend(raycastedEntity)))
                        currentTarget = raycastedEntity

                    hitable = if (!faceHitableValue.get()) true else RotationUtils.isFaced(currentTarget, reach)
                }



                else -> hitable = true
            }
        }
    }

    /**
     * Update current target 更新当前的目标
     */
    private fun updateTarget() {
        // Reset fixed target to null 将固定目标重置为空
        target = null

        // Settings 设置
        val hurtTime = hurtTimeValue.get()
        val fov = fovValue.get()

        // Find possible targets 寻找可能的目标
        val targets = mutableListOf<EntityLivingBase>()


        val theWorld = mc.theWorld!!
        val thePlayer = mc.thePlayer!!

        for (entity in theWorld.loadedEntityList) {
            if (entity is EntityWither)
                preBoss = entity

            if (entity !is EntityLivingBase || !isEnemy(entity) ||  if(boss) false else (targetModeValue.get().equals("Switch", ignoreCase = true) && prevTargetEntities.contains(entity.entityId)))
                continue

            val distance = PlayerExtensionUtils.getDistanceToEntityBox(mc.thePlayer, entity)
            val entityFov = RotationUtils.getRotationDifference(entity)

            if (distance <= maxRange && (fov == 180F || entityFov <= fov) && entity.hurtTime <= hurtTime)
                targets.add(entity)

            if(entity is EntityWither) {
                if (if (aacMode) distance <= maxRange - 1 && (fov == 180F || entityFov <= fov) && entity.hurtTime <= hurtTime
                        else distance <= maxRange - 1 && entity.hurtTime <= hurtTime) targets.add(entity)
            } else {
                if (if (aacMode) distance <= maxRange && (fov == 180F || entityFov <= fov) && entity.hurtTime <= hurtTime
                        else distance <= maxRange && entity.hurtTime <= hurtTime) {
                    targets.add(entity)
                } else {
                    preBoss = null
                }
            }

            boss = if (witherValue.get()) targets.contains(preBoss) else false
        }

        // Sort targets by priority 使用优先级整理目标
        when (priorityValue.get().toLowerCase()) {
            "distance" -> targets.sortBy { PlayerExtensionUtils.getDistanceToEntityBox(mc.thePlayer, it) }
            "health" -> targets.sortBy { it.health }
            "direction" -> targets.sortBy { RotationUtils.getRotationDifference(it) }
            "armor" -> targets.sortBy { it.totalArmorValue }
            "hurttime" -> targets.sortBy { it.hurtTime }
            "hurtresistanttime" -> targets.sortBy { it.hurtResistantTime }
            "livingtime" -> targets.sortBy { -it.ticksExisted }
        }

        // Find best target 寻找最好的目标
        for (entity in targets) {
            val distance = PlayerExtensionUtils.getDistanceToEntityBox(thePlayer, entity)
            // Set target to current entity
            // target = entity
            if (boss && witherValue.get()) {
                if(distance <= maxRange - 1 && preBoss !=null) {
                    updateRotations(preBoss as EntityWither)
                    target = preBoss
                    return
                }
            } else {
                updateRotations(entity)
                target = entity
                return
            }
        }

        // Cleanup last targets when no target found and try again 未找到目标时清除最后一个目标，然后重试
        if (prevTargetEntities.isNotEmpty()) {
            prevTargetEntities.clear()
            updateTarget()
        }
    }

    /**
     * Update killAura rotations to enemy 设置杀戮摇头到可攻击实体
     */
    private fun updateRotations(entity: Entity): Boolean {
        var boundingBox = entity.entityBoundingBox

        if (maxTurnSpeed.get() <= 0F)
            return true

        if (aacMode && predictValue.get())
            boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
            )

        val (_, rotation) = if (aacMode) RotationUtils.searchCenterAAC(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                PlayerExtensionUtils.getDistanceToEntityBox(mc.thePlayer, entity) < throughWallsRangeValue.get(),
                maxRange) ?: return false
        else RotationUtils.searchCenterNCP(boundingBox,
                PlayerExtensionUtils.getDistanceToEntityBox(mc.thePlayer, entity) < throughWallsRangeValue.get(), maxRange)
                ?: return false

        when (rotationMode.get()) {
            "Vanilla" -> {
                val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rotation, turnSpeed)

                if (silentRotationValue.get()) {
                    RotationUtils.setTargetRotation(limitedRotation,if(aacMode)15 else 0)
                } else limitedRotation.toPlayer(mc.thePlayer!!)
            }

            "RayCast" -> {
                val raycastedEntity = RotationUtils.isFaced(entity, maxRange.toDouble())
                val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rotation, if (raycastedEntity) 0f else turnSpeed)

                if (silentRotationValue.get()) {
                    RotationUtils.setTargetRotation(limitedRotation,if(aacMode)15 else 0)
                } else limitedRotation.toPlayer(mc.thePlayer!!)
            }

            "BackTrack" -> {
                val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation,
                        RotationUtils.OtherRotation(boundingBox, RotationUtils.getCenter(entity.entityBoundingBox),
                                PlayerExtensionUtils.getDistanceToEntityBox(mc.thePlayer, entity) < throughWallsRangeValue.get(), maxRange), turnSpeed)

                if (silentRotationValue.get()) {
                    RotationUtils.setTargetRotation(limitedRotation,if(aacMode)15 else 0)
                } else limitedRotation.toPlayer(mc.thePlayer!!)
            }


        }

        return true
    }

    /**
     * Check if [entity] is selected as enemy with current target options and other modules 检测实体是否应该攻击
     */
    private fun isEnemy(entity: Entity?): Boolean {
        if (entity is EntityLivingBase && (EntityUtils.targetDead || isAlive(entity)) && entity != mc.thePlayer) {
            if (!EntityUtils.targetInvisible && entity.isInvisible())
                return false

            if (EntityUtils.targetPlayer && entity is EntityPlayer) {
                if (entity.isSpectator || AntiBot.isBot(entity))
                    return false

                if (EntityUtils.isFriend(entity) && !LiquidBounce.moduleManager[NoFriends::class.java]!!.state)
                    return false

                val teams = LiquidBounce.moduleManager[Teams::class.java] as Teams

                return !teams.state || !Teams.isInYourTeam(entity)
            }

            if (entity is EntityWither) {
                return witherValue.get()
            }

            return EntityUtils.targetMobs && EntityUtils.isMob(entity) || EntityUtils.targetAnimals && EntityUtils.isAnimal(entity)
        }

        return false
    }

    /**
     * Check if run should be cancelled 检测是否应该取消攻击
     */
    private val cancelRun: Boolean
        inline get() = mc.thePlayer!!.isSpectator || !isAlive(mc.thePlayer!!)
                || LiquidBounce.moduleManager[Blink::class.java]!!.state ||
                LiquidBounce.moduleManager[FreeCam::class.java]!!.state

    /**
     * Start blocking 开始格挡
     */
    private fun startBlocking(interactEntity: Entity, interact: Boolean) {
        if (interact) {
            mc.netHandler.addToSendQueue(C02PacketUseEntity(interactEntity, interactEntity.positionVector))
            mc.netHandler.addToSendQueue(C02PacketUseEntity(interactEntity, C02PacketUseEntity.Action.INTERACT))
        }

        when (AutoBlockValue.get().toLowerCase()){
            "normal", "aftertick" -> mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))

            "pos" -> mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0f, 0.0f, 0.0f))

            "click" -> mc.gameSettings.keyBindUseItem.pressed = true

            "aac" -> {
                if (mc.thePlayer.heldItem.item is ItemSword)
                    mc.gameSettings.keyBindUseItem.pressed = true
            }
        }

        blockingStatus = true
    }

    @EventTarget
    private fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C07PacketPlayerDigging && AutoBlockValue.get().equals("AAC", ignoreCase = true) && mc.thePlayer.heldItem.item is ItemSword) {
            flag = !flag
            if (flag) {
                event.cancelEvent()
                mc.netHandler.addToSendQueue(packet)
                if (cancelRun) {
                    target = null
                    currentTarget = null
                    hitable = false
                    stopBlocking()
                    return
                }

                if (noInventoryAttackValue.get() && (mc.currentScreen is GuiContainer)) {
                    target = null
                    currentTarget = null
                    hitable = false
                    return
                }

                if (target != null && currentTarget != null) {
                    while (clicks > 0) {
                        runAttack()
                        clicks--
                    }
                }
            }

        }
    }

    /**
     * Stop blocking 停止格挡
     */
    private fun stopBlocking() {
        if (!blockingStatus) return

        when (AutoBlockValue.get().toLowerCase()){
            "normal", "aftertick", "pos" -> mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))

            "click", "aac" -> mc.gameSettings.keyBindUseItem.pressed = false
        }

        blockingStatus = false
    }

    /**
     * Check if [entity] is alive 检测实体是否是活着
     */
    private fun isAlive(entity: EntityLivingBase) = entity.isEntityAlive && entity.health > 0 || aacMode && entity.hurtTime > 5

    /**
     * Check if player is able to block 检测玩家是否可以防砍
     */
    private val canBlock: Boolean
        get() = mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword

    /**
     * Range 返回最大距离
     */
    private val maxRange: Float
        get() = max(rangeValue.get(), throughWallsRangeValue.get())
    private fun getRange(entity: Entity) =
            (if (PlayerExtensionUtils.getDistanceToEntityBox(mc.thePlayer, entity) >= throughWallsRangeValue.get()) rangeValue.get() else throughWallsRangeValue.get())
}