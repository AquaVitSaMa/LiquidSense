package net.ccbluex.liquidbounce.features.command.commands;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.command.Command;

public class PrefixCommand extends Command {
    public PrefixCommand() {
        super("prefix");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length <= 1) {
            this.chatSyntax("prefix <character>");
            return;
        }

        String prefix = args[1];

        if (prefix.length() > 1) {
            this.chat("§cPrefix can only be one character long!");
            return;
        }

        LiquidBounce.commandManager.setPrefix(prefix.charAt(0));
        LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.valuesConfig);

        this.chat("Successfully changed command prefix to '§8" +prefix+"§3'");
    }
}
