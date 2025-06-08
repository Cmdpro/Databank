package com.cmdpro.databank.music.conditions;

import com.cmdpro.databank.music.MusicCondition;
import com.cmdpro.databank.music.MusicSerializer;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public class NotMusicCondition extends MusicCondition {
    public MusicCondition condition;
    public NotMusicCondition(MusicCondition condition) {
        this.condition = condition;
    }
    @Override
    public boolean isPlaying() {
        return !condition.isPlaying();
    }
    @Override
    public Serializer getSerializer() {
        return NotConditionSerializer.INSTANCE;
    }
    public static class NotConditionSerializer extends Serializer<NotMusicCondition> {
        public static final NotConditionSerializer INSTANCE = new NotConditionSerializer();
        public static final MapCodec<NotMusicCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                MusicSerializer.MUSIC_CONDITION_CODEC.fieldOf("condition").forGetter((condition) -> condition.condition)
        ).apply(instance, NotMusicCondition::new));
        @Override
        public MapCodec<NotMusicCondition> codec() {
            return CODEC;
        }
    }
}
