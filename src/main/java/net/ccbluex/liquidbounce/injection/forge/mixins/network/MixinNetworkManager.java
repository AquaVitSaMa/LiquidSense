package net.ccbluex.liquidbounce.injection.forge.mixins.network;

import com.google.common.collect.Queues;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventRespawn;
import net.ccbluex.liquidbounce.event.EventType;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.injection.implementations.INetworkManager;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager implements INetworkManager {
    @Shadow
    private Channel channel;

    @Shadow
    private Queue outboundPacketsQueue;

    @Shadow
    protected abstract void dispatchPacket(Packet a, GenericFutureListener[] a2);

    @Shadow
    protected abstract void flushOutboundQueue();

    @Inject(method = "channelRead0", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;processPacket(Lnet/minecraft/network/INetHandler;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void readpacket(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callback) {
        if (this.channel.isOpen()) {
            final PacketEvent event = new PacketEvent(packet, EventType.RECEIVE);
            LiquidBounce.eventManager.callEvent(event);
            if (packet instanceof S07PacketRespawn) {
                final EventRespawn eventRespawn = new EventRespawn();
                LiquidBounce.eventManager.callEvent(eventRespawn);
                if(eventRespawn.isCancelled())
                    callback.cancel();
            }
            if(event.isCancelled())
                callback.cancel();
        }

    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void send(Packet<?> packet, CallbackInfo callback) {
        final PacketEvent event = new PacketEvent(packet, EventType.SEND);
        LiquidBounce.eventManager.callEvent(event);

        if(event.isCancelled())
            callback.cancel();
    }

    public void sendPacketNoEvent(final Packet<?> a) {
        if (this.channel != null && this.channel.isOpen()) {
            final GenericFutureListener[] a2 = null;
            this.flushOutboundQueue();
            this.dispatchPacket(a, a2);
            return;
        }
        this.outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(a, (GenericFutureListener<? extends Future<? super Void>>[])null));
    }

    static class InboundHandlerTuplePacketListener
    {
        private final Packet packet;
        private final GenericFutureListener<? extends Future<? super Void>>[] futureListeners;

        public InboundHandlerTuplePacketListener(final Packet inPacket, final GenericFutureListener<? extends Future<? super Void>>... inFutureListeners) {
            this.packet = inPacket;
            this.futureListeners = inFutureListeners;
        }
    }
}