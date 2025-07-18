package com.cmdpro.databank.mixin;

import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.hidden.types.BlockHiddenType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Inject(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getShape(BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof EntityCollisionContext entityShapeContext) {
            Entity contextEntity = entityShapeContext.getEntity();
            if (contextEntity instanceof Player player) {
                if ((Object)this instanceof BlockState state) {
                    if (player.level().isClientSide) {
                        Block block = BlockHiddenType.getHiddenBlockClient(state.getBlock(), BlockHiddenType.getProperties(state));
                        if (block != null && block != state.getBlock()) {
                            cir.setReturnValue(DatabankUtils.changeBlockType(state, block).getShape(level, pos, context));
                        }
                    } else {
                        Block block = BlockHiddenType.getHiddenBlock(state.getBlock(), BlockHiddenType.getProperties(state), player);
                        if (block != null && block != state.getBlock()) {
                            cir.setReturnValue(DatabankUtils.changeBlockType(state, block).getShape(level, pos, context));
                        }
                    }
                }
            }
        }
    }
}
