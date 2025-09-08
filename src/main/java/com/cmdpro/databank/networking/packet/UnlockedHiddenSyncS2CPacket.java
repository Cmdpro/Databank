package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.hidden.*;
import com.cmdpro.databank.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public record UnlockedHiddenSyncS2CPacket(List<ResourceLocation> hidden, boolean updateListeners) implements Message {
    public static UnlockedHiddenSyncS2CPacket read(FriendlyByteBuf buf) {
        List<ResourceLocation> blocks = buf.readList(FriendlyByteBuf::readResourceLocation);
        boolean updateListeners = buf.readBoolean();
        return new UnlockedHiddenSyncS2CPacket(blocks, updateListeners);
    }
    public static void write(FriendlyByteBuf buf, UnlockedHiddenSyncS2CPacket obj) {
        buf.writeCollection(obj.hidden, FriendlyByteBuf::writeResourceLocation);
        buf.writeBoolean(obj.updateListeners);
    }
    public static final Type<UnlockedHiddenSyncS2CPacket> TYPE = new Type<>(Databank.locate("unlocked_hidden_block_sync"));
    @Override
    public Type<UnlockedHiddenSyncS2CPacket> type() {
        return TYPE;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext context) {
        if (updateListeners && !ClientHiddenListener.HIDDEN_LISTENERS.isEmpty()) {
            // We need to make a copy of the list to do our destructive comparisons below.
            List<ResourceLocation> unlockedIds = new ArrayList<>(hidden);
            List<ResourceLocation> lockedIds = new ArrayList<>(ClientHidden.unlocked);

            // Any entries in locked should be removed from both sets, leaving us with one set
            // containing newly unhidden items and old items that are no longer unhidden.
            unlockedIds.removeIf(lockedIds::remove);

            // Make sure we still have work to do before doing more work.
            if (!lockedIds.isEmpty() || !unlockedIds.isEmpty()) {
                List<Hidden> unlockedList = new ArrayList<>(unlockedIds.size());
                List<Hidden> lockedList = new ArrayList<>(lockedIds.size());

                for (ResourceLocation id : lockedIds) {
                    Hidden i = HiddenManager.hidden.get(id);
                    if (i == null)
                        continue;
                    lockedList.add(i);
                    ClientHiddenListener.HIDDEN_LISTENERS.forEach(listener -> listener.onHide(i));
                }

                ClientHiddenListener.HIDDEN_LISTENERS.forEach(listener -> listener.onHide(lockedList));

                for (ResourceLocation id : unlockedIds) {
                    Hidden i = HiddenManager.hidden.get(id);
                    if (i == null)
                        continue;
                    unlockedList.add(i);
                    ClientHiddenListener.HIDDEN_LISTENERS.forEach(listener -> listener.onUnhide(i));
                }

                ClientHiddenListener.HIDDEN_LISTENERS.forEach(listener -> listener.onUnhide(unlockedList));
            }
        }

        ClientHidden.unlocked = hidden;
        for (HiddenTypeInstance.HiddenType<?> i : DatabankRegistries.HIDDEN_TYPE_REGISTRY) {
            i.updateClient();
        }
    }
}