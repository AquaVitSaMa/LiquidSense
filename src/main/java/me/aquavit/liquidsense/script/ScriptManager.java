package me.aquavit.liquidsense.script;

import me.aquavit.liquidsense.utils.client.ClientUtils;
import net.ccbluex.liquidbounce.LiquidBounce;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;

public class ScriptManager {

    private List<Script> scripts = new ArrayList<Script>();
    private File scriptsFolder = new File(LiquidBounce.fileManager.dir, "scripts");
    private String scriptFileExtension = ".js";

    public List<Script> getScripts() {
        return scripts;
    }

    public File getScriptsFolder() {
        return scriptsFolder;
    }

    public void loadScripts() {
        if (!scriptsFolder.exists())
            scriptsFolder.mkdir();

        Arrays.stream(Objects.requireNonNull(scriptsFolder.listFiles(file -> file.getName().endsWith(scriptFileExtension)))).forEach(this::loadScript);
    }


    public void unloadScripts() {
        scripts.clear();
    }

    public void loadScript(File scriptFile) {
        try {
            Script script = new Script(scriptFile);
            script.initScript();
            scripts.add(script);
        }
        catch (Throwable t) {
            ClientUtils.getLogger().error("[ScriptAPI] Failed to load script '" + scriptFile.getName() + "'.", t);
        }
    }

    public void enableScripts() {
        scripts.forEach(Script::onEnable);
    }

    public void disableScripts() {
        scripts.forEach(Script::onDisable);
    }

    public void importScript(final File file) {
        File scriptFile = new File(scriptsFolder, file.getName());
        try {
            FileUtils.copyFile(file,scriptFile);
        }catch (Exception e){
            ClientUtils.getLogger().info("[ScriptAPI] Failed to copy script '" + scriptFile.getName() + "'.");
        }
        loadScript(scriptFile);
        ClientUtils.getLogger().info("[ScriptAPI] Successfully imported script '" + scriptFile.getName() + "'.");
    }

    public void deleteScript(Script script) {
        script.onDisable();
        scripts.remove(script);
        script.scriptFile.delete();
        ClientUtils.getLogger().info("Successfully deleted script: " + script.scriptFile.getName());
    }

    public void reloadScripts() {
        disableScripts();
        unloadScripts();
        loadScripts();
        enableScripts();
        ClientUtils.getLogger().info("Successfully reloaded scripts.");
    }

}
