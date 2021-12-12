package me.aquavit.liquidsense.modules.client;

import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.PacketEvent;
import me.aquavit.liquidsense.event.events.Render3DEvent;
import me.aquavit.liquidsense.modules.blatant.Aura;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.modules.world.ChestAura;
import me.aquavit.liquidsense.modules.world.Scaffold;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;

@ModuleInfo(name = "Rotations", description = "Allows you to see server-sided head and body rotations.", category = ModuleCategory.CLIENT)
public class Rotations extends Module {

    public static IntegerValue colorRedValue = new IntegerValue("R", 0, 0, 255);
    public static IntegerValue colorGreenValue = new IntegerValue("G", 160, 0, 255);
    public static IntegerValue colorBlueValue = new IntegerValue("B", 255, 0, 255);
    public static IntegerValue alphaValue = new IntegerValue("Alpha", 0, 0, 255);
    public static BoolValue rainbow = new BoolValue("RainBow", false);
    public static BoolValue ghost = new BoolValue("Ghost", true);

    private Float playerYaw;

    @EventTarget
    public void onRender3D(Render3DEvent event) {

    }

    @EventTarget
    public void onPacket(PacketEvent event) {

    }

    private boolean getState(Class<?> module) {
        return LiquidBounce.moduleManager.getModule(module).getState();
    }

    private boolean shouldRotate() {
        return getState(Scaffold.class) ||
                (getState(Aura.class) && ((Aura) LiquidBounce.moduleManager.getModule(Aura.class)).getTarget() != null) ||
                getState(ChestAura.class);
    }

}
