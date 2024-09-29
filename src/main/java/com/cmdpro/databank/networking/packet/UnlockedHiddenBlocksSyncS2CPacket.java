package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.Databank;
import com.cmdpro.databank.hiddenblock.ClientHiddenBlocks;
import com.cmdpro.databank.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record UnlockedHiddenBlocksSyncS2CPacket(List<ResourceLocation> blocks) implements Message {
    public static UnlockedHiddenBlocksSyncS2CPacket read(FriendlyByteBuf buf) {
        List<ResourceLocation> blocks = buf.readList(FriendlyByteBuf::readResourceLocation);
        return new UnlockedHiddenBlocksSyncS2CPacket(blocks);
    }
    public static void write(FriendlyByteBuf buf, UnlockedHiddenBlocksSyncS2CPacket obj) {
        buf.writeCollection(obj.blocks, FriendlyByteBuf::writeResourceLocation);
    }
    public static final Type<UnlockedHiddenBlocksSyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "unlocked_hidden_block_sync"));
    @Override
    public Type<UnlockedHiddenBlocksSyncS2CPacket> type() {
        return TYPE;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientHiddenBlocks.unlocked = blocks;
        ClientDatabankUtils.updateWorld();
    }
}