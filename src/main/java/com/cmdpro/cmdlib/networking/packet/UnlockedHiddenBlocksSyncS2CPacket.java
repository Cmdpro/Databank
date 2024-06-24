package com.cmdpro.cmdlib.networking.packet;

import com.cmdpro.cmdlib.ClientCmdLibUtils;
import com.cmdpro.cmdlib.hiddenblocks.ClientHiddenBlocks;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlock;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlocksSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class UnlockedHiddenBlocksSyncS2CPacket {
    private final List<ResourceLocation> blocks;

    public UnlockedHiddenBlocksSyncS2CPacket(List<ResourceLocation> blocks) {
        this.blocks = blocks;
    }

    public UnlockedHiddenBlocksSyncS2CPacket(FriendlyByteBuf buf) {
        this.blocks = buf.readList(FriendlyByteBuf::readResourceLocation);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeCollection(blocks, FriendlyByteBuf::writeResourceLocation);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientPacketHandler.handlePacket(this, supplier);
        });
        return true;
    }
    public static class ClientPacketHandler {
        public static void handlePacket(UnlockedHiddenBlocksSyncS2CPacket msg, Supplier<NetworkEvent.Context> supplier) {
            ClientHiddenBlocks.unlocked = msg.blocks;
            ClientCmdLibUtils.updateWorld();
        }
    }
}