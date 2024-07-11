package com.cmdpro.cmdlib.networking;

import com.cmdpro.cmdlib.CmdLib;
import com.cmdpro.cmdlib.networking.packet.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

@Mod.EventBusSubscriber(modid = CmdLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModMessages {
    public class Handler {
        public static <T extends CustomPacketPayload> void handle(T message, IPayloadContext ctx) {
            if (message instanceof Message msg) {
                if (ctx.flow().getReceptionSide() == LogicalSide.SERVER) {
                    ctx.workHandler().submitAsync(() -> {
                        Server.handle(msg, ctx);
                    });
                } else {
                    ctx.workHandler().submitAsync(() -> {
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
                message.handleServer(ctx.level().get().getServer(), (ServerPlayer)ctx.player().get());
            }
        }
    }
    @SubscribeEvent
    public static void register(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(CmdLib.MOD_ID)
                .versioned("1.0");

        //S2C
        registrar.play(UnlockedHiddenBlocksSyncS2CPacket.ID, UnlockedHiddenBlocksSyncS2CPacket::read, Handler::handle);
        registrar.play(UnlockHiddenBlockSyncS2CPacket.ID, UnlockHiddenBlockSyncS2CPacket::read, Handler::handle);
        //S2C Config
        registrar.configuration(HiddenBlockSyncS2CPacket.ID, HiddenBlockSyncS2CPacket::read, Handler::handle);
    }
    public static <T extends Message> void sendToServer(T message) {
        PacketDistributor.SERVER.noArg().send(message);
    }

    public static <T extends Message> void sendToPlayer(T message, ServerPlayer player) {
        PacketDistributor.PLAYER.with(player).send(message);
    }
}
