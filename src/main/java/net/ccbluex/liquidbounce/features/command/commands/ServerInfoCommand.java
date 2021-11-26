package net.ccbluex.liquidbounce.features.command.commands;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Listenable;
import net.ccbluex.liquidbounce.event.events.PacketEvent;
import net.ccbluex.liquidbounce.features.command.Command;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;

public class ServerInfoCommand extends Command implements Listenable
{
    private String ip;
    private int port;

    public ServerInfoCommand() {
        super("serverinfo");
        LiquidBounce.eventManager.registerListener(this);
    }

    @Override
    public void execute(final String[] args) {
        if (mc.getCurrentServerData() == null) {
            this.chat("This command only work on a server.");
            return;
        }
        final ServerData data = mc.getCurrentServerData();
        this.chat("Server infos:");
        this.chat("§7Name: §8" + data.serverName);
        this.chat("§7IP: §8" + this.ip + ':' + this.port);
        this.chat("§7Players: §8" + data.populationInfo);
        this.chat("§7MOTD: §8" + data.serverMOTD);
        this.chat("§7ServerVersion: §8" + data.gameVersion);
        this.chat("§7ProtocolVersion: §8" + data.version);
        this.chat("§7Ping: §8" + data.pingToServer);
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        final Packet packet = event.getPacket();
        if (packet instanceof C00Handshake) {
            final C00Handshake handshake = (C00Handshake)packet;
            this.ip = handshake.ip;
            this.port = handshake.port;
        }
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
