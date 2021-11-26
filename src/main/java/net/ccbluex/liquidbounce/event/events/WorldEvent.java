package net.ccbluex.liquidbounce.event.events;

import net.ccbluex.liquidbounce.event.Event;
import net.minecraft.client.multiplayer.WorldClient;

public class WorldEvent extends Event {
    private WorldClient worldClient;

    public WorldEvent(WorldClient worldClient) {
        this.worldClient = worldClient;
    }

    public WorldClient getWorldClient() {
        return this.worldClient;
    }
}
