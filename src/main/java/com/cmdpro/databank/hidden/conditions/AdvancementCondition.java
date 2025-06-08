package com.cmdpro.databank.hidden.conditions;

import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.HiddenSerializer;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class AdvancementCondition extends HiddenCondition {
    public ResourceKey<Advancement> advancement;
    public AdvancementCondition(ResourceKey<Advancement> advancement) {
        this.advancement = advancement;
    }
    @Override
    public boolean isUnlocked(Player player) {
        AdvancementHolder advancement2 = ServerLifecycleHooks.getCurrentServer().getAdvancements().get(advancement.location());
        if (advancement2 != null) {
            return ((ServerPlayer) player).getAdvancements().getOrStartProgress(advancement2).isDone();
        } else {
            return false;
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
    }
}
