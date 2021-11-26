package net.ccbluex.liquidbounce.event.events;

import net.ccbluex.liquidbounce.event.Event;

public class StepConfirmEvent extends Event {
    private float stepHeight;

    public float getStepHeight() {
        return this.stepHeight;
    }

    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public StepConfirmEvent(float stepHeight) {
        this.stepHeight = stepHeight;
    }
}
