package com.cmdpro.databank.dialogue;

import com.cmdpro.databank.dialogue.styles.BasicDialogueStyle;
import com.cmdpro.databank.rendering.NineSliceSprite;
import com.cmdpro.databank.rendering.SpriteData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class DialogueSpeaker {
    public static final MapCodec<DialogueSpeaker> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("portrait").forGetter((obj) -> obj.portrait),
            ComponentSerialization.CODEC.fieldOf("name").forGetter((obj) -> obj.name)
    ).apply(instance, DialogueSpeaker::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueSpeaker> STREAM_CODEC = StreamCodec.of((buf, obj) -> {
        buf.writeResourceLocation(obj.portrait);
        ComponentSerialization.STREAM_CODEC.encode(buf, obj.name);
    }, (buf) -> {
        ResourceLocation portrait = buf.readResourceLocation();
        Component name = ComponentSerialization.STREAM_CODEC.decode(buf);
        return new DialogueSpeaker(portrait, name);
    });
    public ResourceLocation portrait;
    public Component name;
    public DialogueSpeaker(ResourceLocation portrait, Component name) {
        this.portrait = portrait;
        this.name = name;
    }
}
