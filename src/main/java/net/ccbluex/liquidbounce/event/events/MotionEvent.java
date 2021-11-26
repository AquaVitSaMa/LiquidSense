package net.ccbluex.liquidbounce.event.events;

import net.ccbluex.liquidbounce.event.Event;
import net.ccbluex.liquidbounce.event.EventState;

public class MotionEvent extends Event {
    private EventState eventState;

    public MotionEvent(EventState eventState) {
        this.eventState = eventState;
    }

    public EventState getEventState() {
        return this.eventState;
    }
}
