package me.aquavit.liquidsense.command.commands;

import me.aquavit.liquidsense.LiquidSense;
import me.aquavit.liquidsense.command.Command;
import me.aquavit.liquidsense.ui.client.clickgui.neverlose.Main;
import me.aquavit.liquidsense.ui.font.Fonts;

public class ReloadCommand extends Command
{
    public ReloadCommand() {
        super("reload", "configreload");
    }

    @Override
    public void execute(final String[] args) {
        this.chat("Reloading...");
        this.chat("§c§lReloading scripts...");
        LiquidSense.scriptManager.reloadScripts();
        this.chat("§c§lReloading fonts...");
        Fonts.loadFonts();
        this.chat("§c§lReloading modules...");
        LiquidSense.fileManager.loadConfig(LiquidSense.fileManager.modulesConfig);
        this.chat("§c§lReloading values...");
        LiquidSense.fileManager.loadConfig(LiquidSense.fileManager.valuesConfig);
        this.chat("§c§lReloading accounts...");
        LiquidSense.fileManager.loadConfig(LiquidSense.fileManager.accountsConfig);
        this.chat("§c§lReloading friends...");
        LiquidSense.fileManager.loadConfig(LiquidSense.fileManager.friendsConfig);
        this.chat("§c§lReloading xray...");
        LiquidSense.fileManager.loadConfig(LiquidSense.fileManager.xrayConfig);
        this.chat("§c§lReloading hud...");
        LiquidSense.fileManager.loadConfig(LiquidSense.fileManager.hudConfig);
        this.chat("§c§lReloading ClickGUI...");
        LiquidSense.neverlose= new Main();
        this.chat("Reloaded.");
    }
}
