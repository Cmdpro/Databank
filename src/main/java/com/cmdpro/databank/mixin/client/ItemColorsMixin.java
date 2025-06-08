package com.cmdpro.databank.mixin.client;

import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.hidden.types.BlockHiddenType;
import com.cmdpro.databank.hidden.types.ItemHiddenType;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(ItemColors.class)
public abstract class ItemColorsMixin {
    @Inject(method = "getColor", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void getColor(ItemStack stack, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        if (stack != null) {
            Item item = ItemHiddenType.getHiddenItemClient(stack.getItem());
            if (item != null) {
                ItemColors itemColors = (ItemColors)(Object)this;
                ItemStack stack2 = new ItemStack(Holder.direct(item), stack.getCount(), stack.getComponentsPatch());
                cir.setReturnValue(itemColors.getColor(stack2, tintIndex));
            }
        }
    }
}
