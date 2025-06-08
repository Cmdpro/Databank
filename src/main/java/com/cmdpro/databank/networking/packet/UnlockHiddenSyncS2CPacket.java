package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.hidden.ClientHidden;
import com.cmdpro.databank.hidden.HiddenManager;
import com.cmdpro.databank.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record UnlockHiddenSyncS2CPacket(ResourceLocation hidden) implements Message {
    public static UnlockHiddenSyncS2CPacket read(FriendlyByteBuf buf) {
        ResourceLocation block = buf.readResourceLocation();
        return new UnlockHiddenSyncS2CPacket(block);
    }
    public static void write(FriendlyByteBuf buf, UnlockHiddenSyncS2CPacket obj) {
        buf.writeResourceLocation(obj.hidden);
    }
    public static final Type<UnlockHiddenSyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "unlock_hidden_block_sync"));
    @Override
    public Type<UnlockHiddenSyncS2CPacket> type() {
        return TYPE;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        if (!ClientHidden.unlocked.contains(hidden)) {
            if (HiddenManager.hidden.containsKey(hidden)) {
                ClientHidden.unlocked.add(hidden);
            }
        }
        if (HiddenManager.hidden.containsKey(hidden)) {
            HiddenManager.hidden.get(hidden).type.getType().updateClient();
        }
    }
}