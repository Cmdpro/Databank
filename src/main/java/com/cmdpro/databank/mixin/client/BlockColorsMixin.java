package com.cmdpro.databank.mixin.client;

import com.cmdpro.databank.ClientDatabankUtils;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(BlockColors.class)
public abstract class BlockColorsMixin {
    @Shadow public abstract int getColor(BlockState state, Level level, BlockPos pos);

    @Shadow public abstract int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex);

    @Shadow public abstract Set<Property<?>> getColoringProperties(Block block);

    @Inject(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)I", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getColor(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (state != null) {
            BlockState state2 = ClientDatabankUtils.getHiddenBlock(state.getBlock());
            if (state2 != null) {
                cir.setReturnValue(getColor(state2, level, pos));
            }
        }
    }
    @Inject(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        if (state != null) {
            BlockState state2 = ClientDatabankUtils.getHiddenBlock(state.getBlock());
            if (state2 != null) {
                cir.setReturnValue(getColor(state2, level, pos, tintIndex));
            }
        }
    }
    @Inject(method = "getColoringProperties", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getColoringProperties(Block block, CallbackInfoReturnable<Set<Property<?>>> cir) {
        if (block != null) {
            BlockState state2 = ClientDatabankUtils.getHiddenBlock(block);
            if (state2 != null) {
                cir.setReturnValue(getColoringProperties(state2.getBlock()));
            }
        }
    }
}
