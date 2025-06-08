package com.cmdpro.databank.music.conditions;

import com.cmdpro.databank.music.MusicCondition;
import com.cmdpro.databank.music.MusicSerializer;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public class AndMusicCondition extends MusicCondition {
    public MusicCondition conditionA;
    public MusicCondition conditionB;
    public AndMusicCondition(MusicCondition conditionA, MusicCondition conditionB) {
        this.conditionA = conditionA;
        this.conditionB = conditionB;
    }
    @Override
    public boolean isPlaying() {
        return conditionA.isPlaying() && conditionB.isPlaying();
    }
    @Override
    public Serializer getSerializer() {
        return AndConditionSerializer.INSTANCE;
    }
    public static class AndConditionSerializer extends Serializer<AndMusicCondition> {
        public static final AndConditionSerializer INSTANCE = new AndConditionSerializer();
        public static final MapCodec<AndMusicCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                MusicSerializer.MUSIC_CONDITION_CODEC.fieldOf("conditionA").forGetter((condition) -> condition.conditionA),
                MusicSerializer.MUSIC_CONDITION_CODEC.fieldOf("conditionB").forGetter((condition) -> condition.conditionB)
        ).apply(instance, AndMusicCondition::new));
        @Override
        public MapCodec<AndMusicCondition> codec() {
            return CODEC;
        }
    }
}
