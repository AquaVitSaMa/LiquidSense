package net.ccbluex.liquidbounce.event.events;

import net.ccbluex.liquidbounce.event.Event;
import net.minecraft.client.gui.GuiScreen;

public class ScreenEvent extends Event {
    private GuiScreen guiScreen;

    public ScreenEvent(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }

    public GuiScreen getGuiScreen() {
        return this.guiScreen;
    }
}
