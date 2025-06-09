package com.cmdpro.databank.hidden.conditions;

import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.HiddenSerializer;
import com.cmdpro.databank.music.MusicSerializer;
import com.cmdpro.databank.music.conditions.NotMusicCondition;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
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
        public static final StreamCodec<RegistryFriendlyByteBuf, NotCondition> STREAM_CODEC = StreamCodec.of((buf, value) -> {
            buf.writeResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY.getResourceKey(value.condition.getSerializer()).orElseThrow());
            value.condition.getSerializer().streamCodec().encode(buf, value.condition);
        }, (buf) -> {
            ResourceKey<Serializer<?>> conditionKey = buf.readResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY_KEY);
            HiddenCondition.Serializer<?> conditionSerializer = DatabankRegistries.HIDDEN_CONDITION_REGISTRY.get(conditionKey);
            HiddenCondition condition = conditionSerializer.streamCodec().decode(buf);
            return new NotCondition(condition);
        });

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, NotCondition> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
