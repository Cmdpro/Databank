package com.cmdpro.databank.registry;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.databank.multiblock.predicates.serializers.BlockstateMultiblockPredicateSerializer;
import com.cmdpro.databank.multiblock.predicates.serializers.TagMultiblockPredicateSerializer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MultiblockPredicateRegistry {
    public static final DeferredRegister<MultiblockPredicateSerializer> MULTIBLOCK_PREDICATE_TYPES = DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "multiblock_predicates"), Databank.MOD_ID);

    public static final Supplier<MultiblockPredicateSerializer> BLOCKSTATE = register("blockstate", () -> new BlockstateMultiblockPredicateSerializer());
    public static final Supplier<MultiblockPredicateSerializer> TAG = register("tag", () -> new TagMultiblockPredicateSerializer());
    private static <T extends MultiblockPredicateSerializer> Supplier<T> register(final String name, final Supplier<T> item) {
        return MULTIBLOCK_PREDICATE_TYPES.register(name, item);
    }
}
