package com.cmdpro.cmdlib.networking.packet;

import com.cmdpro.cmdlib.ClientCmdLibUtils;
import com.cmdpro.cmdlib.CmdLib;
import com.cmdpro.cmdlib.hiddenblocks.ClientHiddenBlocks;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlock;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlocksSerializer;
import com.cmdpro.cmdlib.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record UnlockedHiddenBlocksSyncS2CPacket(List<ResourceLocation> blocks) implements Message {
    public static UnlockedHiddenBlocksSyncS2CPacket read(FriendlyByteBuf buf) {
        List<ResourceLocation> blocks = buf.readList(FriendlyByteBuf::readResourceLocation);
        return new UnlockedHiddenBlocksSyncS2CPacket(blocks);
    }
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(blocks, FriendlyByteBuf::writeResourceLocation);
    }
    public static final ResourceLocation ID = new ResourceLocation(CmdLib.MOD_ID, "unlocked_hidden_block_sync");
    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        ClientHiddenBlocks.unlocked = blocks;
        ClientCmdLibUtils.updateWorld();
    }
}