package com.cmdpro.databank.registry;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.conditions.*;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HiddenConditionRegistry {
    public static final DeferredRegister<HiddenCondition.Serializer<?>> HIDDEN_CONDITIONS = DeferredRegister.create(DatabankRegistries.HIDDEN_CONDITION_REGISTRY_KEY, Databank.MOD_ID);

    public static final Supplier<HiddenCondition.Serializer<?>> AND = register("and", () -> AndCondition.AndConditionSerializer.INSTANCE);
    public static final Supplier<HiddenCondition.Serializer<?>> OR = register("or", () -> OrCondition.OrConditionSerializer.INSTANCE);
    public static final Supplier<HiddenCondition.Serializer<?>> NOT = register("not", () -> NotCondition.NotConditionSerializer.INSTANCE);
    public static final Supplier<HiddenCondition.Serializer<?>> ADVANCEMENT = register("advancement", () -> AdvancementCondition.AdvancementConditionSerializer.INSTANCE);
    public static final Supplier<HiddenCondition.Serializer<?>> ACTUAL_PLAYER = register("actual_player", () -> ActualPlayerCondition.ActualPlayerConditionSerializer.INSTANCE);
    public static final Supplier<HiddenCondition.Serializer<?>> ALWAYS_TRUE = register("always_true", () -> AlwaysTrueCondition.AlwaysTrueConditionSerializer.INSTANCE);
    private static <T extends HiddenCondition.Serializer<?>> Supplier<T> register(final String name, final Supplier<T> item) {
        return HIDDEN_CONDITIONS.register(name, item);
    }
}
