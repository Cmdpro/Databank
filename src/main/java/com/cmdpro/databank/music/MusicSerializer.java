package com.cmdpro.databank.music;

import com.cmdpro.databank.DatabankRegistries;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.Optional;

public class MusicSerializer {
    public static final Codec<MusicCondition> MUSIC_CONDITION_CODEC = DatabankRegistries.MUSIC_CONDITION_REGISTRY.byNameCodec().dispatch(MusicCondition::getSerializer, MusicCondition.Serializer::codec);
    public static final Codec<MusicController> ORIGINAL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MUSIC_CONDITION_CODEC.fieldOf("condition").forGetter((controller) -> controller.condition),
            ResourceKey.codec(Registries.SOUND_EVENT).fieldOf("music").forGetter((controller) -> controller.music),
            Codec.INT.fieldOf("priority").forGetter((controller) -> controller.priority)
    ).apply(instance, MusicController::new));
    public static final Codec<Optional<WithConditions<MusicController>>> CODEC = ConditionalOps.createConditionalCodecWithConditions(ORIGINAL_CODEC);
    public MusicController read(ResourceLocation entryId, JsonObject json) {
        return ICondition.getWithWithConditionsCodec(CODEC, JsonOps.INSTANCE, json).orElse(null);
    }
}