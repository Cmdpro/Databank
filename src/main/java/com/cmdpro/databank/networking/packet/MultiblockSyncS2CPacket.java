package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.multiblock.Multiblock;
import com.cmdpro.databank.multiblock.MultiblockManager;
import com.cmdpro.databank.multiblock.MultiblockSerializer;
import com.cmdpro.databank.networking.Message;
import com.mojang.datafixers.types.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public record MultiblockSyncS2CPacket(Map<ResourceLocation, Multiblock> multiblocks) implements Message {

    public static void write(RegistryFriendlyByteBuf buf, MultiblockSyncS2CPacket obj) {
        buf.writeMap(obj.multiblocks, ResourceLocation.STREAM_CODEC, (pBuffer, pValue) -> MultiblockSerializer.STREAM_CODEC.encode((RegistryFriendlyByteBuf)pBuffer, pValue));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<MultiblockSyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "multiblock_sync"));

    public static MultiblockSyncS2CPacket read(RegistryFriendlyByteBuf buf) {
        Map<ResourceLocation, Multiblock> multiblocks = buf.readMap(ResourceLocation.STREAM_CODEC, (pBuffer) -> MultiblockSerializer.STREAM_CODEC.decode((RegistryFriendlyByteBuf)pBuffer));
        return new MultiblockSyncS2CPacket(multiblocks);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext context) {
        context.enqueueWork(() -> {
            MultiblockManager.multiblocks.clear();
            MultiblockManager.multiblocks.putAll(multiblocks);
        });
    }
}