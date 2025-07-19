package com.cmdpro.databank.registry;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.advancement.criteria.HasAdvancementCriteria;
import com.cmdpro.databank.advancement.criteria.HasAdvancementsCriteria;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CriteriaTriggerRegistry {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES,
            Databank.MOD_ID);
    public static final Supplier<HasAdvancementCriteria> HAS_ADVANCEMENT = register("has_advancement", HasAdvancementCriteria::new);
    public static final Supplier<HasAdvancementsCriteria> HAS_ADVANCEMENTS = register("has_advancements", HasAdvancementsCriteria::new);
    private static <T extends CriterionTrigger<?>> Supplier<T> register(final String name,
                                                                        final Supplier<? extends T> trigger) {
        return TRIGGERS.register(name, trigger);
    }
}