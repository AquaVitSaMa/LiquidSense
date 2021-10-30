/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.KeyEvent
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.features.module.modules.combat.*
import net.ccbluex.liquidbounce.features.module.modules.exploit.*
import net.ccbluex.liquidbounce.features.module.modules.misc.*
import net.ccbluex.liquidbounce.features.module.modules.movement.*
import net.ccbluex.liquidbounce.features.module.modules.player.*
import net.ccbluex.liquidbounce.features.module.modules.render.*
import net.ccbluex.liquidbounce.features.module.modules.world.*
import net.ccbluex.liquidbounce.utils.ClientUtils
import java.util.*


class ModuleManager : Listenable {

    val modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) }
    private val moduleClassMap = hashMapOf<Class<*>, Module>()

    init {
        LiquidBounce.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        ClientUtils.getLogger().info("[ModuleManager] Loading modules...")

        registerModules(
                Aimbot::class.java,
                AutoArmor::class.java,
                AutoBow::class.java,
                AutoPot::class.java,
                AutoSoup::class.java,
                BowAimbot::class.java,
                Criticals::class.java,
                Aura::class.java, //
                Velocity::class.java,
                Fly::class.java,
                ClickGUI::class.java,
                HighJump::class.java,
                InventoryMove::class.java,
                NoSlow::class.java,
                LiquidWalk::class.java,
                Sprint::class.java,
                Teams::class.java,
                AntiBot::class.java,
                ChestStealer::class.java,
                Scaffold::class.java,
                Tower::class.java,
                ESP::class.java,
                Speed::class.java,
                Tracers::class.java,
                NameTags::class.java,
                FastUse::class.java,
                Teleport::class.java,
                Fullbright::class.java,
                ItemESP::class.java,
                StorageESP::class.java,
                Projectiles::class.java,
                PingSpoof::class.java,
                FastClimb::class.java,
                Step::class.java,
                Spammer::class.java,
                IceSpeed::class.java,
                NoFall::class.java,
                Blink::class.java,
                NameProtect::class.java,
                MidClick::class.java,
                XRay::class.java,
                FreeCam::class.java,
                Plugins::class.java,
                LongJump::class.java,
                FastBow::class.java,
                AutoClicker::class.java,
                BlockESP::class.java,
                Chams::class.java,
                ServerCrasher::class.java,
                FastStairs::class.java,
                InventoryCleaner::class.java,
                BufferSpeed::class.java,
                SuperKnockback::class.java,
                ProphuntESP::class.java,
                KeepContainer::class.java,
                HUD::class.java,
                TNTESP::class.java,
                Rotations::class.java,
                BugUp::class.java,
                AntiObsidian::class.java
        )

        registerModule(Fucker)
        registerModule(ChestAura)

        ClientUtils.getLogger().info("[ModuleManager] Loaded ${modules.size} modules.")
    }

    /**
     * Register [module]
     */
    fun registerModule(module: Module) {
        modules += module
        moduleClassMap[module.javaClass] = module

        generateCommand(module)
        LiquidBounce.eventManager.registerListener(module)
    }

    /**
     * Register [moduleClass]
     */
    private fun registerModule(moduleClass: Class<out Module>) {
        try {
            registerModule(moduleClass.newInstance())
        } catch (e: Throwable) {
            ClientUtils.getLogger().error("Failed to load module: ${moduleClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }

    /**
     * Register a list of modules
     */
    @SafeVarargs
    fun registerModules(vararg modules: Class<out Module>) {
        var liquidSenseModules = LiquidBounce.liquidSense.liquidSenseModules
        for (lbModule in modules){
            registerModule(lbModule)
        }
        for (cbModule in liquidSenseModules){
            registerModule(cbModule)
        }
    }

    private fun registerModule(cbModule: Any?) {
        registerModule((cbModule as Class<out Module>).newInstance())
    }


    /**
     * Unregister module
     */
    fun unregisterModule(module: Module) {
        modules.remove(module)
        moduleClassMap.remove(module::class.java)
        LiquidBounce.eventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    private fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        LiquidBounce.commandManager.registerCommand(ModuleCommand(module, values))
    }

    /**
     * Legacy stuff
     *
     * TODO: Remove later when everything is translated to Kotlin
     */

    /**
     * Get module by [moduleClass]
     */
    fun getModule(moduleClass: Class<*>) = moduleClassMap[moduleClass]

    operator fun get(clazz: Class<*>) = getModule(clazz)

    /**
     * Get module by [moduleName]
     */
    fun getModule(moduleName: String?) = modules.find { it.name.equals(moduleName, ignoreCase = true) }

    /**
     * Module related events
     */

    /**
     * Handle incoming key presses
     */
    @EventTarget
    private fun onKey(event: KeyEvent) = modules.filter { it.keyBind == event.key }.forEach { it.toggle() }

    override fun handleEvents() = true
}
