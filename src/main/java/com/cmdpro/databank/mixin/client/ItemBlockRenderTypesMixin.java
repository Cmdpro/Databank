package com.cmdpro.databank.mixin.client;

import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.hidden.types.BlockHiddenType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin {
    @Inject(method = "getRenderLayers", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void getRenderLayers(BlockState pState, CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
        Block block = BlockHiddenType.getHiddenBlockClient(pState);
        if ((block != null) && (block != pState.getBlock())) {
            cir.setReturnValue(ItemBlockRenderTypes.getRenderLayers(DatabankUtils.changeBlockType(pState, block)));
        }
    }
}
