package com.cmdpro.databank.mixin.client;

import com.cmdpro.databank.hidden.types.BlockHiddenType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Shadow protected abstract Block asBlock();

    @Inject(method = "getName", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getName(CallbackInfoReturnable<MutableComponent> cir) {
        Block block = BlockHiddenType.getHiddenBlockClient((Block)(Object)this);
        if (block != null) {
            if (block != this.asBlock())
                cir.setReturnValue(BlockHiddenType.getHiddenBlockNameOverride(block).orElse(block.getName()).copy());
            else {
                Optional<Component> override = BlockHiddenType.getHiddenBlockNameOverride(this.asBlock());
                if (override.isPresent()) {
                    cir.setReturnValue(override.get().copy());
                }
            }
        }
    }
}
