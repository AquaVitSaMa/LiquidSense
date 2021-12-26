package net.ccbluex.liquidbounce.script.remapper;

import me.aquavit.liquidsense.utils.client.ClientUtils;
import me.aquavit.liquidsense.utils.misc.HttpUtils;
import net.ccbluex.liquidbounce.LiquidBounce;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Remapper {

    public static final Remapper INSTANCE = new Remapper();

    private final String srgName = "stable_22";
    private static File srgFile = new File(LiquidBounce.fileManager.dir, "mcp-$srgName.srg");

    private static HashMap<String, HashMap<String, String>> fields = new HashMap<>();
    private static HashMap<String, HashMap<String, String>> methods = new HashMap<>();

    /**
     * Load srg
     */
    public void loadSrg() {
        // Check if srg file is already downloaded
        if (!srgFile.exists()) {
            // Download srg file
            try {
                srgFile.createNewFile();
            } catch (Exception e) {
                ClientUtils.getLogger().error("[Remapper] Failed to create new file");
            }

            ClientUtils.getLogger().info("[Remapper] Downloading $srgName srg...");
            try {
                HttpUtils.download("${LiquidBounce.CLIENT_CLOUD}/srgs/mcp-$srgName.srg", srgFile);
            } catch (IOException e) {
                ClientUtils.getLogger().error("[Remapper] Download " + srgName + " failed.");
            }
            ClientUtils.getLogger().info("[Remapper] Downloaded $srgName.");
        }

        // Load srg
        ClientUtils.getLogger().info("[Remapper] Loading srg...");
        try {
            parseSrg();
        } catch (Exception e) {
            ClientUtils.getLogger().error("[Remapper] Failed to load srg.");
            e.printStackTrace();
        }
        ClientUtils.getLogger().info("[Remapper] Loaded srg.");
    }

    private void parseSrg() throws Exception {


        FileUtils.readLines(srgFile).forEach(it -> {
            String[] args = it.split(" ");

            if (it.startsWith("FD:")) {
                String name = args[1];
                String srg = args[2];

                String className = name.substring(0, name.lastIndexOf('/')).replace('/', '.');
                String fieldName = name.substring(name.lastIndexOf('/') + 1);
                String fieldSrg = srg.substring(srg.lastIndexOf('/') + 1);

                if (!fields.containsKey(className))
                    fields.put(className,new HashMap<>());

                HashMap<String, String> temp = fields.get(className);
                temp.put(fieldSrg,fieldName);
                fields.put(className,temp);
            } else if (it.startsWith("MD:")) {
                String name = args[1];
                String desc = args[2];
                String srg = args[3];

                String className = name.substring(0, name.lastIndexOf('/')).replace('/', '.');
                String methodName = name.substring(name.lastIndexOf('/') + 1);
                String methodSrg = srg.substring(srg.lastIndexOf('/') + 1);

                if (!methods.containsKey(className))
                    methods.put(className,new HashMap<>());

                HashMap<String, String> temp = methods.get(className);
                temp.put(methodSrg + desc,methodName);
                methods.put(className,temp);
            }
        });
    }


    /**
     * Remap field
     */
    public static String remapField(Class<?> clazz, String name) {
        if (!fields.containsKey(clazz.getName()))
            return name;

        return fields.get(clazz.getName()).getOrDefault(name, name);
    }

    /**
     * Remap method
     */
    public static String remapMethod(Class<?> clazz, String name, String desc) {
        if (!methods.containsKey(clazz.getName()))
            return name;

        return methods.get(clazz.getName()).getOrDefault(name + desc, name);
    }
}
