package net.ccbluex.liquidbounce.event.events;

import net.ccbluex.liquidbounce.event.CancellableEvent;
import net.ccbluex.liquidbounce.event.EventType;
import net.minecraft.network.Packet;

public class PacketEvent extends CancellableEvent {
    private Packet<?> packet;
    private EventType eventType;

    public Packet<?> getPacket() {
        return this.packet;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public PacketEvent(Packet<?> packet, EventType eventType) {
        this.packet = packet;
        this.eventType = eventType;
    }
}
