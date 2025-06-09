package com.cmdpro.databank.hidden.conditions;

import com.cmdpro.databank.hidden.HiddenCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public class AlwaysTrueCondition extends HiddenCondition {
    public AlwaysTrueCondition() {}
    @Override
    public boolean isUnlocked(Player player) {
        return true;
    }
    @Override
    public Serializer<?> getSerializer() {
        return AlwaysTrueConditionSerializer.INSTANCE;
    }
    public static class AlwaysTrueConditionSerializer extends Serializer<AlwaysTrueCondition> {
        public static final AlwaysTrueConditionSerializer INSTANCE = new AlwaysTrueConditionSerializer();
        public static final MapCodec<AlwaysTrueCondition> CODEC = MapCodec.unit(new AlwaysTrueCondition());
        @Override
        public MapCodec<AlwaysTrueCondition> codec() {
            return CODEC;
        }
        public static final StreamCodec<RegistryFriendlyByteBuf, AlwaysTrueCondition> STREAM_CODEC = StreamCodec.of((buf, val) -> {}, (buf) -> new AlwaysTrueCondition());
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlwaysTrueCondition> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
