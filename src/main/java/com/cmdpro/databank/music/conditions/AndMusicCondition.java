package com.cmdpro.databank.music.conditions;

import com.cmdpro.databank.music.MusicConditions;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class AndMusicCondition extends MusicConditions.MusicCondition {
    public MusicConditions.MusicCondition conditionA;
    public MusicConditions.MusicCondition conditionB;
    public AndMusicCondition(MusicConditions.MusicCondition conditionA, MusicConditions.MusicCondition conditionB) {
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
    public static class AndConditionSerializer extends Serializer {
        public static final AndConditionSerializer INSTANCE = new AndConditionSerializer();
        @Override
        public MusicConditions.MusicCondition deserialize(JsonObject json) {
            ResourceLocation conditionA = ResourceLocation.tryParse(json.get("conditionA").getAsString());
            JsonObject conditionAData = json.get("conditionAData").getAsJsonObject();
            ResourceLocation conditionB = ResourceLocation.tryParse(json.get("conditionB").getAsString());
            JsonObject conditionBData = json.get("conditionBData").getAsJsonObject();
            return new AndMusicCondition(MusicConditions.conditions.get(conditionA).deserialize(conditionAData), MusicConditions.conditions.get(conditionB).deserialize(conditionBData));
        }
    }
}
