package com.cmdpro.databank.advancement.criteria;

import com.cmdpro.databank.registry.CriteriaTriggerRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;

public class HasAdvancementsCriteria extends SimpleCriterionTrigger<HasAdvancementsCriteria.HasAdvancementsCriteriaInstance> {
    public static Criterion<HasAdvancementsCriteriaInstance> instance(ContextAwarePredicate player, List<ResourceLocation> advancement, MinMaxBounds.Ints range) {
        return CriteriaTriggerRegistry.HAS_ADVANCEMENTS.get().createCriterion(new HasAdvancementsCriteriaInstance(Optional.of(player), advancement, range == null ? Optional.empty() : Optional.of(range)));
    }
    public static final Codec<HasAdvancementsCriteriaInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(HasAdvancementsCriteriaInstance::player),
            ResourceLocation.CODEC.listOf().fieldOf("advancements").forGetter(HasAdvancementsCriteriaInstance::advancements),
            MinMaxBounds.Ints.CODEC.optionalFieldOf("range").forGetter(HasAdvancementsCriteriaInstance::range)
    ).apply(instance, HasAdvancementsCriteriaInstance::new));
    @Override
    public Codec<HasAdvancementsCriteriaInstance> codec() {
        return CODEC;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (instance) -> instance.valid(player));
    }
    public record HasAdvancementsCriteriaInstance(Optional<ContextAwarePredicate> player, List<ResourceLocation> advancements, Optional<MinMaxBounds.Ints> range) implements SimpleInstance {
        public boolean valid(ServerPlayer player) {
            MinecraftServer server = player.getServer();
            if (server != null) {
                ServerAdvancementManager serverAdvancements = server.getAdvancements();
                PlayerAdvancements playerAdvancements = player.getAdvancements();
                int matching = 0;
                int amount = advancements.size();
                for (ResourceLocation i : advancements) {
                    AdvancementHolder holder = serverAdvancements.get(i);
                    if (holder != null) {
                        if (playerAdvancements.getOrStartProgress(holder).isDone()) {
                            matching++;
                        }
                    }
                }
                return this.range.isPresent() ? range.get().matches(matching) : matching >= amount;
            }
            return false;
        }
    }
}
