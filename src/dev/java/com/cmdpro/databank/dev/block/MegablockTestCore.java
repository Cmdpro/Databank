package com.cmdpro.databank.dev.block;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.dev.registry.BlockRegistry;
import com.cmdpro.databank.megablock.BasicMegablockCore;
import com.cmdpro.databank.megablock.MegablockShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class MegablockTestCore extends BasicMegablockCore {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public MegablockTestCore(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public Rotation getRotation(BlockState state) {
        return DatabankUtils.getRotationFromDirection(state.getValue(FACING));
    }

    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return checkPlacement(pContext, this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite()));
    }

    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public MegablockShape getMegablockShape() {
        return shape;
    }

    public static MegablockShape shape = new MegablockShape(new ArrayList<>() {
        {
            add(Vec3i.ZERO);
            add(new Vec3i(0, 1, 0));
            add(new Vec3i(0, 2, 0));
            add(new Vec3i(0, 0, 1));
            add(new Vec3i(0, 1, 1));
        }
    });

    @Override
    public Block getRouterBlock() {
        return BlockRegistry.MEGABLOCK_TEST_ROUTER.get();
    }
}