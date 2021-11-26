package net.ccbluex.liquidbounce.event.events;

import net.ccbluex.liquidbounce.event.CancellableEvent;

public class JumpEvent extends CancellableEvent {
    private float motion;

    public JumpEvent(float motion) {
        this.motion = motion;
    }

    public void setMotion(float motion) {
        this.motion = motion;
    }

    public float getMotion() {
        return this.motion;
    }
}
