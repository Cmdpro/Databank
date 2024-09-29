package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.Databank;
import com.cmdpro.databank.hiddenblock.ClientHiddenBlocks;
import com.cmdpro.databank.hiddenblock.HiddenBlocksManager;
import com.cmdpro.databank.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record UnlockHiddenBlockSyncS2CPacket(ResourceLocation block) implements Message {
    public static UnlockHiddenBlockSyncS2CPacket read(FriendlyByteBuf buf) {
        ResourceLocation block = buf.readResourceLocation();
        return new UnlockHiddenBlockSyncS2CPacket(block);
    }
    public static void write(FriendlyByteBuf buf, UnlockHiddenBlockSyncS2CPacket obj) {
        buf.writeResourceLocation(obj.block);
    }
    public static final Type<UnlockHiddenBlockSyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "unlock_hidden_block_sync"));
    @Override
    public Type<UnlockHiddenBlockSyncS2CPacket> type() {
        return TYPE;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        if (!ClientHiddenBlocks.unlocked.contains(block)) {
            if (HiddenBlocksManager.blocks.containsKey(block)) {
                ClientHiddenBlocks.unlocked.add(block);
            }
        }
        ClientDatabankUtils.updateWorld();
    }
}