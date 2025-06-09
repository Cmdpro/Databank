package com.cmdpro.databank.hidden.conditions;

import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.types.BlockHiddenType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class ActualPlayerCondition extends HiddenCondition {
    public ActualPlayerCondition() {}
    @Override
    public boolean isUnlocked(Player player) {
        return player != null;
    }
    @Override
    public Serializer<?> getSerializer() {
        return ActualPlayerConditionSerializer.INSTANCE;
    }
    public static class ActualPlayerConditionSerializer extends Serializer<ActualPlayerCondition> {
        public static final ActualPlayerConditionSerializer INSTANCE = new ActualPlayerConditionSerializer();
        public static final MapCodec<ActualPlayerCondition> CODEC = MapCodec.unit(new ActualPlayerCondition());
        @Override
        public MapCodec<ActualPlayerCondition> codec() {
            return CODEC;
        }
        public static final StreamCodec<RegistryFriendlyByteBuf, ActualPlayerCondition> STREAM_CODEC = StreamCodec.of((buf, val) -> {}, (buf) -> new ActualPlayerCondition());
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ActualPlayerCondition> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
