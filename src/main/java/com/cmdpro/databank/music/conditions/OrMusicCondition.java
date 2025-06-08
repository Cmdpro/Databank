package com.cmdpro.databank.music.conditions;

import com.cmdpro.databank.music.MusicCondition;
import com.cmdpro.databank.music.MusicSerializer;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public class OrMusicCondition extends MusicCondition {
    public MusicCondition conditionA;
    public MusicCondition conditionB;
    public OrMusicCondition(MusicCondition conditionA, MusicCondition conditionB) {
        this.conditionA = conditionA;
        this.conditionB = conditionB;
    }
    @Override
    public boolean isPlaying() {
        return conditionA.isPlaying() || conditionB.isPlaying();
    }
    @Override
    public Serializer getSerializer() {
        return OrConditionSerializer.INSTANCE;
    }
    public static class OrConditionSerializer extends Serializer<OrMusicCondition> {
        public static final OrConditionSerializer INSTANCE = new OrConditionSerializer();
        public static final MapCodec<OrMusicCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                MusicSerializer.MUSIC_CONDITION_CODEC.fieldOf("conditionA").forGetter((condition) -> condition.conditionA),
                MusicSerializer.MUSIC_CONDITION_CODEC.fieldOf("conditionB").forGetter((condition) -> condition.conditionB)
        ).apply(instance, OrMusicCondition::new));
        @Override
        public MapCodec<OrMusicCondition> codec() {
            return CODEC;
        }
    }
}
