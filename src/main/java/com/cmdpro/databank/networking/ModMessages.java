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
        registrar.playToClient(UnlockedHiddenSyncS2CPacket.TYPE, getNetworkCodec(UnlockedHiddenSyncS2CPacket::read, UnlockedHiddenSyncS2CPacket::write), Handler::handle);
        registrar.playToClient(UnlockHiddenSyncS2CPacket.TYPE, getNetworkCodec(UnlockHiddenSyncS2CPacket::read, UnlockHiddenSyncS2CPacket::write), Handler::handle);
        registrar.playToClient(HiddenSyncS2CPacket.TYPE, getNetworkCodec(HiddenSyncS2CPacket::read, HiddenSyncS2CPacket::write), Handler::handle);
        registrar.playToClient(MultiblockSyncS2CPacket.TYPE, getNetworkCodec(MultiblockSyncS2CPacket::read, MultiblockSyncS2CPacket::write), Handler::handle);

        //C2S
        registrar.playToServer(WorldGuiInteractC2SPacket.TYPE, getNetworkCodec(WorldGuiInteractC2SPacket::read, WorldGuiInteractC2SPacket::write), Handler::handle);
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
