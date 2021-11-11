package net.ccbluex.liquidbounce.features.command.commands;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.aquavit.liquidsense.utils.misc.StringUtils;
import me.aquavit.liquidsense.utils.client.SettingsUtils;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.command.Command;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType;
import net.ccbluex.liquidbounce.utils.misc.HttpUtils;
import net.mcleaks.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AutoSettingsCommand extends Command {

    private Object loadingLock = new Object();
    private List<String> autoSettingFiles = new ArrayList<>();

    public AutoSettingsCommand() {
        super("autosettings", "setting", "settings", "config", "autosetting");
    }

    /**
     * Execute commands with provided [args]
     */
    public void execute(String[] args) {
        if (args.length <= 1) {
            chatSyntax("settings <load/list>");

            return;
        }

        if (args[1].toLowerCase().equals("load")) {
            if (args.length < 3) {
                chatSyntax("settings load <name/url>");
                return;
            }

            // Settings url
            String url;
            if (args[2].startsWith("http")) {
                url = args[2];
            } else {
                url = LiquidBounce.CLIENT_CLOUD + "/settings/" + args[2].toLowerCase();
            }

            chat("Loading settings...");

            new Thread(() -> {
                try {
                    // Load settings and apply them
                    String settings = HttpUtils.get(url);

                    chat("Applying settings...");
                    SettingsUtils.executeScript(StringUtils.toLines(settings));
                    chat("§6Settings applied successfully");
                    LiquidBounce.hud.addNotification(new Notification("Updated Settings", "Setting was updated.", NotifyType.SUCCESS, 1500, 500));
                    playEdit();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    chat("Failed to fetch auto settings.");
                }
            }).start();
        } else if (args[1].toLowerCase().equals("list")) {
            chat("Loading settings...");

            loadSettings(false, null, list -> {
                for (String setting : list)
                    chat("> " + setting);
            });
        }
    }

    private void loadSettings(boolean useCached, Long join, Callback<List<String>> callback) {


        Thread thread = new Thread(() -> {
            // Prevent the settings from being loaded twice
            synchronized (loadingLock) {
                if (useCached && autoSettingFiles != null) {
                    callback.done(autoSettingFiles);
                    return;
                }

                try {
                    JsonElement json = new JsonParser().parse(HttpUtils.get(
                            // TODO: Add another way to get all settings
                            "https://api.github.com/repos/CCBlueX/LiquidCloud/contents/LiquidBounce/settings"
                    ));

                    List<String> autoSettings = new ArrayList<>();

                    if (json instanceof JsonArray){
                        for (JsonElement setting : ((JsonArray) json))
                            autoSettings.add(setting.getAsJsonObject().get("name").getAsString());
                    }

                    callback.done(autoSettings);

                    this.autoSettingFiles = autoSettings;
                } catch (Exception e) {
                    e.printStackTrace();
                    chat("Failed to fetch auto settings list.");
                }
            }
        });

        thread.start();

        if (join != null) {
            try {
                thread.join(join);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<String> tabComplete(String[] args) {
        if (args.length == 0) return new ArrayList<>();

        switch (args.length) {
            case 1:
                return Lists.newArrayList("list", "load").stream()
                        .filter(command -> command.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            case 2:
                if (args[0].equalsIgnoreCase("load")) {
                    if (autoSettingFiles == null) {
                        this.loadSettings(true, (long) 500, list -> {});
                    }

                    if (autoSettingFiles != null) {
                        return autoSettingFiles.stream()
                                .filter(setting -> setting.toLowerCase().startsWith(args[1].toLowerCase()))
                                .collect(Collectors.toList());
                    }
                }
                return new ArrayList<>();
            default:
                return new ArrayList<>();
        }
    }
}
