package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoveDimensionS2CPacket(ResourceKey<Level> key) implements Message {

    public static RemoveDimensionS2CPacket read(FriendlyByteBuf buf) {
        ResourceKey<Level> key = buf.readResourceKey(Registries.DIMENSION);
        return new RemoveDimensionS2CPacket(key);
    }
    public static void write(FriendlyByteBuf buf, RemoveDimensionS2CPacket obj) {
        buf.writeResourceKey(obj.key);
    }
    public static final Type<RemoveDimensionS2CPacket> TYPE = new Type<>(Databank.locate("remove_dimension"));
    @Override
    public Type<RemoveDimensionS2CPacket> type() {
        return TYPE;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext context) {
        ClientHandler.removeDimension(key);
    }
    private static class ClientHandler {
        public static void removeDimension(ResourceKey<Level> key) {
            Minecraft.getInstance().player.connection.levels().remove(key);
        }
    }
}