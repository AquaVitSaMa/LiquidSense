package net.ccbluex.liquidbounce.features.module;

import kotlin.jvm.internal.Intrinsics;
import me.aquavit.liquidsense.utils.mc.MinecraftInstance;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.Listenable;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Print;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class Module extends MinecraftInstance implements Listenable {
    protected String name;
    protected String description;
    protected ModuleCategory category;
    protected String arrayListName;
    protected boolean state;
    private int keyBind;
    private final boolean canEnable;
    private final float hue;
    private boolean array;
    private float slide;
    private float higt;
    private float slideStep;
    
    public void setKeyBind(final int keyBind) {
        this.keyBind = keyBind;
        LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.modulesConfig);
    }

    public Module() {
        this.name = this.getClass().getAnnotation(ModuleInfo.class).name();
        this.arrayListName = name;
        this.description = this.getClass().getAnnotation(ModuleInfo.class).description();
        this.category = this.getClass().getAnnotation(ModuleInfo.class).category();
        this.keyBind = this.getClass().getAnnotation(ModuleInfo.class).keyBind();
        this.canEnable = this.getClass().getAnnotation(ModuleInfo.class).canEnable();
        this.hue = (float) Math.random();
        this.array = true;
    }


    public void setState(final boolean state) {
        try {
            if (this.getState() == state) {
                return;
            }
            this.onToggle(state);

            if (!LiquidBounce.INSTANCE.isStarting()) {
                LiquidBounce.hud.addPrint(new Print(" " + name + (state ? " Enabled" : " Disabled"),3000f, Print.Type.state));
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.click"),
                        1F));
                LiquidBounce.hud.addNotification(new Notification(state ? "Enabled " : "Disabled "+name,"Toggled", NotifyType.SUCCESS,1500,500));
            }

            if (state) {
                this.onEnable();
                if (this.canEnable) {
                    this.state = true;
                }
            }
            else {
                this.onDisable();
                this.state = false;
            }
            LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.modulesConfig);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTag() {
        return null;
    }

    public final String getTagName() {
        return this.arrayListName + (this.getTag() == null ? "" : " §7" + this.getTag());
    }

    public final String getColorlessTagName() {
        return this.arrayListName + (this.getTag() == null ? "" : " " + ColorUtils.stripColor(this.getTag()));
    }

    public void toggle() {
        this.setState(!this.getState());
    }

    public final void setNameCommad(String namecommand) {
        this.arrayListName = namecommand;
    }

    public void onToggle(final boolean state) {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public Value<?> getValue(final String valueName) {
        for (final Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object o = field.get(this);
                if (o instanceof Value) {
                    final Value value = (Value)o;
                    if (value.getName().equalsIgnoreCase(valueName)) {
                        return value;
                    }
                }
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Value<?>> getValues() {
        final List<Value<?>> values = new ArrayList<Value<?>>();
        for (final Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object o = field.get(this);
                if (o instanceof Value) {
                    values.add((Value<?>)o);
                }
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    @Override
    public boolean handleEvents() {
        return this.getState();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public ModuleCategory getCategory() {
        return this.category;
    }

    public boolean getState() {
        return this.state;
    }

    public int getKeyBind() {
        return this.keyBind;
    }

    public final String getArrayListName() {
        return this.arrayListName;
    }

    public final void setArrayListName(String string) {
        this.arrayListName = string;
    }

    public final float getSlideStep() {
        return this.slideStep;
    }

    public final void setSlideStep(float slide) {
        this.slideStep = slide;
    }

    public final float getHue() {
        return this.hue;
    }

    public final float getSlide() {
        return this.slide;
    }

    public final void setSlide(float slide) {
        this.slide = slide;
    }

    public final boolean getArray() {
        return this.array;
    }

    public final void setArray(boolean state) {
        this.array = state;
    }

    public final float getHigt() {
        return this.higt;
    }

    public final void setHigt(float higt) {
        this.higt = higt;
    }
}
