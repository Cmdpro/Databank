package com.cmdpro.databank.dialogue.actions;

import com.cmdpro.databank.dialogue.DialogueChoice;
import com.cmdpro.databank.dialogue.DialogueChoiceAction;
import com.cmdpro.databank.dialogue.DialogueInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class CommandDialogueAction extends DialogueChoiceAction {
    public String command;
    @Override
    public void onClick(Player player, DialogueInstance instance, DialogueChoice choice) {
        CommandSourceStack source = player.createCommandSourceStack();
        source.withPermission(4);
        if (player.getServer() != null)
            player.getServer().getCommands().performPrefixedCommand(source, command);
    }
    public CommandDialogueAction(String command) {
        this.command = command;
    }
    public static final MapCodec<CommandDialogueAction> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.STRING.fieldOf("command").forGetter((obj) -> obj.command)
    ).apply(instance, CommandDialogueAction::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueChoiceAction> STREAM_CODEC = StreamCodec.of((buf, obj) -> {
        if (obj instanceof CommandDialogueAction action) {
            buf.writeUtf(action.command);
        }
    }, (buf) -> {
        String command = buf.readUtf();
        return new CommandDialogueAction(command);
    });

    public static final Codecs CODECS = new Codecs(CODEC, STREAM_CODEC);
    @Override
    public Codecs getCodecs() {
        return CODECS;
    }
}
