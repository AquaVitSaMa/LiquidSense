package net.ccbluex.liquidbounce.features.command.commands;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.command.Command;
import net.ccbluex.liquidbounce.features.module.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ToggleCommand extends Command {
    public ToggleCommand() {
        super("toggle", "t");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length > 1) {
            Module module = LiquidBounce.moduleManager.getModule(args[1]);

            if (module == null) {
                this.chat("Module '" + args[1] + "' not found.");
                return;
            }

            if (args.length > 2) {
                String newState = args[2].toLowerCase();

                if (newState == "on" || newState == "off") {
                    module.setState(true);
                } else {
                    this.chatSyntax("toggle <module> [on/off]");
                    return;
                }
            } else {
                module.toggle();
            }

            this.chat((module.getState() ? "Enabled" : "Disabled") + " module §8" + module.getName() + "§3.");
            return;
        }

        this.chatSyntax("toggle <module> [on/off]");
    }

    @Override
    public List<String> tabComplete(String[] args) {
        if (args.length == 0) return new ArrayList<>();

        String moduleName = args[0];

        switch (args.length) {
            case 1:
                return LiquidBounce.moduleManager.getModules().stream()
                        .map(Module::getName)
                        .filter(module -> module.startsWith(moduleName))
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }
}
