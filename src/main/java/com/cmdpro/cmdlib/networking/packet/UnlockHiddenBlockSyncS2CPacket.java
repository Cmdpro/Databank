package com.cmdpro.cmdlib.networking.packet;

import com.cmdpro.cmdlib.ClientCmdLibUtils;
import com.cmdpro.cmdlib.hiddenblocks.ClientHiddenBlocks;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlocksManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class UnlockHiddenBlockSyncS2CPacket {
    private final ResourceLocation block;

    public UnlockHiddenBlockSyncS2CPacket(ResourceLocation block) {
        this.block = block;
    }

    public UnlockHiddenBlockSyncS2CPacket(FriendlyByteBuf buf) {
        this.block = buf.readResourceLocation();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(block);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientPacketHandler.handlePacket(this, supplier);
        });
        return true;
    }
    public static class ClientPacketHandler {
        public static void handlePacket(UnlockHiddenBlockSyncS2CPacket msg, Supplier<NetworkEvent.Context> supplier) {
            if (!ClientHiddenBlocks.unlocked.contains(msg.block)) {
                if (HiddenBlocksManager.blocks.containsKey(msg.block)) {
                    ClientHiddenBlocks.unlocked.add(msg.block);
                }
            }
            ClientCmdLibUtils.updateWorld();
        }
    }
}