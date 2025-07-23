package com.cmdpro.databank.mixin.client.compat.sodium;

import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.hidden.types.BlockHiddenType;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.color.ColorProviderRegistry;
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
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(ColorProviderRegistry.class)
public abstract class ColorProviderRegistryMixin {

    @Final
    @Shadow
    private Reference2ReferenceMap<Block, ColorProvider<BlockState>> blocks;

    @Inject(method = "getColorProvider(Lnet/minecraft/world/level/block/Block;)Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getColor(Block block, CallbackInfoReturnable<ColorProvider<BlockState>> cir) {
        if (block != null) {
            BlockState state = block.defaultBlockState();
            Block hiddenBlock = BlockHiddenType.getHiddenBlockClient(state);
            if ((hiddenBlock != null) && (hiddenBlock != state.getBlock())) {
                BlockState state2 = DatabankUtils.changeBlockType(state, hiddenBlock);
                ColorProvider<BlockState> blockcolor = blocks.get(state2.getBlock());
                if (blockcolor != null) {
                    cir.setReturnValue(blockcolor);
                }
            }
        }
    }
}
