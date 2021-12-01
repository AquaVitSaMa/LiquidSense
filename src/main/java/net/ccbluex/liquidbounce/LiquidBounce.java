package net.ccbluex.liquidbounce;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kotlin.jvm.internal.Intrinsics;
import me.aquavit.liquidsense.LiquidSense;
import me.aquavit.liquidsense.utils.client.ClientUtils;
import me.aquavit.liquidsense.utils.forge.BlocksTab;
import me.aquavit.liquidsense.utils.forge.ExploitsTab;
import me.aquavit.liquidsense.utils.forge.HeadsTab;
import me.aquavit.liquidsense.utils.login.UserUtils;
import me.aquavit.liquidsense.utils.mc.ClassUtils;
import me.aquavit.liquidsense.event.events.ClientShutdownEvent;
import me.aquavit.liquidsense.event.EventManager;
import me.aquavit.liquidsense.command.CommandManager;
import net.ccbluex.liquidbounce.features.module.ModuleManager;
import net.ccbluex.liquidbounce.features.special.AntiForge;
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof;
import net.ccbluex.liquidbounce.file.FileManager;
import net.ccbluex.liquidbounce.script.ScriptManager;
import net.ccbluex.liquidbounce.script.remapper.Remapper;
import net.ccbluex.liquidbounce.ui.client.gui.GuiAltManager;
import net.ccbluex.liquidbounce.ui.client.hud.HUD;
import net.ccbluex.liquidbounce.ui.client.miscible.Miscible;
import net.ccbluex.liquidbounce.ui.client.neverlose.Main;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.InventoryUtils;
import me.aquavit.liquidsense.utils.client.RotationUtils;
import me.aquavit.liquidsense.utils.misc.HttpUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LiquidBounce {

    public static LiquidSense liquidSense;

    public static final LiquidBounce INSTANCE = new LiquidBounce();

    public static final String CLIENT_NAME = "LiquidSense";
    public static final String CLIENT_VERSION = "Dev";
    public static final String CLIENT_CREATOR = "CCBlueX";
    public static final String MINECRAFT_VERSION = "1.8.9";
    public static final String CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce";
    public static final String CLIENT_RESOURCE = "https://cdn.jsdelivr.net/gh/BlogResourceRepositories/ClientResource@main/";

    public boolean isStarting;

    public static ModuleManager moduleManager;
    public static CommandManager commandManager;
    public static EventManager eventManager;
    public static FileManager fileManager;
    public static ScriptManager scriptManager;

    public static HUD hud;
    public static Miscible miscible;
    public static Main neverlose;

    public int latestVersion;

    public ResourceLocation background;

    public void startClient() {
        this.isStarting = true;

        ClientUtils.getLogger().info("Starting "+CLIENT_NAME+" "+CLIENT_VERSION+", by "+CLIENT_CREATOR);

        LiquidSense liquidSense = LiquidBounce.liquidSense = new LiquidSense(this);
        liquidSense.onStarting();

        fileManager = new FileManager();

        EventManager eventManager = LiquidBounce.eventManager = new EventManager();

        eventManager.registerListener(new RotationUtils());
        eventManager.registerListener(new AntiForge());
        eventManager.registerListener(new BungeeCordSpoof());
        eventManager.registerListener(new InventoryUtils());

        commandManager = new CommandManager();

        Fonts.loadFonts();

        moduleManager = new ModuleManager();
        moduleManager.registerModules();

        try {
            Remapper.INSTANCE.loadSrg();
            scriptManager = new ScriptManager();
            scriptManager.loadScripts();
            scriptManager.enableScripts();
        } catch (Throwable throwable) {
            ClientUtils.getLogger().error("Failed to load scripts.", throwable);
        }

        commandManager.registerCommands();

        fileManager.loadConfigs(fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
                fileManager.friendsConfig, fileManager.xrayConfig, fileManager.shortcutsConfig);

        miscible = new Miscible();
        neverlose = new Main();

        if (ClassUtils.hasForge()) {
            new BlocksTab();
            new ExploitsTab();
            new HeadsTab();
        }

        hud = HUD.Companion.createDefault();
        fileManager.loadConfig(fileManager.hudConfig);

        ClientUtils.disableFastRender();

        try {
            final JsonElement jsonObj = new JsonParser().parse(HttpUtils.get(CLIENT_CLOUD+"/versions.json"));
            if (jsonObj instanceof JsonObject && ((JsonObject)jsonObj).has(MINECRAFT_VERSION)) {
                JsonElement jsonElement = ((JsonObject)jsonObj).get(MINECRAFT_VERSION);
                Intrinsics.checkExpressionValueIsNotNull((Object)jsonElement, (String)"jsonObj[MINECRAFT_VERSION]");
                latestVersion = jsonElement.getAsInt();
            }
        }
        catch (Throwable exception) {
            ClientUtils.getLogger().error("Failed to check for updates.", exception);
        }

        LoadAltManagerSkin();

        GuiAltManager.loadGenerators();

        fileManager.loadConfigs(fileManager.setnameConfig);

        liquidSense.onStarted();

        isStarting = false;
    }

    public final void stopClient() {
        LiquidBounce.eventManager.callEvent(new ClientShutdownEvent());
        LiquidBounce.fileManager.saveAllConfigs();
    }

    public final void LoadAltManagerSkin(){

        ClientUtils.getLogger().info("Loading AltManagerSkin.");

        for (int id = 0; id < LiquidBounce.fileManager.accountsConfig.altManagerMinecraftAccounts.size(); ++id) {
            if (!LiquidBounce.fileManager.accountsConfig.altManagerMinecraftAccounts.get(id).isCracked() && !GuiAltManager.skin.containsKey(id))
                GuiAltManager.skin.put(id, UserUtils.getPlayerSkin(UserUtils.getUUID(LiquidBounce.fileManager.accountsConfig.altManagerMinecraftAccounts.get(id).getAccountName())));
        }
    }

    public final LiquidSense getLiquidSense() {
        return LiquidBounce.liquidSense;
    }

    public final void setLiquidSense(LiquidSense liquidSense) {
        LiquidBounce.liquidSense = liquidSense;
    }

    public final boolean isStarting() {
        return isStarting;
    }

    public final void setStarting(boolean state) {
        isStarting = state;
    }

    public final ModuleManager getModuleManager() {
        return LiquidBounce.moduleManager;
    }

    public final void setModuleManager(ModuleManager moduleManager) {
        LiquidBounce.moduleManager = moduleManager;
    }

    public final CommandManager getCommandManager() {
        return LiquidBounce.commandManager;
    }

    public final void setCommandManager(CommandManager commandManager) {
        LiquidBounce.commandManager = commandManager;
    }

    public final EventManager getEventManager() {
        return LiquidBounce.eventManager;
    }

    public final void setEventManager(EventManager eventManager) {
        LiquidBounce.eventManager = eventManager;
    }

    public final FileManager getFileManager() {
        return LiquidBounce.fileManager;
    }

    public final void setFileManager(FileManager fileManager) {
        LiquidBounce.fileManager = fileManager;
    }

    public final ScriptManager getScriptManager() {
        return LiquidBounce.scriptManager;
    }

    public final void setScriptManager(ScriptManager scriptManager) {
        LiquidBounce.scriptManager = scriptManager;
    }

    public final HUD getHud() {
        return hud;
    }

    public final void setHud(HUD hUD) {
        hud = hUD;
    }

    public final Miscible getMiscible() {
        return LiquidBounce.miscible;
    }

    public final void setMiscible(Miscible miscible) {
        LiquidBounce.miscible = miscible;
    }

    public final Main getNeverlose() {
        return neverlose;
    }

    public final void setNeverlose(Main main) {
        neverlose = main;
    }

    public final int getLatestVersion() {
        return latestVersion;
    }

    public final void setLatestVersion(int version) {
        latestVersion = version;
    }

    public final ResourceLocation getBackground() {
        return background;
    }

    public final void setBackground(ResourceLocation resourceLocation) {
        background = resourceLocation;
    }
}
