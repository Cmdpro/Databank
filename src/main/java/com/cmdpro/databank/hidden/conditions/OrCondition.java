package com.cmdpro.databank.hidden.conditions;

import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.HiddenSerializer;
import com.cmdpro.databank.music.MusicSerializer;
import com.cmdpro.databank.music.conditions.OrMusicCondition;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class OrCondition extends HiddenCondition {
    public HiddenCondition conditionA;
    public HiddenCondition conditionB;
    public OrCondition(HiddenCondition conditionA, HiddenCondition conditionB) {
        this.conditionA = conditionA;
        this.conditionB = conditionB;
    }

    @Override
    public Serializer<?> getSerializer() {
        return OrConditionSerializer.INSTANCE;
    }

    @Override
    public boolean isUnlocked(Player player) {
        return conditionA.isUnlocked(player) || conditionB.isUnlocked(player);
    }
    public static class OrConditionSerializer extends Serializer<OrCondition> {
        public static final OrConditionSerializer INSTANCE = new OrConditionSerializer();
        public static final MapCodec<OrCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                HiddenSerializer.HIDDEN_CONDITION_CODEC.fieldOf("conditionA").forGetter((condition) -> condition.conditionA),
                HiddenSerializer.HIDDEN_CONDITION_CODEC.fieldOf("conditionB").forGetter((condition) -> condition.conditionB)
        ).apply(instance, OrCondition::new));
        @Override
        public MapCodec<OrCondition> codec() {
            return CODEC;
        }
    }
}
