package com.cmdpro.databank.mixin.client;

import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.hidden.types.BlockHiddenType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockModelShaper.class)
public class BlockModelShaperMixin {
    @Inject(method = "getBlockModel", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getBlockModel(BlockState pState, CallbackInfoReturnable<BakedModel> cir) {
        Block block = BlockHiddenType.getHiddenBlockClient(pState.getBlock());
        if (block != null) {
            BlockModelShaper shaper = (BlockModelShaper)(Object)this;
            cir.setReturnValue(shaper.getBlockModel(DatabankUtils.changeBlockType(pState, block)));
        }
    }
}
