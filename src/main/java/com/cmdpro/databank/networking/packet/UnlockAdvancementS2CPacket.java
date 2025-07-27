package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.advancement.ClientAdvancementListener;
import com.cmdpro.databank.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record UnlockAdvancementS2CPacket(ResourceLocation advancement) implements Message {
    public static UnlockAdvancementS2CPacket read(FriendlyByteBuf buf) {
        ResourceLocation advancement = buf.readResourceLocation();
        return new UnlockAdvancementS2CPacket(advancement);
    }
    public static void write(FriendlyByteBuf buf, UnlockAdvancementS2CPacket obj) {
        buf.writeResourceLocation(obj.advancement);
    }
    public static final Type<UnlockAdvancementS2CPacket> TYPE = new Type<>(Databank.locate("unlock_advancement"));
    @Override
    public Type<UnlockAdvancementS2CPacket> type() {
        return TYPE;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext context) {
        ClientAdvancementListener.ADVANCEMENT_LISTENERS.forEach((listener) -> listener.onUnlock(advancement));
        ClientAdvancementListener.ADVANCEMENT_LISTENERS.forEach((listener) -> listener.onUnlock(List.of(advancement)));
    }
}