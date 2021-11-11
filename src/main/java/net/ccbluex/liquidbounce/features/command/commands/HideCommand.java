package net.ccbluex.liquidbounce.features.command.commands;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.command.Command;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.utils.client.ClientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HideCommand extends Command {

    public HideCommand(){
        super("hide");
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("list")){
                this.chat("§c§lHidden");
                LiquidBounce.moduleManager.getModules().stream().filter(Module::getArray)
                        .forEach(module ->
                                ClientUtils.displayChatMessage("§6> §c" + module.getName()));
                return;
            } else if (args[1].equalsIgnoreCase("clear")){

                for (Module module : LiquidBounce.moduleManager.getModules())
                    module.setArray(true);

                this.chat("Cleared hidden modules.");
                return;
            } else if (args[1].equalsIgnoreCase("reset")) {

                for (Module module : LiquidBounce.moduleManager.getModules())
                    module.setArray(module.getClass().getAnnotation(ModuleInfo.class).array());

                this.chat("Reset hidden modules.");
                return;
            } else {
                // Get module by name
                Module module = LiquidBounce.moduleManager.getModule(args[1]);

                if (module == null) {
                    this.chat("Module §a§l" + args[1] + "§3 not found.");
                    return;
                }

                // Find key by name and change
                module.setArray(!module.getArray());

                // Response to user
                this.chat("Module §a§l" + module.getName() + "§3 is now §a§l" + (module.getArray() ? "visible" : "invisible") + "§3 on the array list.");
                playEdit();
                return;
            }
        }

        this.chatSyntax("hide <module/list/clear/reset>");
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
