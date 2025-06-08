package com.cmdpro.databank.multiblock.predicates;

import com.cmdpro.databank.multiblock.MultiblockPredicate;
import com.cmdpro.databank.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.databank.registry.MultiblockPredicateRegistry;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class AnyMultiblockPredicate extends MultiblockPredicate {
    @Override
    public boolean isSame(BlockState other, Rotation rotation) {
        return true;
    }

    @Override
    public MultiblockPredicateSerializer<?> getSerializer() {
        return MultiblockPredicateRegistry.ANY.get();
    }

    @Override
    public BlockState getVisual() {
        return Blocks.AIR.defaultBlockState();
    }
}
