package com.cmdpro.databank.megablock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class MegablockRouter extends Block {
    public static final Property<Direction> FACING = BlockStateProperties.FACING;
    public Block core;
    public MegablockRouter(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        if (level instanceof ServerLevel serverLevel) {
            BlockPos blockPos = findCore(serverLevel, pos);
            if (blockPos != null) {
                BlockState blockState = level.getBlockState(blockPos);
                if (blockState.is(core)) {
                    level.destroyBlock(pos.below(), true);
                }
            }
        }
    }
    public BlockPos findCore(Level level, BlockPos pos) {
        List<BlockPos> visited = new ArrayList<>();
        BlockPos blockPos = pos;
        boolean valid = false;
        while (!visited.contains(blockPos)) {
            visited.add(blockPos);
            BlockState state2 = level.getBlockState(blockPos);
            if (state2.is(this)) {
                blockPos = blockPos.relative(state2.getValue(FACING));
            } else {
                if (state2.is(core)) {
                    valid = true;
                }
                break;
            }
        }
        if (valid) {
            return blockPos;
        }
        return null;
    }
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            if (findCore(serverLevel, pos) == null) {
                return false;
            }
        }
        return super.canSurvive(state, level, pos);
    }
    public Vec3i findOffset(Level level, BlockPos pos) {
        return pos.subtract(findCore(level, pos));
    }
}
