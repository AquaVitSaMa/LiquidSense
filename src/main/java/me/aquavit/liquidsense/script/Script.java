package me.aquavit.liquidsense.script;

import jdk.internal.dynalink.beans.StaticClass;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptUtils;
import me.aquavit.liquidsense.command.Command;
import me.aquavit.liquidsense.utils.client.ClientUtils;
import me.aquavit.liquidsense.utils.mc.MinecraftInstance;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Function;
import java.io.File;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.JSObject;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import me.aquavit.liquidsense.script.api.ScriptCommand;
import me.aquavit.liquidsense.script.api.ScriptTab;
import me.aquavit.liquidsense.script.api.global.Chat;
import me.aquavit.liquidsense.script.api.global.Item;
import me.aquavit.liquidsense.script.api.global.ScriptModule;
import me.aquavit.liquidsense.script.api.global.Setting;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Script extends MinecraftInstance {

    public File scriptFile;
    private final ScriptEngine scriptEngine;
    private final String scriptText;
    public String scriptName;
    public String scriptVersion;
    public String[] scriptAuthors;
    private boolean state = false;
    private final HashMap<String, JSObject> events = new HashMap<>();
    private final java.util.List<Module> registeredModules = new java.util.ArrayList<>();
    private final java.util.List<Command> registeredCommands = new java.util.ArrayList<>();

    public Script(File scriptFile) {
        this.scriptFile = scriptFile;
        try {
            this.scriptText = FileUtils.readFileToString(scriptFile, java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read script file", e);
        }
        String[] engineFlags = getMagicComment("engine_flags") != null ? getMagicComment("engine_flags").split(",") : new String[0];
        this.scriptEngine = new NashornScriptEngineFactory().getScriptEngine(engineFlags);

        // Global classes
        scriptEngine.put("Chat", StaticClass.forClass(Chat.class));
        scriptEngine.put("Setting", StaticClass.forClass(Setting.class));
        scriptEngine.put("Item", StaticClass.forClass(Item.class));

        // Global instances
        scriptEngine.put("mc", mc);

        scriptEngine.put("moduleManager", LiquidBounce.moduleManager);
        scriptEngine.put("commandManager", LiquidBounce.commandManager);
        scriptEngine.put("scriptManager", LiquidBounce.scriptManager);

        // Global functions
        scriptEngine.put("registerScript", new RegisterScript());

        supportLegacyScripts();
    }

    public void initScript() {
        try {
            scriptEngine.eval(scriptText);
        } catch (ScriptException e) {
            e.printStackTrace();
            return;
        }

        callEvent("load");

        ClientUtils.getLogger().info("[ScriptAPI] Successfully loaded script '" + scriptFile.getName() + "'.");
    }

    private class RegisterScript implements Function<JSObject, Script> {
        /**
         * Global function 'registerScript' which is called to register a script.
         * @param scriptObject JavaScript object containing information about the script.
         * @return The instance of this script.
         */
        @Override
        public Script apply(JSObject scriptObject) {
            scriptName = (String) scriptObject.getMember("name");
            scriptVersion = (String) scriptObject.getMember("version");
            scriptAuthors = (String[]) ScriptUtils.convert(scriptObject.getMember("authors"), String[].class);

            return Script.this;
        }
    }

    public void registerModule(JSObject moduleObject, JSObject callback) {
        Module module = new ScriptModule(moduleObject);
        LiquidBounce.moduleManager.registerModule(module);
        registeredModules.add(module);
        callback.call(moduleObject, module);
    }

    public void registerCommand(JSObject commandObject, JSObject callback) {
        Command command = new ScriptCommand(commandObject);
        LiquidBounce.commandManager.registerCommand(command);
        registeredCommands.add(command);
        callback.call(commandObject, command);
    }

    public void registerTab(JSObject tabObject) {
        new ScriptTab(tabObject);
    }

    private String getMagicComment(String name) {
        String magicPrefix = "///";

        for (String line : scriptText.split("\\n")) {
            if (!line.startsWith(magicPrefix)) {
                continue;
            }

            String[] commentData = line.substring(magicPrefix.length()).split("=", 2);

            if (commentData[0].trim().equals(name)) {
                return commentData[1].trim();
            }
        }

        return null;
    }

    private void supportLegacyScripts() {
        String magicComment = getMagicComment("api_version");
        if (magicComment != null && !magicComment.equals("2")) {
            ClientUtils.getLogger().info("[ScriptAPI] Running script '" + scriptFile.getName() + "' with legacy support.");
            try (InputStream inputStream = LiquidBounce.class.getResourceAsStream("/assets/minecraft/liquidbounce/scriptapi/legacy.js")) {
                if (inputStream != null) {
                    String legacyScript = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    scriptEngine.eval(legacyScript);
                } else {
                    throw new IOException("Input stream is null");
                }
            } catch (IOException | ScriptException e) {
                e.printStackTrace();
            }
        }
    }

    public void onEnable() {
        if (state) return;

        callEvent("enable");
        state = true;
    }

    public void onDisable() {
        if (!state) return;

        for (Module module : registeredModules) {
            LiquidBounce.moduleManager.unregisterModule(module);
        }
        for (Command command : registeredCommands) {
            LiquidBounce.commandManager.unregisterCommand(command);
        }

        callEvent("disable");
        state = false;
    }

    public void importScript(String scriptFile) {
        File file = new File(LiquidBounce.scriptManager.getScriptsFolder(), scriptFile);
        if (!file.exists() || !file.isFile()) {
            System.err.println("Failed to load script file: " + file.getAbsolutePath());
            return;
        }

        try {
            String scriptText = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            scriptEngine.eval(scriptText);
        } catch (IOException | ScriptException e) {
            e.printStackTrace();
        }
    }

    private void callEvent(String eventName) {
        try {
            events.get(eventName).call(null);
        } catch (NullPointerException e) {
            ClientUtils.getLogger().error("[ScriptAPI] Exception in script '" + scriptName + "'!", e.getMessage());
        }
    }
}
