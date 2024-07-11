package com.cmdpro.cmdlib.networking.packet;

import com.cmdpro.cmdlib.ClientCmdLibUtils;
import com.cmdpro.cmdlib.CmdLib;
import com.cmdpro.cmdlib.hiddenblocks.ClientHiddenBlocks;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.cmdlib.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Supplier;

public record UnlockHiddenBlockSyncS2CPacket(ResourceLocation block) implements Message {
    public static UnlockHiddenBlockSyncS2CPacket read(FriendlyByteBuf buf) {
        ResourceLocation block = buf.readResourceLocation();
        return new UnlockHiddenBlockSyncS2CPacket(block);
    }
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(block);
    }
    public static final ResourceLocation ID = new ResourceLocation(CmdLib.MOD_ID, "unlock_hidden_block_sync");
    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        if (!ClientHiddenBlocks.unlocked.contains(block)) {
            if (HiddenBlocksManager.blocks.containsKey(block)) {
                ClientHiddenBlocks.unlocked.add(block);
            }
        }
        ClientCmdLibUtils.updateWorld();
    }
}