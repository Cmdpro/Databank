package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.Databank;
import com.cmdpro.databank.hiddenblock.HiddenBlock;
import com.cmdpro.databank.hiddenblock.HiddenBlocksManager;
import com.cmdpro.databank.hiddenblock.HiddenBlocksSerializer;
import com.cmdpro.databank.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public record HiddenBlockSyncS2CPacket(Map<ResourceLocation, HiddenBlock> blocks) implements Message {

    public static HiddenBlockSyncS2CPacket read(FriendlyByteBuf buf) {
        Map<ResourceLocation, HiddenBlock> blocks = buf.readMap(FriendlyByteBuf::readResourceLocation, HiddenBlocksSerializer::fromNetwork);
        for (Map.Entry<ResourceLocation, HiddenBlock> i : blocks.entrySet()) {
            if (i.getValue().condition == null) {
                if (HiddenBlocksManager.blocks.containsKey(i.getKey())) {
                    i.getValue().condition = HiddenBlocksManager.blocks.get(i.getKey()).condition;
                }
            }
        }
        return new HiddenBlockSyncS2CPacket(blocks);
    }
    public static void write(FriendlyByteBuf buf, HiddenBlockSyncS2CPacket obj) {
        buf.writeMap(obj.blocks, FriendlyByteBuf::writeResourceLocation, HiddenBlocksSerializer::toNetwork);
    }
    public static final Type<HiddenBlockSyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "hidden_block_sync"));
    @Override
    public Type<HiddenBlockSyncS2CPacket> type() {
        return TYPE;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        HiddenBlocksManager.blocks.clear();
        HiddenBlocksManager.blocks.putAll(blocks);
        ClientDatabankUtils.updateWorld();
    }
}