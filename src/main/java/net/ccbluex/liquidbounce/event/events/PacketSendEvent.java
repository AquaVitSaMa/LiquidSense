package net.ccbluex.liquidbounce.event.events;

import net.ccbluex.liquidbounce.event.CancellableEvent;
import net.minecraft.network.Packet;

public class PacketSendEvent extends CancellableEvent {
    private Packet<?> packet;

    public Packet<?> getPacket() {
        return this.packet;
    }

    public PacketSendEvent(Packet<?> packet) {
        this.packet = packet;
    }
}
