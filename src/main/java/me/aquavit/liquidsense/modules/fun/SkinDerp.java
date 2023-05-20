package me.aquavit.liquidsense.modules.fun;

import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.utils.timer.MSTimer;
import me.aquavit.liquidsense.value.BoolValue;
import me.aquavit.liquidsense.value.IntegerValue;
import net.minecraft.entity.player.EnumPlayerModelParts;

import java.util.Random;
import java.util.Set;

@ModuleInfo(name = "SkinDerp", description = "Makes your skin blink (Requires multi-layer skin).", category = ModuleCategory.FUN)
public class SkinDerp extends Module {

    private IntegerValue delayValue = new IntegerValue("Delay", 0, 0, 1000);
    private BoolValue hatValue = new BoolValue("Hat", true);
    private BoolValue jacketValue = new BoolValue("Jacket", true);
    private BoolValue leftPantsValue = new BoolValue("LeftPants", true);
    private BoolValue rightPantsValue = new BoolValue("RightPants", true);
    private BoolValue leftSleeveValue = new BoolValue("LeftSleeve", true);
    private BoolValue rightSleeveValue = new BoolValue("RightSleeve", true);

    private Set<EnumPlayerModelParts> prevModelParts;

    private MSTimer timer = new MSTimer();
    private Random random = new Random();

    @Override
    public void onEnable() {
        prevModelParts = mc.gameSettings.getModelParts();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        // Disable all current model parts

        for (EnumPlayerModelParts modelPart : mc.gameSettings.getModelParts())
            mc.gameSettings.setModelPartEnabled(modelPart, false);

        // Enable all old model parts
        for (EnumPlayerModelParts modelPart : mc.gameSettings.getModelParts())
            mc.gameSettings.setModelPartEnabled(modelPart, true);

        super.onDisable();
    }


    @EventTarget
    public void onUpdate(UpdateEvent event){
        if (timer.hasTimePassed(delayValue.get())) {
            if (hatValue.get())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.HAT, random.nextBoolean());
            if (jacketValue.get())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.JACKET, random.nextBoolean());
            if (leftPantsValue.get())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_PANTS_LEG, random.nextBoolean());
            if (rightPantsValue.get())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_PANTS_LEG, random.nextBoolean());
            if (leftSleeveValue.get())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_SLEEVE, random.nextBoolean());
            if (rightSleeveValue.get())
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_SLEEVE, random.nextBoolean());
            timer.reset();
        }
    }
}
