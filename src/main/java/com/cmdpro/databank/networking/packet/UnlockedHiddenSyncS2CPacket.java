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
        if (updateListeners) {
            List<Hidden> unlocked = hidden.stream().filter((i) -> !ClientHidden.unlocked.contains(i)).map((i) -> HiddenManager.hidden.get(i)).toList();
            List<Hidden> locked = ClientHidden.unlocked.stream().filter((i) -> !hidden.contains(i)).map((i) -> HiddenManager.hidden.get(i)).toList();
            for (Hidden i : locked) {
                ClientHiddenListener.HIDDEN_LISTENERS.forEach((listener) -> listener.onHide(i));
            }
            ClientHiddenListener.HIDDEN_LISTENERS.forEach((listener) -> listener.onHide(locked));
            for (Hidden i : unlocked) {
                ClientHiddenListener.HIDDEN_LISTENERS.forEach((listener) -> listener.onUnhide(i));
            }
            ClientHiddenListener.HIDDEN_LISTENERS.forEach((listener) -> listener.onUnhide(unlocked));
        }
        ClientHidden.unlocked = hidden;
        for (HiddenTypeInstance.HiddenType<?> i : DatabankRegistries.HIDDEN_TYPE_REGISTRY.stream().toList()) {
            i.updateClient();
        }
    }
}