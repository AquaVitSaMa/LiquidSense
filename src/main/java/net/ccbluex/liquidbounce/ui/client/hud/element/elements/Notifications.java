package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import me.AquaVit.liquidSense.utils.render.Translate;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner;
import net.ccbluex.liquidbounce.ui.client.hud.element.Border;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo;
import net.ccbluex.liquidbounce.ui.client.hud.element.Side;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ElementInfo(name = "Notifications")
public class Notifications extends Element {

    private final Notification exampleNotification = new Notification("Example Notification", 0.0F, Notification.Type.info);

    public Notifications() {
        super(0, 30, 1f, new Side(Side.Horizontal.LEFT, Side.Vertical.UP));
    }

    public Stream<Notification> notification;

    @Nullable
    @Override
    public Border drawElement() {
        notification = LiquidBounce.hud.getNotifications().stream();
        int index = 0;
        for(Notification notification : notification.collect(Collectors.toList())) {
            if (!notification.removeing) {
                notification.translate.translate(0f, 11f, 1.0);
            }
            notification.y = (11.0F * index  - 11) + notification.translate.getY();
            notification.drawNotification();
            index++;
        }
        if (mc.currentScreen instanceof GuiHudDesigner) {
            if (!LiquidBounce.INSTANCE.getHud().getNotifications().contains(exampleNotification)) {
                for (int i = 0; i < LiquidBounce.INSTANCE.getHud().getNotifications().size(); i++) {
                    LiquidBounce.INSTANCE.getHud().removeNotification(LiquidBounce.INSTANCE.getHud().getNotifications().get(0));
                }
                LiquidBounce.hud.addNotification(exampleNotification);
            }
            exampleNotification.fadeState = Notification.FadeState.STAY;
            exampleNotification.x = ((float) this.exampleNotification.textLength + 8.0F);
            return new Border(-exampleNotification.x + 12 + exampleNotification.textLength, -29F, -exampleNotification.x - 35, 2F);
        }
        return new Border(0f , 0f , 0f , 0f);
    }
}

