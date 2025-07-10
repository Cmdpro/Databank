package com.cmdpro.databank.mixin.client;

import com.cmdpro.databank.hidden.types.ItemHiddenType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getName", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getName(ItemStack stack, CallbackInfoReturnable<MutableComponent> cir) {
        Item thisItem = (Item)(Object)this;
        Item item = ItemHiddenType.getHiddenItemClient(thisItem);
        if (item != null) {
            var isSameItem = item == stack.getItem();
            if (!isSameItem)
                cir.setReturnValue(ItemHiddenType.getHiddenItemNameOverride(thisItem).orElse(item.getName(stack)).copy());
            else {
                Optional<Component> override = ItemHiddenType.getHiddenItemNameOverride(thisItem);
                if (override.isPresent()) {
                    cir.setReturnValue(override.get().copy());
                }
            }
        }
    }
}
