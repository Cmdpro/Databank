package com.cmdpro.databank.mixin.client;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.DatabankUtils;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(BlockModelShaper.class)
public class BlockModelShaperMixin {
    @Shadow
    private Map<BlockState, BakedModel> modelByStateCache;
    @Inject(method = "getBlockModel", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getBlockModel(BlockState pState, CallbackInfoReturnable<BakedModel> cir) {
        Block block = ClientDatabankUtils.getHiddenBlock(pState.getBlock());
        if (block != null) {
            cir.setReturnValue(modelByStateCache.get(DatabankUtils.changeBlockType(pState, block)));
        }
    }
}
