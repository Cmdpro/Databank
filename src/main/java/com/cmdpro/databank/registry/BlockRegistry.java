package com.cmdpro.databank.registry;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.megastructures.block.MegastructureSaveBlock;
import com.cmdpro.databank.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.databank.multiblock.predicates.serializers.AnyMultiblockPredicateSerializer;
import com.cmdpro.databank.multiblock.predicates.serializers.BlockstateMultiblockPredicateSerializer;
import com.cmdpro.databank.multiblock.predicates.serializers.TagMultiblockPredicateSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.GameMasterBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK,
            Databank.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = ItemRegistry.ITEMS;

    public static final Supplier<Block> MEGASTRUCTURE_SAVE = register("megastructure_save",
            () -> new MegastructureSaveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRUCTURE_BLOCK)),
            object -> () -> new GameMasterBlockItem(object.get(), new Item.Properties()));

    private static <T extends Block> Supplier<T> registerBlock(final String name,
                                                               final Supplier<? extends T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> Supplier<T> register(final String name, final Supplier<? extends T> block,
                                                          Function<Supplier<T>, Supplier<? extends Item>> item) {
        Supplier<T> obj = registerBlock(name, block);
        ITEMS.register(name, item.apply(obj));
        return obj;
    }
}
