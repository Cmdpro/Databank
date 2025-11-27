package com.cmdpro.databank.rendering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public record SpriteData(ResourceLocation texture, int u, int v, int width, int height) {
    public static final Codec<SpriteData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter((data) -> data.texture),
            Codec.INT.fieldOf("u").forGetter((data) -> data.u),
            Codec.INT.fieldOf("v").forGetter((data) -> data.v),
            Codec.INT.fieldOf("width").forGetter((data) -> data.width),
            Codec.INT.fieldOf("height").forGetter((data) -> data.height)
    ).apply(instance, SpriteData::new));
    public void blit(GuiGraphics graphics, int x, int y) {
        graphics.blit(texture, x, y, u, v, width, height);
    }
}
