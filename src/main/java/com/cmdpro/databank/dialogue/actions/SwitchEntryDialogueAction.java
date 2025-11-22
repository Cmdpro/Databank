package com.cmdpro.databank.dialogue.actions;

import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.dialogue.DialogueChoice;
import com.cmdpro.databank.dialogue.DialogueChoiceAction;
import com.cmdpro.databank.dialogue.DialogueInstance;
import com.cmdpro.databank.dialogue.styles.BasicDialogueStyle;
import com.cmdpro.databank.rendering.NineSliceSprite;
import com.cmdpro.databank.rendering.SpriteData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class SwitchEntryDialogueAction extends DialogueChoiceAction {
    public String entry;
    @Override
    public void onClick(Player player, DialogueInstance instance, DialogueChoice choice) {
        instance.setEntryServer(entry, player);
    }
    public SwitchEntryDialogueAction(String entry) {
        this.entry = entry;
    }
    public static final MapCodec<SwitchEntryDialogueAction> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.STRING.fieldOf("entry").forGetter((obj) -> obj.entry)
    ).apply(instance, SwitchEntryDialogueAction::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueChoiceAction> STREAM_CODEC = StreamCodec.of((buf, obj) -> {
        if (obj instanceof SwitchEntryDialogueAction action) {
            buf.writeUtf(action.entry);
        }
    }, (buf) -> {
        String entry = buf.readUtf();
        return new SwitchEntryDialogueAction(entry);
    });

    public static final Codecs CODECS = new Codecs(CODEC, STREAM_CODEC);
    @Override
    public Codecs getCodecs() {
        return CODECS;
    }
}
