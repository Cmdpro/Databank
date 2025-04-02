package com.cmdpro.databank.mixin.client;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.DatabankUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin {
    @Inject(method = "getRenderLayers", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void getRenderLayers(BlockState pState, CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
        Block block = ClientDatabankUtils.getHiddenBlock(pState.getBlock());
        if (block != null) {
            cir.setReturnValue(ItemBlockRenderTypes.getRenderLayers(DatabankUtils.changeBlockType(pState, block)));
        }
    }
}
