package com.cmdpro.databank.megablock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class MegablockCoreUtil {
    public static boolean ableToPlace(MegablockCore core, Rotation rotation, BlockPlaceContext context) {
        MegablockShape shape = core.getMegablockShape().getRotated(rotation);
        for (Vec3i i : shape.shape) {
            if (!context.getLevel().getBlockState(context.getClickedPos().offset(i)).canBeReplaced()) {
                return false;
            }
        }
        return true;
    }
    public static boolean ableToPlace(MegablockCore core, BlockPlaceContext context) {
        return ableToPlace(core, Rotation.NONE, context);
    }
    public static void placeRouters(MegablockCore core, Rotation rotation, Level level, BlockPos pos) {
        MegablockShape shape = core.getMegablockShape().getRotated(rotation);
        for (Vec3i i : shape.shape) {
            if (!i.equals(Vec3i.ZERO) && level.getBlockState(pos.offset(i)).canBeReplaced()) {
                BlockState router = core.getRouterBlock().defaultBlockState();
                level.setBlockAndUpdate(pos.offset(i), router);
            }
        }
        setRouterDirections(shape, core, pos, level, pos);
    }
    public static void setRouterDirections(MegablockCore core, BlockPos corePos, Level level, BlockPos pos) {
        setRouterDirections(new ArrayList<>(), core.getMegablockShape(), core, corePos, level, pos);
    }
    public static void setRouterDirections(MegablockShape shape, MegablockCore core, BlockPos corePos, Level level, BlockPos pos) {
        setRouterDirections(new ArrayList<>(), shape, core, corePos, level, pos);
    }
    private static void setRouterDirections(List<BlockPos> alreadyVisited, MegablockShape shape, MegablockCore core, BlockPos corePos, Level level, BlockPos pos) {
        alreadyVisited.add(pos);
        Direction[] directions = Direction.values();
        for (Direction i : directions) {
            BlockPos shifted = pos.relative(i);
            if (alreadyVisited.contains(shifted)) {
                continue;
            }
            if (shape.shape.contains(shifted.subtract(corePos))) {
                BlockState state = level.getBlockState(shifted);
                if (state.is(core.getRouterBlock())) {
                    state = state.setValue(MegablockRouter.FACING, i.getOpposite());
                    level.setBlockAndUpdate(shifted, state);
                    setRouterDirections(alreadyVisited, shape, core, corePos, level, shifted);
                }
            }
        }
    }
    public static void removeRouters(MegablockCore core, Rotation rotation, Level level, BlockPos pos) {
        MegablockShape shape = core.getMegablockShape().getRotated(rotation);
        for (Vec3i i : shape.shape) {
            if (level.getBlockState(pos.offset(i)).is(core.getRouterBlock())) {
                level.destroyBlock(pos.offset(i), true);
            }
        }
    }
}
