package com.cmdpro.databank.registry;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.databank.multiblock.predicates.serializers.AnyMultiblockPredicateSerializer;
import com.cmdpro.databank.multiblock.predicates.serializers.BlockstateMultiblockPredicateSerializer;
import com.cmdpro.databank.multiblock.predicates.serializers.TagMultiblockPredicateSerializer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MultiblockPredicateRegistry {
    public static final DeferredRegister<MultiblockPredicateSerializer<?>> MULTIBLOCK_PREDICATE_TYPES = DeferredRegister.create(DatabankRegistries.MULTIBLOCK_PREDICATE_REGISTRY_KEY, Databank.MOD_ID);

    public static final Supplier<MultiblockPredicateSerializer<?>> BLOCKSTATE = register("blockstate", () -> BlockstateMultiblockPredicateSerializer.INSTANCE);
    public static final Supplier<MultiblockPredicateSerializer<?>> TAG = register("tag", () -> TagMultiblockPredicateSerializer.INSTANCE);
    public static final Supplier<MultiblockPredicateSerializer<?>> ANY = register("any", () -> AnyMultiblockPredicateSerializer.INSTANCE);
    private static <T extends MultiblockPredicateSerializer<?>> Supplier<T> register(final String name, final Supplier<T> item) {
        return MULTIBLOCK_PREDICATE_TYPES.register(name, item);
    }
}
