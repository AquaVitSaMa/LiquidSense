package net.ccbluex.liquidbounce.features.module;

import me.aquavit.liquidsense.module.modules.blatant.Aura;
import me.aquavit.liquidsense.module.modules.exploit.*;
import me.aquavit.liquidsense.module.modules.misc.AntiBot;
import me.aquavit.liquidsense.module.modules.misc.MidClick;
import me.aquavit.liquidsense.module.modules.misc.NameProtect;
import me.aquavit.liquidsense.module.modules.misc.Spammer;
import me.aquavit.liquidsense.module.modules.movement.*;
import me.aquavit.liquidsense.module.modules.render.ClickGUI;
import me.aquavit.liquidsense.module.modules.client.Fullbright;
import me.aquavit.liquidsense.module.modules.client.HUD;
import me.aquavit.liquidsense.module.modules.ghost.AutoArmor;
import me.aquavit.liquidsense.module.modules.blatant.AutoPot;
import me.aquavit.liquidsense.module.modules.blatant.AutoSoup;
import me.aquavit.liquidsense.module.modules.client.Target;
import me.aquavit.liquidsense.module.modules.ghost.*;
import me.aquavit.liquidsense.module.modules.player.Blink;
import me.aquavit.liquidsense.module.modules.player.FastUse;
import me.aquavit.liquidsense.module.modules.player.InvClean;
import me.aquavit.liquidsense.module.modules.player.NoFall;
import me.aquavit.liquidsense.module.modules.render.*;
import me.aquavit.liquidsense.module.modules.world.ChestAura;
import me.aquavit.liquidsense.module.modules.world.ChestStealer;
import me.aquavit.liquidsense.module.modules.world.Scaffold;
import me.aquavit.liquidsense.module.modules.world.Tower;
import net.ccbluex.liquidbounce.LiquidBounce;
import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.KeyEvent;
import me.aquavit.liquidsense.event.Listenable;
import me.aquavit.liquidsense.utils.client.ClientUtils;
import me.aquavit.liquidsense.value.Value;
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
        this.moduleClassMap = new HashMap<>();
        LiquidBounce.eventManager.registerListener(this);
    }

    public void registerModules() {
        ClientUtils.getLogger().info("[ModuleManager] Loading modules...");
        registerALLModule(
                Target.class,
                Aimbot.class,
                AutoArmor.class,
                AutoPot.class,
                AutoSoup.class,
                Aura.class,
                Fly.class,
                ClickGUI.class,
                HighJump.class,
                InvMove.class,
                NoSlow.class,
                LiquidWalk.class,
                Sprint.class,
                AntiBot.class,
                ChestStealer.class,
                Scaffold.class,
                Tower.class,
                ESP.class,
                Speed.class,
                FastUse.class,
                Teleport.class,
                Phase.class,
                Fullbright.class,
                ItemESP.class,
                StorageESP.class,
                PingSpoof.class,
                FastClimb.class,
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
                AutoClicker.class,
                BlockESP.class,
                ServerCrasher.class,
                FastStairs.class,
                InvClean.class,
                BufferSpeed.class,
                ProphuntESP.class,
                KeepContainer.class,
                BugUp.class,
                HUD.class,
                ChestAura.class,
                AntiObsidian.class);

        ClientUtils.getLogger().info("[ModuleManager] Loaded " + modules.size() + " modules.");
    }

    public void registerModule(final Module module) {
        modules.add(module);
        (moduleClassMap).put(module.getClass(), module);

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
    @SafeVarargs
    public final void registerALLModule(Class<? extends Module>... modules) {
        List<Object> liquidSenseModules = LiquidBounce.liquidSense.getLiquidSenseModules();
        for (Class<? extends Module> lbModule : modules) {
            this.registerModule(lbModule);
        }
        for (Object lsModule : liquidSenseModules) {
            this.registerModule(lsModule);
        }
    }

    private void registerModule(Object lsModule) {
        try {
            this.registerModule((Module)((Class<?>)lsModule).newInstance());
        }
        catch (Throwable e) {
            ClientUtils.getLogger().error("Failed to load module: " + ((Class<?>)lsModule).getName() + " (" + e.getClass().getName() + ": " + e.getMessage() + ")");
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

