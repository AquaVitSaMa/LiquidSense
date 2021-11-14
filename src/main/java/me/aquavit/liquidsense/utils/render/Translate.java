package me.aquavit.liquidsense.utils.render;

public final class Translate {

	private float x;
	private float y;

	public Translate(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void translate(float targetX, float targetY, double speed) {
		x = AnimationUtils.lstransition(targetX, targetY, speed);
		y = AnimationUtils.lstransition(targetX, targetY, speed);
	}

	public void translate(float targetX, float targetY) {
		x = AnimationUtils.lstransition(targetX, targetY, 0.0);
		y = AnimationUtils.lstransition(targetX, targetY, 0.0);
	}

	public float getX() {
		return this.x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return this.y;
	}

	public void setY(float y) {
		this.y = y;
	}
}
