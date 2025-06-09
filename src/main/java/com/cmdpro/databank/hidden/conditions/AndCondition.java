package com.cmdpro.databank.hidden.conditions;

import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.hidden.Hidden;
import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.HiddenSerializer;
import com.cmdpro.databank.hidden.HiddenTypeInstance;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
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
        public static final StreamCodec<RegistryFriendlyByteBuf, AndCondition> STREAM_CODEC = StreamCodec.of((buf, value) -> {
            buf.writeResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY.getResourceKey(value.conditionA.getSerializer()).orElseThrow());
            value.conditionA.getSerializer().streamCodec().encode(buf, value.conditionA);
            buf.writeResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY.getResourceKey(value.conditionB.getSerializer()).orElseThrow());
            value.conditionB.getSerializer().streamCodec().encode(buf, value.conditionB);
        }, (buf) -> {
            ResourceKey<HiddenCondition.Serializer<?>> conditionAKey = buf.readResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY_KEY);
            HiddenCondition.Serializer<?> conditionASerializer = DatabankRegistries.HIDDEN_CONDITION_REGISTRY.get(conditionAKey);
            HiddenCondition conditionA = conditionASerializer.streamCodec().decode(buf);
            ResourceKey<HiddenCondition.Serializer<?>> conditionBKey = buf.readResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY_KEY);
            HiddenCondition.Serializer<?> conditionBSerializer = DatabankRegistries.HIDDEN_CONDITION_REGISTRY.get(conditionBKey);
            HiddenCondition conditionB = conditionBSerializer.streamCodec().decode(buf);
            return new AndCondition(conditionA, conditionB);
        });

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AndCondition> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
