package me.aquavit.liquidsense.command.shortcuts;

import me.aquavit.liquidsense.utils.data.Pair;
import me.aquavit.liquidsense.command.Command;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Shortcut extends Command {

    private String name;
    private List<Pair<Command, String[]>> script;

    public Shortcut(String name, List<Pair<Command, String[]>> script) {
        super(name);
        this.name = name;
        this.script = script;
    }

    public String getName() {
        return name;
    }

    public List<Pair<Command, String[]>> getScript() {
        return script;
    }

    /**
     * Execute commands with provided [args]
     */
    @Override
    public void execute(@NotNull String[] args) {
        script.forEach(commandPair -> commandPair.getFirst().execute(commandPair.getSecond()));
    }
}
