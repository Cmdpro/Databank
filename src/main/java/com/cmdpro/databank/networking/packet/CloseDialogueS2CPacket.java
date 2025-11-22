package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.dialogue.DialogueScreen;
import com.cmdpro.databank.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CloseDialogueS2CPacket() implements Message {

    public static CloseDialogueS2CPacket read(FriendlyByteBuf buf) {
        return new CloseDialogueS2CPacket();
    }
    public static void write(FriendlyByteBuf buf, CloseDialogueS2CPacket obj) {
    }
    public static final Type<CloseDialogueS2CPacket> TYPE = new Type<>(Databank.locate("close_dialogue_s2c"));
    @Override
    public Type<CloseDialogueS2CPacket> type() {
        return TYPE;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext context) {
        ClientHandler.close();
    }
    private static class ClientHandler {
        public static void close() {
            if (Minecraft.getInstance().screen instanceof DialogueScreen dialogueScreen) {
                dialogueScreen.onClose();
            }
        }
    }
}