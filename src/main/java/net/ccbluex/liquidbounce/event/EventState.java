package net.ccbluex.liquidbounce.event;

public enum EventState {
    PRE("PRE"),
    POST("POST");

    private String stateName;

    EventState(final String stateName) {
        this.stateName = stateName;
    }

    public String getStateName() {
        return this.stateName;
    }
}
