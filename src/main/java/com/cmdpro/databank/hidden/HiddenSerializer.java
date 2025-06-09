package com.cmdpro.databank.hidden;

import com.cmdpro.databank.DatabankRegistries;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.Optional;

public class HiddenSerializer {
    public static final Codec<HiddenTypeInstance<?>> HIDDEN_TYPE_CODEC = DatabankRegistries.HIDDEN_TYPE_REGISTRY.byNameCodec().dispatch(HiddenTypeInstance::getType, HiddenTypeInstance.HiddenType::codec);
    public static final Codec<HiddenCondition> HIDDEN_CONDITION_CODEC = DatabankRegistries.HIDDEN_CONDITION_REGISTRY.byNameCodec().dispatch(HiddenCondition::getSerializer, HiddenCondition.Serializer::codec);
    public static final Codec<Hidden> ORIGINAL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            HIDDEN_TYPE_CODEC.fieldOf("type").forGetter((controller) -> controller.type),
            HIDDEN_CONDITION_CODEC.fieldOf("condition").forGetter((controller) -> controller.condition)
    ).apply(instance, Hidden::new));
    public static final Codec<Optional<WithConditions<Hidden>>> CODEC = ConditionalOps.createConditionalCodecWithConditions(ORIGINAL_CODEC);
    public Hidden read(ResourceLocation entryId, JsonObject json) {
        return ICondition.getWithWithConditionsCodec(CODEC, JsonOps.INSTANCE, json).orElse(null);
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, Hidden> STREAM_CODEC = StreamCodec.of((buf, value) -> {
        buf.writeResourceKey(DatabankRegistries.HIDDEN_TYPE_REGISTRY.getResourceKey(value.type.getType()).orElseThrow());
        value.type.getType().streamCodec().encode(buf, value.type);
        buf.writeResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY.getResourceKey(value.condition.getSerializer()).orElseThrow());
        value.condition.getSerializer().streamCodec().encode(buf, value.condition);
    }, (buf) -> {
        ResourceKey<HiddenTypeInstance.HiddenType<?>> key = buf.readResourceKey(DatabankRegistries.HIDDEN_TYPE_REGISTRY_KEY);
        HiddenTypeInstance.HiddenType<?> serializer = DatabankRegistries.HIDDEN_TYPE_REGISTRY.get(key);
        HiddenTypeInstance<?> type = serializer.streamCodec().decode(buf);
        ResourceKey<HiddenCondition.Serializer<?>> conditionKey = buf.readResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY_KEY);
        HiddenCondition.Serializer<?> conditionSerializer = DatabankRegistries.HIDDEN_CONDITION_REGISTRY.get(conditionKey);
        HiddenCondition condition = conditionSerializer.streamCodec().decode(buf);
        return new Hidden(type, condition);
    });
}