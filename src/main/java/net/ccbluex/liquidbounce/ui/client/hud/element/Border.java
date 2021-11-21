package net.ccbluex.liquidbounce.ui.client.hud.element;

import net.ccbluex.liquidbounce.utils.render.RenderUtils;

public class Border {

    private float x;
    private float y;
    private float x2;
    private float y2;

    public void draw() {
        RenderUtils.drawBorderedRect(this.x, this.y, this.x2, this.y2, 3.0F, Integer.MIN_VALUE, 0);
    }

    public final float getX() {
        return this.x;
    }

    public final float getY() {
        return this.y;
    }

    public final float getX2() {
        return this.x2;
    }

    public final float getY2() {
        return this.y2;
    }

    public Border(float x, float y, float x2, float y2) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
    }
}
