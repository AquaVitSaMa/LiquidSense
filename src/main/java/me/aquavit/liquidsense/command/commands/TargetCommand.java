package me.aquavit.liquidsense.command.commands;

import me.aquavit.liquidsense.module.modules.client.Target;
import me.aquavit.liquidsense.command.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TargetCommand extends Command {
    public TargetCommand() {
        super("target");
    }

    @Override
    public void execute(final String[] args) {

        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("players")) {
                Target.player.set(!Target.player.get());
                this.chat("§7Target player toggled " + (Target.player.get() ? "on" : "off") + '.');
                this.playEdit();
                return;
            }
            if (args[1].equalsIgnoreCase("mobs")) {
                Target.mob.set(!Target.mob.get());
                this.chat("§7Target mobs toggled " + (Target.mob.get() ? "on" : "off") + '.');
                this.playEdit();
                return;
            }
            if (args[1].equalsIgnoreCase("animals")) {
                Target.animal.set(!Target.animal.get());
                this.chat("§7Target animals toggled " + (Target.animal.get() ? "on" : "off") + '.');
                this.playEdit();
                return;
            }
            if (args[1].equalsIgnoreCase("invisible")) {
                Target.invisible.set(!Target.invisible.get());
                this.chat("§7Target Invisible toggled " + (Target.invisible.get() ? "on" : "off") + '.');
                this.playEdit();
                return;
            }
            if (args[1].equalsIgnoreCase("dead")) {
                Target.dead.set(!Target.dead.get());
                this.chat("§7Target dead toggled " + (Target.dead.get() ? "on" : "off") + '.');
                this.playEdit();
                return;
            }
        }
        this.chatSyntax("target <players/mobs/animals/invisible/dead>");

    }

    @Override
    public List<String> tabComplete(String[] args) {
        if (args.length == 0) return new ArrayList<>();

        switch (args.length) {
            case 1:
                return Arrays.stream(new String[]{"players", "mobs", "animals", "invisible","dead"})
                        .filter(it -> it.toLowerCase().startsWith(args[0]))
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }
}
