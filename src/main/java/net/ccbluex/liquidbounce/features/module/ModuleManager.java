package net.ccbluex.liquidbounce.features.module;

import me.aquavit.liquidsense.modules.combat.Aura;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.KeyEvent;
import net.ccbluex.liquidbounce.event.Listenable;
import net.ccbluex.liquidbounce.features.module.modules.combat.*;
import net.ccbluex.liquidbounce.features.module.modules.exploit.*;
import net.ccbluex.liquidbounce.features.module.modules.misc.*;
import net.ccbluex.liquidbounce.features.module.modules.movement.*;
import net.ccbluex.liquidbounce.features.module.modules.render.*;
import net.ccbluex.liquidbounce.features.module.modules.world.*;
import net.ccbluex.liquidbounce.features.module.modules.player.*;
import me.aquavit.liquidsense.utils.client.ClientUtils;
import net.ccbluex.liquidbounce.value.Value;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

@SideOnly(Side.CLIENT)
public final class ModuleManager implements Listenable {

    //private static final List<Module> modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) };

    private static final TreeSet<Module> modules = new TreeSet(new Comparator<Module>() {
        @Override
        public int compare(Module module1, Module module2) {
            return module1.getName().compareTo(module2.getName());
        }
    });

    public TreeSet<Module> getModules() {
        return modules;
    }

    private final HashMap<Class<?>, Module> moduleClassMap;

    public ModuleManager() {
        this.moduleClassMap = new HashMap<Class<?>, Module>();
        LiquidBounce.eventManager.registerListener(this);
    }

    public void registerModules() {
        ClientUtils.getLogger().info("[ModuleManager] Loading modules...");
        registerALLModule(
                Aimbot.class,
                AutoArmor.class,
                AutoBow.class,
                AutoPot.class,
                AutoSoup.class,
                BowAimbot.class,
                Criticals.class,
                Aura.class,
                Velocity.class,
                Fly.class,
                ClickGUI.class,
                HighJump.class,
                InventoryMove.class,
                NoSlow.class,
                LiquidWalk.class,
                Sprint.class,
                AntiBot.class,
                ChestStealer.class,
                Scaffold.class,
                Tower.class,
                ESP.class,
                Speed.class,
                NameTags.class,
                FastUse.class,
                Teleport.class,
                Fullbright.class,
                ItemESP.class,
                StorageESP.class,
                PingSpoof.class,
                FastClimb.class,
                Step.class,
                Spammer.class,
                IceSpeed.class,
                NoFall.class,
                Blink.class,
                NameProtect.class,
                MidClick.class,
                XRay.class,
                FreeCam.class,
                Plugins.class,
                LongJump.class,
                FastBow.class,
                AutoClicker.class,
                BlockESP.class,
                ServerCrasher.class,
                FastStairs.class,
                InventoryCleaner.class,
                BufferSpeed.class,
                ProphuntESP.class,
                KeepContainer.class,
                HUD.class,
                Rotations.class,
                BugUp.class,
                AntiObsidian.class);

        this.registerModule(Fucker.INSTANCE);
        this.registerModule(ChestAura.INSTANCE);

        ClientUtils.getLogger().info("[ModuleManager] Loaded " + modules.size() + " modules.");
    }

    public void registerModule(final Module module) {
        modules.add(module);
        ((Map)moduleClassMap).put(module.getClass(), module);

        this.generateCommand(module);
        LiquidBounce.eventManager.registerListener(module);
    }

    public void registerModule(Class<? extends Module> moduleClass) {
        try {
            this.registerModule(moduleClass.newInstance());
        }
        catch (Throwable e) {
            ClientUtils.getLogger().error("Failed to load module: " + moduleClass.getName() + " (" + e.getClass().getName() + ": " + e.getMessage() + ")");
        }
    }

    /**
     * Register a list of modules
     */
    public void registerALLModule(Class<? extends Module>... modules) {
        List<Object> liquidSenseModules = LiquidBounce.liquidSense.getLiquidSenseModules();
        for (Class<? extends Module> lbModule : modules) {
            this.registerModule(lbModule);
        }
        for (Object cbModule : liquidSenseModules) {
            this.registerModule(cbModule);
        }
    }

    private void registerModule(Object cbModule) {
        try {
            this.registerModule((Module)((Class)cbModule).newInstance());
        }
        catch (Throwable e) {
            ClientUtils.getLogger().error("Failed to load module: " + ((Class)cbModule).getName() + " (" + e.getClass().getName() + ": " + e.getMessage() + ")");
        }
    }

    public void unregisterModule(final Module module) {
        modules.remove(module);
        this.moduleClassMap.remove(module.getClass());
        LiquidBounce.eventManager.unregisterListener(module);
    }

    private void generateCommand(Module module) {
        List<Value<?>> values = module.getValues();
        if (values.isEmpty()) {
            return;
        }
        LiquidBounce.commandManager.registerCommand(new ModuleCommand(module, values));
    }

    public final Module getModule(Class<?> moduleClass) {
        return this.moduleClassMap.get(moduleClass);
    }

    public final Module get(Class<?> Clazz) {
        return getModule(Clazz);
    }

    public static Module getModule(final String moduleName) {
        for (final Module module : modules) {
            if (module.getName().equalsIgnoreCase(moduleName)) {
                return module;
            }
        }
        return null;
    }

    @EventTarget
    private void onKey(final KeyEvent event) {
        modules.stream().filter(module -> module.getKeyBind() == event.getKey()).forEach(Module::toggle);
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}

