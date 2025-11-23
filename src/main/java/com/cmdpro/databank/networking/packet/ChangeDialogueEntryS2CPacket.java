package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.dialogue.DialogueInstance;
import com.cmdpro.databank.dialogue.DialogueScreen;
import com.cmdpro.databank.dialogue.DialogueTree;
import com.cmdpro.databank.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ChangeDialogueEntryS2CPacket(String entry) implements Message {

    public static ChangeDialogueEntryS2CPacket read(FriendlyByteBuf buf) {
        String entry = buf.readUtf();
        return new ChangeDialogueEntryS2CPacket(entry);
    }
    public static void write(FriendlyByteBuf buf, ChangeDialogueEntryS2CPacket obj) {
        buf.writeUtf(obj.entry);
    }
    public static final Type<ChangeDialogueEntryS2CPacket> TYPE = new Type<>(Databank.locate("change_dialogue_entry"));
    @Override
    public Type<ChangeDialogueEntryS2CPacket> type() {
        return TYPE;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext context) {
        ClientHandler.changeEntry(entry);
    }
    private static class ClientHandler {
        public static void changeEntry(String entry) {
            if (Minecraft.getInstance().screen instanceof DialogueScreen dialogueScreen) {
                dialogueScreen.changeEntry(dialogueScreen.instance.entry.id, entry);
                dialogueScreen.instance.setEntry(entry);
            }
        }
    }
}