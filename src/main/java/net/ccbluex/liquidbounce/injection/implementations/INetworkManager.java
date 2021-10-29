package net.ccbluex.liquidbounce.injection.implementations;

import net.minecraft.network.Packet;

public interface INetworkManager {
    void sendPacketNoEvent(Packet<?> p);
}
