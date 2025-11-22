package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.dialogue.*;
import com.cmdpro.databank.networking.Message;
import com.cmdpro.databank.registry.AttachmentTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record ClickChoiceC2SPacket(int choice) implements Message {

    public static ClickChoiceC2SPacket read(FriendlyByteBuf buf) {
        int choice = buf.readInt();
        return new ClickChoiceC2SPacket(choice);
    }
    public static void write(FriendlyByteBuf buf, ClickChoiceC2SPacket obj) {
        buf.writeInt(obj.choice);
    }
    public static final Type<ClickChoiceC2SPacket> TYPE = new Type<>(Databank.locate("click_choice"));
    @Override
    public Type<ClickChoiceC2SPacket> type() {
        return TYPE;
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player, IPayloadContext context) {
        Optional<DialogueInstance> dialogue = player.getData(AttachmentTypeRegistry.CURRENT_DIALOGUE);
        if (dialogue.isPresent()) {
            if (choice >= 0 && choice < dialogue.get().entry.choices.size()) {
                DialogueChoice choice = dialogue.get().entry.choices.get(this.choice);
                for (DialogueChoiceAction i : choice.actions) {
                    i.onClick(player, dialogue.get(), choice);
                }
            }
        }
    }
}