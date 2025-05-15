package com.cmdpro.databank.megablock;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class BasicMegablockCore extends Block implements MegablockCore {
    public BasicMegablockCore(Properties properties) {
        super(properties);
    }
    public Rotation getRotation() {
        return Rotation.NONE;
    }
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!MegablockCoreUtil.ableToPlace(this, context)) {
            return null;
        }
        return super.getStateForPlacement(context);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        MegablockCoreUtil.placeRouters(this, getRotation(), level, pos);
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (state.getBlock() != newState.getBlock()) {
            MegablockCoreUtil.removeRouters(this, getRotation(), level, pos);
        }
    }
}
