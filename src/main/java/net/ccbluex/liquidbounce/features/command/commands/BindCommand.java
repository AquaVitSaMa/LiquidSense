package net.ccbluex.liquidbounce.features.command.commands;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.command.Command;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BindCommand extends Command {
    public BindCommand() {
        super("bind");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length <= 2) {
            this.chatSyntax(new String[] { "<module> <key>", "<module> none" });
            return;
        }
        final Module module = LiquidBounce.moduleManager.getModule(args[1]);
        if (module == null) {
            this.chat("Module §a§l" + args[1] + "§3 not found.");
            return;
        }
        final int key = Keyboard.getKeyIndex(args[2].toUpperCase());
        module.setKeyBind(key);
        this.chat("Bound module §a§l" + module.getName() + "§3 to key §a§l" + Keyboard.getKeyName(key) + "§3.");
        LiquidBounce.hud.addNotification(new Notification("Bound " + module.getName() + " to " + Keyboard.getKeyName(key),"",NotifyType.SUCCESS,1500,500));
        playEdit();
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
