package net.ccbluex.liquidbounce.event.events;

import net.ccbluex.liquidbounce.event.Event;

public class StepEvent extends Event {
    private float stepHeight;

    public StepEvent(float stepHeight) {
        this.setStepHeight(stepHeight);
    }

    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public float getStepHeight() {
        return this.stepHeight;
    }
}
