package me.aquavit.liquidsense.script.api.global;

import java.util.*;

import jdk.nashorn.api.scripting.JSObject;
import me.aquavit.liquidsense.event.EventTarget;
import me.aquavit.liquidsense.event.events.*;
import me.aquavit.liquidsense.utils.client.ClientUtils;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import me.aquavit.liquidsense.value.Value;

@ModuleInfo(name = "ScriptModule", description = "Empty", category = ModuleCategory.MISC)
public class ScriptModule extends Module {
    private final HashMap<String, JSObject> events = new HashMap<>();
    private final LinkedHashMap<String, Value<?>> _values = new LinkedHashMap<>();
    private String _tag;

    private JSObject moduleObject;

    /**
     * Allows the user to access values by typing module.settings.<valuename>
     */
    public LinkedHashMap<String, Value<?>> getSettings() {
        return _values;
    }

    public ScriptModule(JSObject moduleObject) {
        this.moduleObject = moduleObject;
        name = (String) moduleObject.getMember("name");
        description = (String) moduleObject.getMember("description");

        String categoryString = (String) moduleObject.getMember("category");
        for (ModuleCategory category : ModuleCategory.values()) {
            if (categoryString.equalsIgnoreCase(category.getDisplayName())) {
                this.category = category;
                break;
            }
        }

        if (moduleObject.hasMember("settings")) {
            JSObject settings = (JSObject) moduleObject.getMember("settings");

            for (String settingName : settings.keySet())
                _values.put(settingName, (Value<?>) settings.getMember(settingName));
        }

        if (moduleObject.hasMember("tag"))
            _tag = (String) moduleObject.getMember("tag");
    }

    @Override
    public List<Value<?>> getValues() {
        return new ArrayList<>(_values.values());
    }

    @Override
    public String getTag() {
        return _tag;
    }

    /**
     * Called from inside the script to register a new event handler.
     * @param eventName Name of the event.
     * @param handler JavaScript function used to handle the event.
     */
    public void on(String eventName, JSObject handler) {
        events.put(eventName, handler);
    }

    @Override
    public void onEnable() {
        callEvent("enable", null);
    }

    @Override
    public void onDisable() {
        callEvent("disable", null);
    }

    @EventTarget
    public void onUpdate(UpdateEvent updateEvent) {
        callEvent("update", null);
    }

    @EventTarget
    public void onRender2D(Render2DEvent render2DEvent) {
        callEvent("render2D", render2DEvent);
    }

    @EventTarget
    public void onRender3D(Render3DEvent render3DEvent) {
        callEvent("render3D", render3DEvent);
    }

    @EventTarget
    public void onPacket(PacketEvent packetEvent) {
        callEvent("packet", packetEvent);
    }

    @EventTarget
    public void onJump(JumpEvent jumpEvent) {
        callEvent("jump", jumpEvent);
    }

    @EventTarget
    public void onAttack(AttackEvent attackEvent) {
        callEvent("attack", attackEvent);
    }

    @EventTarget
    public void onKey(KeyEvent keyEvent) {
        callEvent("key", keyEvent);
    }

    @EventTarget
    public void onMove(MoveEvent moveEvent) {
        callEvent("move", moveEvent);
    }

    @EventTarget
    public void onStep(StepEvent stepEvent) {
        callEvent("step", stepEvent);
    }

    @EventTarget
    public void onStepConfirm(StepConfirmEvent stepConfirmEvent) {
        callEvent("stepConfirm", null);
    }

    @EventTarget
    public void onWorld(WorldEvent worldEvent) {
        callEvent("world", worldEvent);
    }

    @EventTarget
    public void onSession(SessionEvent sessionEvent) {
        callEvent("session", null);
    }

    @EventTarget
    public void onClickBlock(ClickBlockEvent clickBlockEvent) {
        callEvent("clickBlock", clickBlockEvent);
    }

    @EventTarget
    public void onStrafe(StrafeEvent strafeEvent) {
        callEvent("strafe", strafeEvent);
    }

    @EventTarget
    public void onSlowDown(SlowDownEvent slowDownEvent) {
        callEvent("slowDown", slowDownEvent);
    }

    @EventTarget
    public void onShutdown(ClientShutdownEvent shutdownEvent) {
        callEvent("shutdown", null);
    }



    /**
     * Calls the handler of a registered event.
     * @param eventName Name of the event to be called.
     * @param payload Event data passed to the handler function.
     */
    private void callEvent(String eventName, Object payload) {
        try {
            events.get(eventName).call(moduleObject, payload);
        } catch (Throwable throwable) {
            ClientUtils.getLogger().error("[ScriptAPI] Exception in module '" + name + "'!", throwable);
        }
    }

}
