package com.cmdpro.cmdlib.networking.packet;

import com.cmdpro.cmdlib.ClientCmdLibUtils;
import com.cmdpro.cmdlib.CmdLib;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlock;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlocksSerializer;
import com.cmdpro.cmdlib.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.function.Supplier;

public record HiddenBlockSyncS2CPacket(Map<ResourceLocation, HiddenBlock> blocks) implements Message {

    public static HiddenBlockSyncS2CPacket read(FriendlyByteBuf buf) {
        Map<ResourceLocation, HiddenBlock> blocks = buf.readMap(FriendlyByteBuf::readResourceLocation, HiddenBlocksSerializer::fromNetwork);
        return new HiddenBlockSyncS2CPacket(blocks);
    }
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(blocks, FriendlyByteBuf::writeResourceLocation, HiddenBlocksSerializer::toNetwork);
    }
    public static final ResourceLocation ID = new ResourceLocation(CmdLib.MOD_ID, "hidden_block_sync");
    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        HiddenBlocksManager.blocks.clear();
        for (Map.Entry<ResourceLocation, HiddenBlock> i : blocks.entrySet()) {
            HiddenBlocksManager.blocks.put(i.getKey(), i.getValue());
        }
        ClientCmdLibUtils.updateWorld();
    }
}