package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventLivingUpdate;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.features.module.modules.combat.Aura;
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner;
import net.ccbluex.liquidbounce.ui.client.hud.element.Border;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo;
import net.ccbluex.liquidbounce.ui.client.hud.element.Side;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@ElementInfo(name = "Print")
public class Prints extends Element {

    private final Print exampleNotification = new Print("Example Print", 0.0F, Print.Type.info);

    public Prints() {
        super(0, 30, 1f, new Side(Side.Horizontal.LEFT, Side.Vertical.UP));
    }

    public Stream<Print> print;

    @Nullable
    @Override
    public Border drawElement() {
        print = LiquidBounce.hud.getPrints().stream();
        int index = 0;
        for(Print print : print.collect(Collectors.toList())) {
            if (!print.removeing) {
                print.index = index;
                print.translate.translate(0f, (LiquidBounce.hud.getPrints().size() * 11) - (index * 11), 1.5);
            }
            print.y = print.translate.getY();
            print.drawPrint();
            if(print.fadeState == Print.FadeState.END) {
                LiquidBounce.hud.removePrint(print);
                index--;
            }
            index++;
        }
        if (mc.currentScreen instanceof GuiHudDesigner) {
            if (!LiquidBounce.INSTANCE.getHud().getPrints().contains(exampleNotification)) {
                LiquidBounce.hud.addPrint(exampleNotification);
            }
            exampleNotification.fadeState = Print.FadeState.STAY;
            exampleNotification.x = ((float) this.exampleNotification.textLength + 8.0F);
            return new Border(-exampleNotification.x + 12 + exampleNotification.textLength, -29, -exampleNotification.x - 35, 11F * LiquidBounce.INSTANCE.getHud().getNotifications().size());
        }
        return new Border(0f , 0f , 0f , 0f);
    }
}

