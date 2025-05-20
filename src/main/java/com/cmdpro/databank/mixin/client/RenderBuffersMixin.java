package com.cmdpro.databank.mixin.client;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderBuffers.class)
public interface RenderBuffersMixin {
    //For working around iris
    @Accessor("bufferSource")
    public MultiBufferSource.BufferSource getBufferSource();
}
