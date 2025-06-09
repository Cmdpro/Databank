package com.cmdpro.databank.hidden.conditions;

import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.HiddenSerializer;
import com.cmdpro.databank.music.MusicSerializer;
import com.cmdpro.databank.music.conditions.OrMusicCondition;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
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
        public static final StreamCodec<RegistryFriendlyByteBuf, OrCondition> STREAM_CODEC = StreamCodec.of((buf, value) -> {
            buf.writeResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY.getResourceKey(value.conditionA.getSerializer()).orElseThrow());
            value.conditionA.getSerializer().streamCodec().encode(buf, value.conditionA);
            buf.writeResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY.getResourceKey(value.conditionB.getSerializer()).orElseThrow());
            value.conditionB.getSerializer().streamCodec().encode(buf, value.conditionB);
        }, (buf) -> {
            ResourceKey<Serializer<?>> conditionAKey = buf.readResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY_KEY);
            HiddenCondition.Serializer<?> conditionASerializer = DatabankRegistries.HIDDEN_CONDITION_REGISTRY.get(conditionAKey);
            HiddenCondition conditionA = conditionASerializer.streamCodec().decode(buf);
            ResourceKey<HiddenCondition.Serializer<?>> conditionBKey = buf.readResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY_KEY);
            HiddenCondition.Serializer<?> conditionBSerializer = DatabankRegistries.HIDDEN_CONDITION_REGISTRY.get(conditionBKey);
            HiddenCondition conditionB = conditionBSerializer.streamCodec().decode(buf);
            return new OrCondition(conditionA, conditionB);
        });

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, OrCondition> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
