package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.dialogue.DialogueScreen;
import com.cmdpro.databank.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AddDimensionS2CPacket(ResourceKey<Level> key) implements Message {

    public static AddDimensionS2CPacket read(FriendlyByteBuf buf) {
        ResourceKey<Level> key = buf.readResourceKey(Registries.DIMENSION);
        return new AddDimensionS2CPacket(key);
    }
    public static void write(FriendlyByteBuf buf, AddDimensionS2CPacket obj) {
        buf.writeResourceKey(obj.key);
    }
    public static final Type<AddDimensionS2CPacket> TYPE = new Type<>(Databank.locate("add_dimension"));
    @Override
    public Type<AddDimensionS2CPacket> type() {
        return TYPE;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext context) {
        ClientHandler.addDimension(key);
    }
    private static class ClientHandler {
        public static void addDimension(ResourceKey<Level> key) {
            Minecraft.getInstance().player.connection.levels().add(key);
        }
    }
}