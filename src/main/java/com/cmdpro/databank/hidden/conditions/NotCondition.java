package com.cmdpro.databank.hidden.conditions;

import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.HiddenSerializer;
import com.cmdpro.databank.music.MusicSerializer;
import com.cmdpro.databank.music.conditions.NotMusicCondition;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class NotCondition extends HiddenCondition {
    public HiddenCondition condition;
    public NotCondition(HiddenCondition condition) {
        this.condition = condition;
    }
    @Override
    public boolean isUnlocked(Player player) {
        return !condition.isUnlocked(player);
    }
    @Override
    public Serializer<?> getSerializer() {
        return NotConditionSerializer.INSTANCE;
    }
    public static class NotConditionSerializer extends Serializer {
        public static final NotConditionSerializer INSTANCE = new NotConditionSerializer();
        public static final MapCodec<NotCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            HiddenSerializer.HIDDEN_CONDITION_CODEC.fieldOf("condition").forGetter((condition) -> condition.condition)
        ).apply(instance, NotCondition::new));
        @Override
        public MapCodec<NotCondition> codec() {
            return CODEC;
        }
    }
}
