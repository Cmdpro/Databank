package com.cmdpro.databank.registry;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.hidden.HiddenTypeInstance;
import com.cmdpro.databank.hidden.types.BlockHiddenType;
import com.cmdpro.databank.hidden.types.ItemHiddenType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HiddenTypeRegistry {
    public static final DeferredRegister<HiddenTypeInstance.HiddenType<?>> HIDDEN_TYPES = DeferredRegister.create(DatabankRegistries.HIDDEN_TYPE_REGISTRY_KEY, Databank.MOD_ID);

    public static final Supplier<HiddenTypeInstance.HiddenType<?>> BLOCK = register("block", () -> BlockHiddenType.INSTANCE);
    public static final Supplier<HiddenTypeInstance.HiddenType<?>> ITEM = register("item", () -> ItemHiddenType.INSTANCE);
    private static <T extends HiddenTypeInstance.HiddenType<?>> Supplier<T> register(final String name, final Supplier<T> item) {
        return HIDDEN_TYPES.register(name, item);
    }
}
