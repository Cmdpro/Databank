package com.cmdpro.databank.dialogue;

import com.cmdpro.databank.networking.ModMessages;
import com.cmdpro.databank.networking.packet.ChangeDialogueEntryS2CPacket;
import com.cmdpro.databank.networking.packet.CloseDialogueS2CPacket;
import com.cmdpro.databank.networking.packet.DialogueOpenS2CPacket;
import com.cmdpro.databank.registry.AttachmentTypeRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class DialogueInstance {
    public DialogueTree tree;
    public DialogueEntry entry;
    public DialogueInstance(DialogueTree tree, DialogueEntry entry) {
        this.tree = tree;
        this.entry = entry;
    }
    public DialogueInstance setTree(DialogueTree tree) {
        this.tree = tree;
        return this;
    }
    public DialogueInstance setEntry(DialogueEntry entry) {
        this.entry = entry;
        return this;
    }
    public DialogueInstance setEntry(String entry) {
        return setEntry(tree.entries.get(entry));
    }
    public DialogueInstance setEntryServer(String entry, Player player) {
        setEntry(entry);
        ModMessages.sendToPlayer(new ChangeDialogueEntryS2CPacket(entry), (ServerPlayer)player);
        return this;
    }
    public void close(Player player) {
        ModMessages.sendToPlayer(new CloseDialogueS2CPacket(), (ServerPlayer)player);
        player.setData(AttachmentTypeRegistry.CURRENT_DIALOGUE, Optional.empty());
    }
    public DialogueInstance setForPlayer(Player player) {
        player.setData(AttachmentTypeRegistry.CURRENT_DIALOGUE, Optional.of(this));
        ModMessages.sendToPlayer(new DialogueOpenS2CPacket(tree, entry.id), (ServerPlayer)player);
        return this;
    }
}
