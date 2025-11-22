package com.cmdpro.databank.dialogue.actions;

import com.cmdpro.databank.dialogue.DialogueChoice;
import com.cmdpro.databank.dialogue.DialogueChoiceAction;
import com.cmdpro.databank.dialogue.DialogueInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public class CloseDialogueAction extends DialogueChoiceAction {
    @Override
    public void onClick(Player player, DialogueInstance instance, DialogueChoice choice) {
        instance.close(player);
    }
    public static final MapCodec<CloseDialogueAction> CODEC = MapCodec.unit(new CloseDialogueAction());
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueChoiceAction> STREAM_CODEC = StreamCodec.of((buf, obj) -> {
    }, (buf) -> {
        return new CloseDialogueAction();
    });

    public static final Codecs CODECS = new Codecs(CODEC, STREAM_CODEC);
    @Override
    public Codecs getCodecs() {
        return CODECS;
    }
}
