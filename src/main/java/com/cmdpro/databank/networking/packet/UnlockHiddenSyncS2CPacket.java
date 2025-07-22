package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.hidden.ClientHidden;
import com.cmdpro.databank.hidden.Hidden;
import com.cmdpro.databank.hidden.ClientHiddenListener;
import com.cmdpro.databank.hidden.HiddenManager;
import com.cmdpro.databank.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

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
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext context) {
        if (!ClientHidden.unlocked.contains(hidden)) {
            if (HiddenManager.hidden.containsKey(hidden)) {
                ClientHidden.unlocked.add(hidden);
                Hidden hidden2 = HiddenManager.hidden.get(hidden);
                ClientHiddenListener.HIDDEN_LISTENERS.forEach((listener) -> listener.onUnhide(hidden2));
                ClientHiddenListener.HIDDEN_LISTENERS.forEach((listener) -> listener.onUnhide(List.of(hidden2)));
            }
        }
        if (HiddenManager.hidden.containsKey(hidden)) {
            HiddenManager.hidden.get(hidden).type.getType().updateClient();
        }
    }
}