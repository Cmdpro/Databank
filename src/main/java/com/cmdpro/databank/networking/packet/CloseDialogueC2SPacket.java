package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.dialogue.DialogueChoice;
import com.cmdpro.databank.dialogue.DialogueChoiceAction;
import com.cmdpro.databank.dialogue.DialogueInstance;
import com.cmdpro.databank.networking.Message;
import com.cmdpro.databank.registry.AttachmentTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record CloseDialogueC2SPacket() implements Message {

    public static CloseDialogueC2SPacket read(FriendlyByteBuf buf) {
        return new CloseDialogueC2SPacket();
    }
    public static void write(FriendlyByteBuf buf, CloseDialogueC2SPacket obj) {
    }
    public static final Type<CloseDialogueC2SPacket> TYPE = new Type<>(Databank.locate("close_dialogue_c2s"));
    @Override
    public Type<CloseDialogueC2SPacket> type() {
        return TYPE;
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player, IPayloadContext context) {
        player.getData(AttachmentTypeRegistry.CURRENT_DIALOGUE).ifPresent((data) -> {
            if (data.entry != null) {
                if (data.entry.closeMenuChoice.isPresent()) {
                    int choice = data.entry.closeMenuChoice.get();
                    if (data.entry.choices.size() > choice && choice >= 0) {
                        DialogueChoice choiceInst = data.entry.choices.get(choice);
                        choiceInst.onClick(player, data, choiceInst);
                    }
                }
            }
        });
        player.setData(AttachmentTypeRegistry.CURRENT_DIALOGUE, Optional.empty());
    }
}