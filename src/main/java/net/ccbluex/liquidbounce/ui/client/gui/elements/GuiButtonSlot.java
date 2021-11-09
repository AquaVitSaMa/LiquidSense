package net.ccbluex.liquidbounce.ui.client.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;

public class GuiButtonSlot extends GuiSlot {

    public GuiButtonSlot(Minecraft mc, final int width, final int height, final int top, final int bottom, int slotHeight) {
        super(mc,width,height,top,bottom,slotHeight);
    }

    protected int getSize() {
        return 0;
    }

    protected void elementClicked(int i, boolean b, int i1, int i2) {
    }

    protected boolean isSelected(int i) {
        return false;
    }

    protected void drawBackground() {
    }

    protected void drawSlot(int i, int i1, int i2, int i3, int i4, int i5) {
    }
}
