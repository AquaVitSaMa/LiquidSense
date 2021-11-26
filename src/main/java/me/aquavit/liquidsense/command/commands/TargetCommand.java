package me.aquavit.liquidsense.command.commands;

import me.aquavit.liquidsense.utils.entity.EntityUtils;
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
                EntityUtils.targetPlayer = !EntityUtils.targetPlayer;
                this.chat("§7Target player toggled " + (EntityUtils.targetPlayer ? "on" : "off") + '.');
                this.playEdit();
                return;
            }
            if (args[1].equalsIgnoreCase("mobs")) {
                EntityUtils.targetMobs = !EntityUtils.targetMobs;
                this.chat("§7Target mobs toggled " + (EntityUtils.targetMobs ? "on" : "off") + '.');
                this.playEdit();
                return;
            }
            if (args[1].equalsIgnoreCase("animals")) {
                EntityUtils.targetAnimals = !EntityUtils.targetAnimals;
                this.chat("§7Target animals toggled " + (EntityUtils.targetAnimals ? "on" : "off") + '.');
                this.playEdit();
                return;
            }
            if (args[1].equalsIgnoreCase("invisible")) {
                EntityUtils.targetInvisible = !EntityUtils.targetInvisible;
                this.chat("§7Target Invisible toggled " + (EntityUtils.targetInvisible ? "on" : "off") + '.');
                this.playEdit();
                return;
            }
            if (args[1].equalsIgnoreCase("dead")) {
                EntityUtils.targetDead = !EntityUtils.targetDead;
                this.chat("§7Target dead toggled " + (EntityUtils.targetDead ? "on" : "off") + '.');
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
                        .filter(it -> it.startsWith(args[0]))
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }
}
