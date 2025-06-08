package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.hidden.ClientHidden;
import com.cmdpro.databank.hidden.HiddenTypeInstance;
import com.cmdpro.databank.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record UnlockedHiddenSyncS2CPacket(List<ResourceLocation> hidden) implements Message {
    public static UnlockedHiddenSyncS2CPacket read(FriendlyByteBuf buf) {
        List<ResourceLocation> blocks = buf.readList(FriendlyByteBuf::readResourceLocation);
        return new UnlockedHiddenSyncS2CPacket(blocks);
    }
    public static void write(FriendlyByteBuf buf, UnlockedHiddenSyncS2CPacket obj) {
        buf.writeCollection(obj.hidden, FriendlyByteBuf::writeResourceLocation);
    }
    public static final Type<UnlockedHiddenSyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "unlocked_hidden_block_sync"));
    @Override
    public Type<UnlockedHiddenSyncS2CPacket> type() {
        return TYPE;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientHidden.unlocked = hidden;
        for (HiddenTypeInstance.HiddenType<?> i : DatabankRegistries.HIDDEN_TYPE_REGISTRY.stream().toList()) {
            i.updateClient();
        }
    }
}