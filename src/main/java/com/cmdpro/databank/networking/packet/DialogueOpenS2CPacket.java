package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.dialogue.DialogueInstance;
import com.cmdpro.databank.dialogue.DialogueScreen;
import com.cmdpro.databank.dialogue.DialogueTree;
import com.cmdpro.databank.dialogue.DialogueTreeManager;
import com.cmdpro.databank.hidden.Hidden;
import com.cmdpro.databank.hidden.HiddenManager;
import com.cmdpro.databank.hidden.HiddenSerializer;
import com.cmdpro.databank.hidden.HiddenTypeInstance;
import com.cmdpro.databank.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public record DialogueOpenS2CPacket(DialogueTree tree, String entry) implements Message {

    public static DialogueOpenS2CPacket read(FriendlyByteBuf buf) {
        DialogueTree tree = DialogueTree.STREAM_CODEC.decode((RegistryFriendlyByteBuf)buf);
        String entry = buf.readUtf();
        return new DialogueOpenS2CPacket(tree, entry);
    }
    public static void write(FriendlyByteBuf buf, DialogueOpenS2CPacket obj) {
        DialogueTree.STREAM_CODEC.encode((RegistryFriendlyByteBuf)buf, obj.tree);
        buf.writeUtf(obj.entry);
    }
    public static final Type<DialogueOpenS2CPacket> TYPE = new Type<>(Databank.locate("dialogue_open"));
    @Override
    public Type<DialogueOpenS2CPacket> type() {
        return TYPE;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext context) {
        ClientHandler.openScreen(new DialogueInstance(tree, tree.entries.get(entry)));
    }
    private static class ClientHandler {
        public static void openScreen(DialogueInstance instance) {
            Minecraft.getInstance().setScreen(new DialogueScreen(instance));
        }
    }
}