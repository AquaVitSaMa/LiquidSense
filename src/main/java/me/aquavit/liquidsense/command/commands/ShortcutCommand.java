package me.aquavit.liquidsense.command.commands;

import net.ccbluex.liquidbounce.LiquidBounce;
import me.aquavit.liquidsense.command.Command;
import me.aquavit.liquidsense.utils.misc.StringUtils;

public class ShortcutCommand extends Command {
    public ShortcutCommand() { super("shortcut");}

    @Override
    public void execute(final String[] args) {
        if (args.length > 3 && args[1].equalsIgnoreCase("add")) {
            try {
                LiquidBounce.commandManager.registerShortcut(args[2], StringUtils.toCompleteString(args, 3));

                this.chat("Successfully added shortcut.");
            } catch (IllegalArgumentException e) {
                this.chat(e.getMessage());
            }
        } else if (args.length >= 3 && args[1].equalsIgnoreCase("remove")) {
            if (LiquidBounce.commandManager.unregisterShortcut(args[2])) {
                this.chat("Successfully removed shortcut.");
            } else {
                this.chat("Shortcut does not exist.");
            }
        } else {
            this.chat("shortcut <add <shortcut_name> <script>/remove <shortcut_name>>");
        }
    }
}