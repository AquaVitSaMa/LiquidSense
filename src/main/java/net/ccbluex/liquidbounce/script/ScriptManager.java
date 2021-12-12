package net.ccbluex.liquidbounce.script;

import me.aquavit.liquidsense.utils.client.ClientUtils;
import net.ccbluex.liquidbounce.LiquidBounce;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ScriptManager {
    private final List<Script> scripts = new ArrayList<Script>();
    private final File scriptsFolder = new File(LiquidBounce.fileManager.dir, "scripts");
    private final String scriptFileExtension = ".js";

    public final void loadScripts() {
        if (!scriptsFolder.exists()) {
            scriptsFolder.mkdir();
        }

        Arrays.stream(Objects.requireNonNull(scriptsFolder.listFiles(file -> file.getName().endsWith(scriptFileExtension)))).forEach(this::loadScript);
    }

    public final void unloadScripts() {
        scripts.clear();
    }

    public final void loadScript(File scriptFile) {
        try {
            Script script = new Script(scriptFile);
            script.initScript();
            scripts.add(script);
        }
        catch (Throwable t) {
            ClientUtils.getLogger().error("[ScriptAPI] Failed to load script '" + scriptFile.getName() + "'.", t);
        }
    }

    public final void enableScripts() {
        scripts.forEach(Script::onEnable);
    }

    public final void disableScripts() {
        scripts.forEach(Script::onDisable);
    }

    public final void importScript(final File file) {
        File scriptFile = new File(scriptsFolder, file.getName());
        try {
            FileUtils.copyFile(file,scriptFile);
        }catch (Exception e){
            ClientUtils.getLogger().info("[ScriptAPI] Failed to copy script '" + scriptFile.getName() + "'.");
        }
        loadScript(scriptFile);
        ClientUtils.getLogger().info("[ScriptAPI] Successfully imported script '" + scriptFile.getName() + "'.");
    }

    public final void deleteScript(Script script) {
        script.onDisable();
        scripts.remove(script);
        script.getScriptFile().delete();
        ClientUtils.getLogger().info("Successfully deleted script: " + script.getScriptFile().getName());
    }

    public final void reloadScripts() {
        disableScripts();
        unloadScripts();
        loadScripts();
        enableScripts();
        ClientUtils.getLogger().info("Successfully reloaded scripts.");
    }

    public File getScriptsFolder() {
        return scriptsFolder;
    }
}
