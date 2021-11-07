package net.ccbluex.liquidbounce.features.command.commands;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.command.Command;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PanicCommand extends Command {
    public PanicCommand(){
        super("panic");
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1 && !args[1].isEmpty()) {
            if (args[1].equalsIgnoreCase("all")){
                LiquidBounce.moduleManager.getModules().stream().filter(Module::getState).forEach(module -> module.setState(false));
                this.chat("Disabled all modules.");
            }else if (args[1].equalsIgnoreCase("nonrender")){
                LiquidBounce.moduleManager.getModules().stream().filter(module -> module.getState() && module.getCategory() != ModuleCategory.RENDER).forEach(module -> module.setState(false));
                this.chat("Disabled all non-render modules.");
            } else {
                for (ModuleCategory categories : ModuleCategory.values()){
                    if (args[1].equalsIgnoreCase(categories.displayName.toLowerCase())){
                        LiquidBounce.moduleManager.getModules().stream().filter
                                (module -> module.getState() && module.getCategory() == categories).forEach(module -> module.setState(false));
                        this.chat("Disabled "+args[1].toLowerCase()+ " modules.");
                        return;
                    }
                }
                this.chat("Category "+ args[1] + " not found");
            }
        } else {
            this.chatSyntax("panic <all/nonrender/combat/player/movement/render/world/misc/exploit/fun>");
        }
    }

    @Override
    public List<String> tabComplete(String[] args) {
        if (args.length == 0) return new ArrayList<>();

        switch (args.length) {
            case 1:
                return Arrays.stream(new String[]{"all", "nonrender", "combat", "player", "movement", "render", "world", "misc", "exploit", "fun"})
                        .filter(it -> it.startsWith(args[0]))
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

}
