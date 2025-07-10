package com.cmdpro.databank.mixin.client;

import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.hidden.types.BlockHiddenType;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(BlockColors.class)
public abstract class BlockColorsMixin {

    @Shadow @Final private Map<Block, BlockColor> blockColors;

    @Shadow @Final private Map<Block, Set<Property<?>>> coloringStates;

    @Inject(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)I", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getColor(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (state != null) {
            Block block = BlockHiddenType.getHiddenBlockClient(state.getBlock());
            if ((block != null) && (block != state.getBlock())) {
                BlockState state2 = DatabankUtils.changeBlockType(state, block);
                BlockColor blockcolor = blockColors.get(state2.getBlock());
                if (blockcolor != null) {
                    cir.setReturnValue(blockcolor.getColor(state2, null, null, 0));
                } else {
                    MapColor mapcolor = state2.getMapColor(level, pos);
                    cir.setReturnValue(mapcolor != null ? mapcolor.col : -1);
                }
            }
        }
    }
    @Inject(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        if (state != null) {
            Block block = BlockHiddenType.getHiddenBlockClient(state.getBlock());
            if ((block != null) && (block != state.getBlock())) {
                BlockState state2 = DatabankUtils.changeBlockType(state, block);
                BlockColor blockcolor = this.blockColors.get(state2.getBlock());
                cir.setReturnValue(blockcolor == null ? -1 : blockcolor.getColor(state2, level, pos, tintIndex));
            }
        }
    }
    @Inject(method = "getColoringProperties", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getColoringProperties(Block block, CallbackInfoReturnable<Set<Property<?>>> cir) {
        if (block != null) {
            Block block2 = BlockHiddenType.getHiddenBlockClient(block);
            if ((block2 != null) && (block2 != block)) {
                cir.setReturnValue(coloringStates.getOrDefault(block2, ImmutableSet.of()));
            }
        }
    }
}
