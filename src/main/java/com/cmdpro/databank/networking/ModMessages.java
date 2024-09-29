package com.cmdpro.databank.networking;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.networking.packet.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Databank.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModMessages {
    public class Handler {
        public static <T extends CustomPacketPayload> void handle(T message, IPayloadContext ctx) {
            if (message instanceof Message msg) {
                if (ctx.flow().getReceptionSide() == LogicalSide.SERVER) {
                    ctx.enqueueWork(() -> {
                        Server.handle(msg, ctx);
                    });
                } else {
                    ctx.enqueueWork(() -> {
                        Client.handle(msg, ctx);
                    });
                }
            }
        }
        public class Client {
            public static <T extends Message> void handle(T message, IPayloadContext ctx) {
                message.handleClient(Minecraft.getInstance(), Minecraft.getInstance().player);
            }
        }
        public class Server {
            public static <T extends Message> void handle(T message, IPayloadContext ctx) {
                message.handleServer(ctx.player().getServer(), (ServerPlayer)ctx.player());
            }
        }
        public abstract interface Reader<T extends Message> {
            public abstract T read(RegistryFriendlyByteBuf buf);
        }
        public abstract interface Writer<T extends Message> {
            public abstract void write(RegistryFriendlyByteBuf buf, T message);
        }
    }
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Databank.MOD_ID)
                .versioned("1.0");

        //S2C
        registrar.playToClient(UnlockedHiddenBlocksSyncS2CPacket.TYPE, getNetworkCodec(UnlockedHiddenBlocksSyncS2CPacket::read, UnlockedHiddenBlocksSyncS2CPacket::write), Handler::handle);
        registrar.playToClient(UnlockHiddenBlockSyncS2CPacket.TYPE, getNetworkCodec(UnlockHiddenBlockSyncS2CPacket::read, UnlockHiddenBlockSyncS2CPacket::write), Handler::handle);
        registrar.playToClient(HiddenBlockSyncS2CPacket.TYPE, getNetworkCodec(HiddenBlockSyncS2CPacket::read, HiddenBlockSyncS2CPacket::write), Handler::handle);
    }

    public static <T extends Message> StreamCodec<RegistryFriendlyByteBuf, T> getNetworkCodec(Handler.Reader<T> reader, Handler.Writer<T> writer) {
        return StreamCodec.of(writer::write, reader::read);
    }

    public static <T extends Message> void sendToServer(T message) {
        PacketDistributor.sendToServer(message);
    }

    public static <T extends Message> void sendToPlayer(T message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }
    public static <T extends Message> void sendToPlayersTrackingEntityAndSelf(T message, ServerPlayer player) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, message);
    }
}
