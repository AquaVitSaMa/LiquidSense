package me.aquavit.liquidsense.command;

import me.aquavit.liquidsense.LiquidSense;
import me.aquavit.liquidsense.utils.data.Pair;
import me.aquavit.liquidsense.command.commands.*;
import me.aquavit.liquidsense.command.shortcuts.Shortcut;
import me.aquavit.liquidsense.command.shortcuts.ShortcutParser;
import me.aquavit.liquidsense.utils.client.ClientUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CommandManager {

    private List<Command> commands = new ArrayList<>();
    private String[] latestAutoComplete = new String[0];

    private char prefix = '.';

    public void setPrefix(char prefix) {
        this.prefix = prefix;
    }

    public char getPrefix() {
        return prefix;
    }

    public String[] getLatestAutoComplete() {
        return latestAutoComplete;
    }

    public List<Command> getCommands() {
        return commands;
    }

    /**
     * Register all default commands
     */
    public void registerCommands() {
        registerCommand(new BindCommand());
        registerCommand(new HelpCommand());
        registerCommand(new SayCommand());
        registerCommand(new FriendCommand());
        registerCommand(new AutoSettingsCommand());
        registerCommand(new LocalAutoSettingsCommand());
        registerCommand(new ServerInfoCommand());
        registerCommand(new ToggleCommand());
        registerCommand(new UsernameCommand());
        registerCommand(new TargetCommand());
        registerCommand(new BindsCommand());
        registerCommand(new PanicCommand());
        registerCommand(new PingCommand());
        registerCommand(new RenameCommand());
        registerCommand(new ReloadCommand());
        registerCommand(new LoginCommand());
        registerCommand(new RemoteViewCommand());
        registerCommand(new PrefixCommand());
        registerCommand(new ShortcutCommand());
        registerCommand(new HideCommand());
        registerCommand(new SetNameCommand());
        registerCommand(new XrayCommand());
    }


    /**
     * Execute command by given [input]
     */
    public void executeCommands(String input) {
        for (Command command : commands) {
            String[] args = input.split(" ");

            if (args[0].equalsIgnoreCase(prefix + command.getCommand())) {
                command.execute(args);
                return;
            }

            for (String alias : command.getAlias()) {
                if (!args[0].equalsIgnoreCase(prefix + alias))
                    continue;

                command.execute(args);
                return;
            }
        }

        ClientUtils.displayChatMessage("§cCommand not found. Type "+prefix+"help to view all commands.");
    }

    public Command getCommand(String name) {
        return commands.stream()
                .filter(it -> it.getCommand().equalsIgnoreCase(name)
                        || Arrays.stream(it.getAlias()).anyMatch(alias -> alias.equalsIgnoreCase(name)))
                .findFirst()
                .orElse(null);
    }

    /**
     * Register [command] by just adding it to the commands registry
     */
    public void registerCommand(Command command) {
        commands.add(command);
    }

    /**
     * Unregister [command] by just removing it from the commands registry
     */
    public void unregisterCommand(Command command) {
        commands.remove(command);
    }

    /**
     * Updates the [latestAutoComplete] array based on the provided [input].
     *
     * @param input text that should be used to check for auto completions.
     * @author NurMarvin
     */
    public boolean autoComplete(String input) {
        String[] completions = this.getCompletions(input);
        if (completions != null) {
            this.latestAutoComplete = completions;
        } else {
            this.latestAutoComplete = new String[0];
        }
        return input.startsWith(String.valueOf(this.prefix)) && this.latestAutoComplete.length > 0;
    }

    /**
     * Returns the auto completions for [input].
     *
     * @param input text that should be used to check for auto completions.
     * @author NurMarvin
     */
    private String[] getCompletions(String input) {
        if (input.length() > 0 && input.toCharArray()[0] == this.prefix) {
            String[] args = input.split(" ");

            if (args.length > 1) {
                Command command = getCommand(args[0].substring(1));
                if (command == null) return null;
                List<String> tabCompletions = command.tabComplete(Arrays.copyOfRange(args, 1, args.length));

                return tabCompletions.toArray(new String[0]);
            } else {
                String rawInput = input.substring(1);

                return commands.stream()
                        .filter(it -> it.getCommand().toLowerCase().startsWith(rawInput.toLowerCase())
                                || Arrays.stream(it.getAlias()).anyMatch(alias -> alias.toLowerCase().startsWith(rawInput.toLowerCase())))
                        .map(it -> {
                            String alias;
                            if (it.getCommand().toLowerCase().startsWith(rawInput.toLowerCase())) {
                                alias = it.getCommand();
                            } else {
                                alias = Arrays.stream(it.getAlias())
                                        .filter(temp -> temp.toLowerCase()
                                                .startsWith(rawInput.toLowerCase()))
                                        .findFirst().orElse(null);
                            }

                            return this.prefix + alias;
                        }).toArray(String[]::new);
            }
        }
        return null;
    }

    public void registerShortcut(String name, String script) {
        if (getCommand(name) == null) {
            registerCommand(new Shortcut(name, ShortcutParser.parse(script).stream().map(it -> {
                Command command = getCommand(it.get(0));
                if (command != null) {
                    return new Pair<>(command, it.toArray(new String[0]));
                } else {
                    throw new IllegalArgumentException("Command " + it.get(0) + " not found!");
                }
            }).collect(Collectors.toCollection((Supplier<List<Pair<Command,String[]>>>)ArrayList::new))));

            LiquidSense.fileManager.saveConfig(LiquidSense.fileManager.shortcutsConfig);
        } else {
            throw new IllegalArgumentException("Command already exists!");
        }
    }

    public boolean unregisterShortcut(String name) {
        boolean removed = commands.removeIf(it -> it instanceof Shortcut && it.getCommand().equals(name));

        LiquidSense.fileManager.saveConfig(LiquidSense.fileManager.shortcutsConfig);

        return removed;
    }
}
