package me.AquaVit.liquidSense.utils.render;

public final class Translate {

    private float x;
    private float y;

    public Translate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void interpolate(float targetX, float targetY, double smoothing) {
        x = (float) AnimationUtils.animate(targetX, this.x, smoothing);
        y = (float) AnimationUtils.animate(targetY, this.y, smoothing);
    }

    public void translate(float targetX, float targetY) {
        x = (float) AnimationUtils.Anim(x, targetX, 1.0);
        y = (float) AnimationUtils.Anim(y, targetY, 1.0);
    }

    public void translate(float targetX, float targetY , double speed) {
        x = (float) AnimationUtils.Anim(x, targetX, speed);
        y = (float) AnimationUtils.Anim(y, targetY, speed);
    }

    public void translate(float targetX, float targetY , double xspeed , double yspeed) {
        x = (float) AnimationUtils.Anim(x, targetX, xspeed);
        y = (float) AnimationUtils.Anim(y, targetY, yspeed);
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
