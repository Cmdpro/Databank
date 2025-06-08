package com.cmdpro.databank.hidden.conditions;

import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.HiddenSerializer;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class AndCondition extends HiddenCondition {
    public HiddenCondition conditionA;
    public HiddenCondition conditionB;
    public AndCondition(HiddenCondition conditionA, HiddenCondition conditionB) {
        this.conditionA = conditionA;
        this.conditionB = conditionB;
    }
    @Override
    public boolean isUnlocked(Player player) {
        return conditionA.isUnlocked(player) && conditionB.isUnlocked(player);
    }
    @Override
    public Serializer<?> getSerializer() {
        return AndConditionSerializer.INSTANCE;
    }
    public static class AndConditionSerializer extends HiddenCondition.Serializer<AndCondition> {
        public static final AndConditionSerializer INSTANCE = new AndConditionSerializer();
        public static final MapCodec<AndCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                HiddenSerializer.HIDDEN_CONDITION_CODEC.fieldOf("conditionA").forGetter((condition) -> condition.conditionA),
                HiddenSerializer.HIDDEN_CONDITION_CODEC.fieldOf("conditionB").forGetter((condition) -> condition.conditionB)
        ).apply(instance, AndCondition::new));
        @Override
        public MapCodec<AndCondition> codec() {
            return CODEC;
        }
    }
}
