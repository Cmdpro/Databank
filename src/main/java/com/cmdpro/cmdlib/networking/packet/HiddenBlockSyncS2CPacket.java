package com.cmdpro.cmdlib.networking.packet;

import com.cmdpro.cmdlib.ClientCmdLibUtils;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlock;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlocksSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class HiddenBlockSyncS2CPacket {
    private final Map<ResourceLocation, HiddenBlock> blocks;

    public HiddenBlockSyncS2CPacket(Map<ResourceLocation, HiddenBlock> blocks) {
        this.blocks = blocks;
    }

    public HiddenBlockSyncS2CPacket(FriendlyByteBuf buf) {
        this.blocks = buf.readMap(FriendlyByteBuf::readResourceLocation, HiddenBlocksSerializer::fromNetwork);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeMap(blocks, FriendlyByteBuf::writeResourceLocation, HiddenBlocksSerializer::toNetwork);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ClientPacketHandler.handlePacket(this, supplier);
        });
        return true;
    }
    public static class ClientPacketHandler {
        public static void handlePacket(HiddenBlockSyncS2CPacket msg, Supplier<NetworkEvent.Context> supplier) {
            HiddenBlocksManager.blocks.clear();
            for (Map.Entry<ResourceLocation, HiddenBlock> i : msg.blocks.entrySet()) {
                HiddenBlocksManager.blocks.put(i.getKey(), i.getValue());
            }
            ClientCmdLibUtils.updateWorld();
        }
    }
}