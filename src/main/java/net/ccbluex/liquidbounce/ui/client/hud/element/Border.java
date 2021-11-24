package net.ccbluex.liquidbounce.ui.client.hud.element;

import net.ccbluex.liquidbounce.utils.render.RenderUtils;

public class Border {

	public float x;
	public float y;
	public float x2;
	public float y2;

    public void draw() {
        RenderUtils.drawBorderedRect(this.x, this.y, this.x2, this.y2, 3.0F, Integer.MIN_VALUE, 0);
    }

    public Border(float x, float y, float x2, float y2) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
    }
}
