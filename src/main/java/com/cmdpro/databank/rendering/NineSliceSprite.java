package com.cmdpro.databank.rendering;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.dialogue.styles.BasicDialogueStyle;
import com.cmdpro.databank.model.DatabankPartData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public record NineSliceSprite(ResourceLocation texture, int u, int v, int width, int height, int top, int bottom, int left, int right, int defaultInset) {
    public int getHorizontal() {
        return left + right;
    }

    public int getVertical() {
        return top + bottom;
    }
    public int getHorizontalInset(int inset) {
        return (left + right)-(inset*2);
    }

    public int getVerticalInset(int inset) {
        return (top + bottom)-(inset*2);
    }
    public int getHorizontalInset() {
        return getHorizontalInset(defaultInset);
    }

    public int getVerticalInset() {
        return getVerticalInset(defaultInset);
    }

    public void blit(GuiGraphics graphics, int x, int y, int width, int height) {
        blit(graphics, x, y, width, height, defaultInset);
    }
    public void blit(GuiGraphics graphics, int x, int y, int width, int height, int inset) {
        blit(graphics, this.texture, x, y, this.u, this.v, width, height, this.width, this.height, this.top, this.bottom, this.left, this.right, inset);
    }
    public static void blit(GuiGraphics graphics, ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, int inset) {
        graphics.blit(texture, (x-leftBorder)+inset, (y-topBorder)+inset, u, v, leftBorder, topBorder);
        graphics.blit(texture, (x+width)-inset, (y-topBorder)+inset, (u+textureWidth)-rightBorder, v, rightBorder, topBorder);

        graphics.blit(texture, (x-leftBorder)+inset, (y+height)-inset, u, (v+textureHeight)-bottomBorder, leftBorder, bottomBorder);
        graphics.blit(texture, (x+width)-inset, (y+height)-inset, (u+textureWidth)-rightBorder, (v+textureHeight)-bottomBorder, rightBorder, bottomBorder);

        ClientDatabankUtils.blitStretched(graphics, texture, x+inset, (y-topBorder)+inset, u+leftBorder, v, textureWidth-(leftBorder+rightBorder), topBorder, width-(inset*2), topBorder);
        ClientDatabankUtils.blitStretched(graphics, texture, x+inset, (y+height)-inset, u+leftBorder, v+(textureHeight-bottomBorder), textureWidth-(leftBorder+rightBorder), bottomBorder, width-(inset*2), bottomBorder);
        ClientDatabankUtils.blitStretched(graphics, texture, (x-leftBorder)+inset, y+inset, u, v+topBorder, leftBorder, textureHeight-(topBorder+bottomBorder), leftBorder, height-(inset*2));
        ClientDatabankUtils.blitStretched(graphics, texture, (x+width)-inset, y+inset, u+(textureWidth-rightBorder), v+topBorder, rightBorder, textureHeight-(topBorder+bottomBorder), rightBorder, height-(inset*2));

        ClientDatabankUtils.blitStretched(graphics, texture, x+inset, y+inset, u+leftBorder, v+topBorder, textureWidth-(leftBorder+rightBorder), textureHeight-(topBorder+bottomBorder), width-(inset*2), height-(inset*2));
    }
    public static final Codec<NineSliceSprite> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter((data) -> data.texture),
            Codec.INT.fieldOf("u").forGetter((data) -> data.u),
            Codec.INT.fieldOf("v").forGetter((data) -> data.v),
            Codec.INT.fieldOf("width").forGetter((data) -> data.width),
            Codec.INT.fieldOf("height").forGetter((data) -> data.height),
            Codec.INT.fieldOf("top").forGetter((data) -> data.top),
            Codec.INT.fieldOf("bottom").forGetter((data) -> data.bottom),
            Codec.INT.fieldOf("left").forGetter((data) -> data.left),
            Codec.INT.fieldOf("right").forGetter((data) -> data.right),
            Codec.INT.optionalFieldOf("defaultInset", 0).forGetter((data) -> data.defaultInset)
    ).apply(instance, NineSliceSprite::new));
}