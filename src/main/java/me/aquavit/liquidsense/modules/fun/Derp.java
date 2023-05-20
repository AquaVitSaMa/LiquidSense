package me.aquavit.liquidsense.modules.fun;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.value.BoolValue;
import me.aquavit.liquidsense.value.FloatValue;

@ModuleInfo(name = "Derp", description = "Makes it look like you were derping around.", category = ModuleCategory.FUN)
public class Derp extends Module {
    public static BoolValue headlessValue = new BoolValue("Headless", false);
    public static BoolValue spinnyValue = new BoolValue("Spinny", false);
    public static FloatValue incrementValue = new FloatValue("Increment", 1.0f, 0.0f, 50.0f);

    public static float currentSpin;

    public static float[] getRotation() {
        float[] derpRotations = new float[]{mc.thePlayer.rotationYaw + (float)(Math.random() * (double)360 - (double)180), (float)(Math.random() * (double)180 - (double)90)};

        if (headlessValue.get()) derpRotations[1] = 180.0f;

        if (spinnyValue.get()) {
            derpRotations[0] = currentSpin + incrementValue.get();
            currentSpin = derpRotations[0];
        }

        return derpRotations;
    }
}
