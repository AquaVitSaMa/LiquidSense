package net.ccbluex.liquidbounce.features.command.commands;

import com.google.gson.JsonParser;
import me.aquavit.liquidsense.utils.misc.StringUtils;
import me.aquavit.liquidsense.utils.module.SettingsUtils;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.command.Command;
import net.ccbluex.liquidbounce.file.FileManager;
import net.ccbluex.liquidbounce.file.configs.FriendsConfig;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LocalAutoSettingsCommand extends Command {

    public LocalAutoSettingsCommand(){
        super("localautosettings", "localsetting", "localsettings", "localconfig");
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            if (args[1].equals("load")){
                if (args.length > 2) {
                    File scriptFile = new File(LiquidBounce.fileManager.settingsDir, args[2]);
                    if (scriptFile.exists()) {
                        try {
                            chat("§9Loading settings...");

                            final String settings = new JsonParser().parse(new BufferedReader(new FileReader(scriptFile))).getAsString();
                            chat("§9Set settings...");
                            SettingsUtils.executeScript(StringUtils.toLines(settings));
                            chat("§6Settings applied successfully.");
                            LiquidBounce.hud.addNotification(new Notification("Updated Settings", "Setting was updated.", NotifyType.SUCCESS,1500,500));
                            playEdit();

                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    chat("§cSettings file does not exist!");
                    return;
                }
                chatSyntax("localautosettings load <name>");
                return;
            }
            if (args[1].equals("save")){
                if (args.length > 2) {
                    File scriptFile = new File(LiquidBounce.fileManager.settingsDir, args[2]);

                    try {
                        if (scriptFile.exists())
                            scriptFile.delete();
                        scriptFile.createNewFile();

                        String option = args.length > 3 ? StringUtils.toCompleteString(args, 3).toLowerCase() : "values";
                        boolean values = option.contains("all") || option.contains("values");
                        boolean binds = option.contains("all") || option.contains("binds");
                        boolean states = option.contains("all") || option.contains("states");
                        if (!values && !binds && !states) {
                            chatSyntaxError();
                            return;
                        }

                        chat("§9Creating settings...");
                        String settingsScript = SettingsUtils.generateScript(values, binds, states);
                        chat("§9Saving settings...");
                        final PrintWriter printWriter = new PrintWriter(new FileWriter(scriptFile));
                        printWriter.println(FileManager.PRETTY_GSON.toJson(settingsScript));
                        printWriter.close();
                        chat("§6Settings saved successfully.");
                    } catch (Throwable throwable) {
                        chat("§cFailed to create local config: §3"+throwable.getMessage());
                        ClientUtils.getLogger().error("Failed to create local config.", throwable);
                    }
                    return;
                }

                chatSyntax("localsettings save <name> [all/values/binds/states]...");
                return;
            }
            if (args[1].equals("delete")){
                if (args.length > 2) {
                    File scriptFile = new File(LiquidBounce.fileManager.settingsDir, args[2]);

                    if (scriptFile.exists()) {
                        scriptFile.delete();
                        chat("§6Settings file deleted successfully.");
                        return;
                    }

                    chat("§cSettings file does not exist!");
                    return;
                }

                chatSyntax("localsettings delete <name>");
                return;
            }
            if (args[1].equals("list")){
                chat("§cSettings:");

                File[] settings = this.getLocalSettings();

                if (settings == null) {
                    return;
                }

                for (File file : settings)
                    chat("> " + file.getName());
                return;
            }

        }
        chatSyntax("localsettings <load/save/list/delete>");
    }

    @Override
    public List<String> tabComplete(String[] args) {
        if (args.length == 0) return new ArrayList<>();

        switch (args.length) {
            case 1:
                return Arrays.stream(new String[]{"delete", "list", "load", "save"})
                        .filter(it -> it.startsWith(args[0]))
                        .collect(Collectors.toList());
            case 2:{
                switch (args[0].toLowerCase()){
                    case "delete":
                    case "load":{
                        File[] settings = this.getLocalSettings();

                        if (settings == null) {
                            return new ArrayList<>();
                        }

                        return Arrays.stream(settings).map(File::getName).filter(it -> it.startsWith(args[1])).collect(Collectors.toList());
                    }
                    default:
                        return new ArrayList<>();
                }
            }
            default:
                return new ArrayList<>();
        }
    }

    private File[] getLocalSettings(){
        return LiquidBounce.fileManager.settingsDir.listFiles();
    }
}
