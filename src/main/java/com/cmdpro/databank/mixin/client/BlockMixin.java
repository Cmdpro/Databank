package com.cmdpro.databank.mixin.client;

import com.cmdpro.databank.hidden.types.BlockHiddenType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "getName", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getName(CallbackInfoReturnable<MutableComponent> cir) {
        Block block = BlockHiddenType.getHiddenBlockClient((Block)(Object)this);
        if (block != null) {
            cir.setReturnValue(BlockHiddenType.getHiddenBlockNameOverride(block).orElse(block.getName()).copy());
        }
    }
}
