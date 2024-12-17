package com.cmdpro.databank.music.conditions;

import com.cmdpro.databank.music.MusicConditions;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class NotMusicCondition extends MusicConditions.MusicCondition {
    public MusicConditions.MusicCondition condition;
    public NotMusicCondition(MusicConditions.MusicCondition condition) {
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
    public static class NotConditionSerializer extends Serializer {
        public static final NotConditionSerializer INSTANCE = new NotConditionSerializer();
        @Override
        public MusicConditions.MusicCondition deserialize(JsonObject json) {
            ResourceLocation condition = ResourceLocation.tryParse(json.get("condition").getAsString());
            JsonObject conditionData = json.get("conditionData").getAsJsonObject();
            return new NotMusicCondition(MusicConditions.conditions.get(condition).deserialize(conditionData));
        }
    }
}
