package com.cmdpro.databank.dialogue;

import com.cmdpro.databank.DatabankRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;

public abstract class DialogueChoiceAction {
    public static final Codec<DialogueChoiceAction> CODEC = DatabankRegistries.DIALOGUE_CHOICE_ACTION_REGISTRY.byNameCodec().dispatch((i) -> new Codecs((MapCodec<DialogueChoiceAction>)i.getCodec(), i.getStreamCodec()), (i) -> i.codec);
    public abstract void onClick(Player player, DialogueInstance instance, DialogueChoice choice);
    public MapCodec<? extends DialogueChoiceAction> getCodec() {
        return getCodecs().codec();
    }
    public StreamCodec<RegistryFriendlyByteBuf, DialogueChoiceAction> getStreamCodec() {
        return getCodecs().streamCodec();
    }
    public abstract Codecs getCodecs();
    public record Codecs(MapCodec<? extends DialogueChoiceAction> codec, StreamCodec<RegistryFriendlyByteBuf, DialogueChoiceAction> streamCodec) {}
}
