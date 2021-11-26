package me.aquavit.liquidsense.command.commands;

import net.ccbluex.liquidbounce.LiquidBounce;
import me.aquavit.liquidsense.command.Command;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetNameCommand extends Command {
    public SetNameCommand() {
        super("name");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("load")) {
                LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.setnameConfig);
                return;
            }
            if (args[1].equalsIgnoreCase("list")) {
                for (Module module : LiquidBounce.moduleManager.getModules()) {
                    if (module.getArrayListName().equals(module.getClass().getAnnotation(ModuleInfo.class).name())) continue;
                    this.chat("Module <" + module.getArrayListName() + "> is §7" + module.getClass().getAnnotation(ModuleInfo.class).name());
                }
                return;
            }
            if (args[1].equalsIgnoreCase("cleaner")) {
                for (Module module : LiquidBounce.moduleManager.getModules()) {
                    if (args.length > 2) {
                        String newValue = this.getname(args, 2);

                        Module oldmodule = LiquidBounce.moduleManager.getModule(newValue);
                        if (oldmodule == null)return;

                        if (module.getName().equalsIgnoreCase(oldmodule.getName())) {
                            module.setArrayListName(module.getClass().getAnnotation(ModuleInfo.class).name());

                            LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.setnameConfig);
                            this.chat("reset Module <" + newValue + '>');
                            return;
                        }
                    }
                    module.setArrayListName(module.getClass().getAnnotation(ModuleInfo.class).name());
                    LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.setnameConfig);
                }
                this.chat("reset Module name ");
                return;
            }
            if (args.length > 2) {
                String newValue = this.getname(args, 2);
                String oldVaule = this.getname(args, 1);
                Module module = LiquidBounce.moduleManager.getModule(oldVaule);
                if (module == null) {
                    this.chat("Module §a§l" + args[1] + "§3 not found.");
                    return;
                }
                module.setNameCommad(newValue);
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.setnameConfig);
                this.chat("Module §a§l" + args[1] + "§l§a to " + newValue);
                return;
            }
        }
        this.chatSyntax(new String[]{"<module> <name>", "<cleaner/load/list> "});
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

    public String getname(final String[] args,int size) {
        String string;
        if (args[size].contains("#")){
            string = args[size].replace("#","");
        } else if (args[size].contains("#l")){
            string = args[size].replace("#l", EnumChatFormatting.BOLD.toString());
        } else if (args[size].contains("#o")){
            string = args[size].replace("#o", EnumChatFormatting.ITALIC.toString());
        } else if (args[size].contains("#m")){
            string = args[size].replace("#m", EnumChatFormatting.STRIKETHROUGH.toString());
        } else if (args[size].contains("#n")){
            string = args[size].replace("#n", EnumChatFormatting.UNDERLINE.toString());
        } else {
            string = args[size];
        }
        return string;
    }
}
