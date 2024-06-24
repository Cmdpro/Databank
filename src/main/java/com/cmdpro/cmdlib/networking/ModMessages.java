package com.cmdpro.cmdlib.networking;

import com.cmdpro.cmdlib.CmdLib;
import com.cmdpro.cmdlib.networking.packet.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {

    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }
    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(CmdLib.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(HiddenBlockSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(HiddenBlockSyncS2CPacket::new)
                .encoder(HiddenBlockSyncS2CPacket::toBytes)
                .consumerMainThread(HiddenBlockSyncS2CPacket::handle)
                .add();
        net.messageBuilder(UnlockedHiddenBlocksSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(UnlockedHiddenBlocksSyncS2CPacket::new)
                .encoder(UnlockedHiddenBlocksSyncS2CPacket::toBytes)
                .consumerMainThread(UnlockedHiddenBlocksSyncS2CPacket::handle)
                .add();
        net.messageBuilder(UnlockHiddenBlockSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(UnlockHiddenBlockSyncS2CPacket::new)
                .encoder(UnlockHiddenBlockSyncS2CPacket::toBytes)
                .consumerMainThread(UnlockHiddenBlockSyncS2CPacket::handle)
                .add();

    }
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
