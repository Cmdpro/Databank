package com.cmdpro.databank.hidden.conditions;

import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.HiddenSerializer;
import com.cmdpro.databank.hidden.types.BlockHiddenType;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Optional;

public class AdvancementCondition extends HiddenCondition {
    public ResourceKey<Advancement> advancement;
    public AdvancementCondition(ResourceKey<Advancement> advancement) {
        this.advancement = advancement;
    }
    @Override
    public boolean isUnlocked(Player player) {
        if (player != null) {
            AdvancementHolder advancement2 = ServerLifecycleHooks.getCurrentServer().getAdvancements().get(advancement.location());
            if (advancement2 != null) {
                return ((ServerPlayer) player).getAdvancements().getOrStartProgress(advancement2).isDone();
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    @Override
    public Serializer<?> getSerializer() {
        return AdvancementConditionSerializer.INSTANCE;
    }
    public static class AdvancementConditionSerializer extends Serializer<AdvancementCondition> {
        public static final AdvancementConditionSerializer INSTANCE = new AdvancementConditionSerializer();
        public static final MapCodec<AdvancementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceKey.codec(Registries.ADVANCEMENT).fieldOf("advancement").forGetter((condition) -> condition.advancement)
        ).apply(instance, AdvancementCondition::new));
        @Override
        public MapCodec<AdvancementCondition> codec() {
            return CODEC;
        }
        public static final StreamCodec<RegistryFriendlyByteBuf, AdvancementCondition> STREAM_CODEC = StreamCodec.of((buf, val) -> {
            buf.writeResourceKey(val.advancement);
        }, (buf) -> {
            ResourceKey<Advancement> advancement = buf.readResourceKey(Registries.ADVANCEMENT);
            return new AdvancementCondition(advancement);
        });

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AdvancementCondition> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
