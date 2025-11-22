package com.cmdpro.databank.dialogue;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class DialogueEntry {
    public DialogueTree tree;
    public String id;

    public Component text;
    public String speaker;
    public List<DialogueChoice> choices;
    public ResourceLocation style;
    public DialogueEntry(Component text, String speaker, List<DialogueChoice> choices, ResourceLocation style) {
        this.text = text;
        this.speaker = speaker;
        this.choices = choices;
        this.style = style;
        for (DialogueChoice i : choices) {
            i.entry = this;
        }
    }
    public DialogueSpeaker getSpeaker() {
        return tree.speakers.get(speaker);
    }
    public static final Codec<DialogueEntry> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            ComponentSerialization.CODEC.fieldOf("text").forGetter((obj) -> obj.text),
            Codec.STRING.fieldOf("speaker").forGetter((obj) -> obj.speaker),
            DialogueChoice.CODEC.listOf().fieldOf("choices").forGetter((obj) -> obj.choices),
            ResourceLocation.CODEC.fieldOf("style").forGetter((obj) -> obj.style)
    ).apply(builder, DialogueEntry::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueEntry> STREAM_CODEC = StreamCodec.of((buf, obj) -> {
        ComponentSerialization.STREAM_CODEC.encode(buf, obj.text);
        buf.writeUtf(obj.speaker);
        buf.writeCollection(obj.choices, (buf2, obj2) -> DialogueChoice.STREAM_CODEC.encode((RegistryFriendlyByteBuf)buf2, obj2));
        buf.writeResourceLocation(obj.style);
    }, (buf) -> {
        Component text = ComponentSerialization.STREAM_CODEC.decode(buf);
        String speaker = buf.readUtf();
        List<DialogueChoice> choices = buf.readList((buf2) -> DialogueChoice.STREAM_CODEC.decode((RegistryFriendlyByteBuf)buf2));
        ResourceLocation style = buf.readResourceLocation();
        return new DialogueEntry(text, speaker, choices, style);
    });
}
