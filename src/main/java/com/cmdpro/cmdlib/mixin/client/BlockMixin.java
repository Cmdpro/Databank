package com.cmdpro.cmdlib.mixin.client;

import com.cmdpro.cmdlib.ClientCmdLibUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "getName", at = @At(value = "HEAD"), cancellable = true)
    public void getName(CallbackInfoReturnable<MutableComponent> cir) {
        BlockState state = ClientCmdLibUtils.getHiddenBlock((Block)(Object)this);
        if (state != null) {
            cir.setReturnValue(state.getBlock().getName());
        }
    }
}
