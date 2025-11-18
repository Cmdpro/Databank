package com.cmdpro.databank.dev.registry;

import com.cmdpro.databank.dev.DatabankDev;
import com.cmdpro.databank.dev.block.ModelTestBlock;
import com.cmdpro.databank.dev.block.ModelTestBlockEntity;
import com.cmdpro.databank.megastructures.block.MegastructureSaveBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.GameMasterBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK,
            DatabankDev.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = ItemRegistry.ITEMS;

    public static final Supplier<Block> MODEL_TEST = register("model_test",
            () -> new ModelTestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRUCTURE_BLOCK).noCollission().noOcclusion().noLootTable().noTerrainParticles()),
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
