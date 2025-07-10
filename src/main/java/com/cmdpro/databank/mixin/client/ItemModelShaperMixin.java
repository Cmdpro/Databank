package com.cmdpro.databank.mixin.client;

import com.cmdpro.databank.hidden.types.ItemHiddenType;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemModelShaper.class)
public class ItemModelShaperMixin {
    @Inject(method = "getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void getItemModel(ItemStack stack, CallbackInfoReturnable<BakedModel> cir) {
        Item hiddenItem = ItemHiddenType.getHiddenItemClient(stack.getItem());
        if ((hiddenItem != null) && (hiddenItem != stack.getItem())) {
            ItemModelShaper shaper = (ItemModelShaper)(Object)this;
            ItemStack stack2 = new ItemStack(Holder.direct(hiddenItem), stack.getCount(), stack.getComponentsPatch());
            cir.setReturnValue(shaper.getItemModel(stack2));
        }
    }
}
