package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import me.AquaVit.liquidSense.utils.render.Translate;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class Notification {

    private final float timer;
    public float x;
    public float y;
    public int textLength;
    public FadeState fadeState;
    public Type type;
    public String message;
    public boolean removeing;
    public Translate translate;
    public Translate removeingtranslate;
    private float stay;
    public String typemsg;

    public Notification(String Message, float Timer, Type T) {
        this.message = Message;
        this.timer = Timer;
        removeing = false;
        fadeState = FadeState.IN;
        type = T;
        translate = new Translate(0f, 0f);
        removeingtranslate = new Translate(0f, 0f);
    }

    public void drawNotification() {

        switch (type) {
            case state: {
                typemsg = "V";
                break;
            }
            case error: {
                typemsg = "U";
                break;
            }
            case info: {
                typemsg = "M";
                break;
            }
            case success: {
                typemsg = "T";
                break;
            }
        }

        textLength = 60;
        float width = textLength + 8F;

        if (150 - removeingtranslate.getX() > 30) {
            GlStateManager.pushMatrix();
            GlStateManager.resetColor();
            if (!message.isEmpty() && type != Type.none) {
                RenderUtils.drawGradientSideway(-width + 14 + textLength, y -5, -width - (Fonts.csgo40.getStringWidth(typemsg) + Fonts.font40.getStringWidth(message) + 10f), y - 15f, new Color(0, 0, 0, (150 - (int) removeingtranslate.getX())).getRGB(), new Color(0, 0, 0, 0).getRGB());
                Fonts.csgo40.drawString(typemsg, -width - 32, y - 7f, new Color(0, 131, 193, (150 - (int) removeingtranslate.getX())).getRGB());
                Fonts.font40.drawString(message, -width - 32 + Fonts.csgo40.getStringWidth(typemsg), y - 7f, new Color(255, 255, 255, (150 - (int) removeingtranslate.getX())).getRGB());
            }
            GlStateManager.popMatrix();
        }

        switch (fadeState) {
            case IN: {
                stay = timer;
                fadeState = FadeState.STAY;
                break;
            }
            case STAY: {
                if (stay > 0) {
                    stay -= RenderUtils.deltaTime;
                } else {
                    fadeState = FadeState.OUT;
                }
                break;
            }
            case OUT: {
                removeing = LiquidBounce.hud.getNotifications().size() * 11 <= y + 11 || removeingtranslate.getX() > 0;
                if (removeing) {
                    removeingtranslate.translate(150f, 0f, 1.0);
                }
                if (150 - removeingtranslate.getX() <= 1) {
                    fadeState = FadeState.END;
                }
                break;
            }
            case END: {
                LiquidBounce.hud.removeNotification(this);
                break;
            }
        }

    }

    public enum FadeState {IN, STAY, OUT, END}

    public enum Type {error, success, info, state, none}
}

