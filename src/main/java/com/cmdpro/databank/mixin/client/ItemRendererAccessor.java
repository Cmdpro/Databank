package com.cmdpro.databank.mixin.client;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {
    @Accessor
    static ModelResourceLocation getTRIDENT_MODEL() {
        throw new UnsupportedOperationException();
    }
    @Accessor
    ItemColors getItemColors();

    @Accessor
    static ModelResourceLocation getSPYGLASS_MODEL() {
        throw new UnsupportedOperationException();
    }

    @Invoker
    static boolean callHasAnimatedTexture(ItemStack pStack) {
        throw new UnsupportedOperationException();
    }
}
