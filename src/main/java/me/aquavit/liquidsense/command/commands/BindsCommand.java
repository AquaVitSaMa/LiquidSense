package me.aquavit.liquidsense.command.commands;

import me.aquavit.liquidsense.LiquidBounce;
import me.aquavit.liquidsense.command.Command;
import me.aquavit.liquidsense.utils.client.ClientUtils;
import org.lwjgl.input.Keyboard;

public class BindsCommand extends Command {
    public BindsCommand() {
        super("binds");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length > 1) {
            final String lowerCase = args[1].toLowerCase();
            switch (lowerCase) {
                case "clear": {
                    LiquidBounce.moduleManager.getModules().forEach(module -> module.setKeyBind(0));
                    this.chat("Removed all binds.");
                    return;
                }
            }
        }
        this.chat("§c§lBinds");
        LiquidBounce.moduleManager.getModules().stream().filter(module -> module.getKeyBind() != 0).forEach(
                module -> ClientUtils.displayChatMessage("§6> §c" + module.getName() + ": §a§l" + Keyboard.getKeyName(module.getKeyBind())));
        this.chatSyntax(".binds clear");
    }
}