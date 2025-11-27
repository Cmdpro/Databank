package com.cmdpro.databank.dialogue;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DialogueEntry {
    public DialogueTree tree;
    public String id;

    public Component text;
    public String speaker;
    public List<DialogueChoice> choices;
    public ResourceLocation style;
    public double speed;
    public Optional<Integer> closeMenuChoice;
    public DialogueEntry(Component text, String speaker, List<DialogueChoice> choices, ResourceLocation style, double speed, Optional<Integer> closeMenuChoice) {
        this.text = text;
        this.speaker = speaker;
        this.choices = choices;
        this.style = style;
        this.speed = speed;
        this.closeMenuChoice = closeMenuChoice;
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
            ResourceLocation.CODEC.fieldOf("style").forGetter((obj) -> obj.style),
            Codec.DOUBLE.optionalFieldOf("speed", 1d).forGetter((obj) -> obj.speed),
            Codec.INT.optionalFieldOf("closeMenuChoice").forGetter((obj) -> obj.closeMenuChoice)
    ).apply(builder, DialogueEntry::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueEntry> STREAM_CODEC = StreamCodec.of((buf, obj) -> {
        ComponentSerialization.STREAM_CODEC.encode(buf, obj.text);
        buf.writeUtf(obj.speaker);
        buf.writeCollection(obj.choices, (buf2, obj2) -> DialogueChoice.STREAM_CODEC.encode((RegistryFriendlyByteBuf)buf2, obj2));
        buf.writeResourceLocation(obj.style);
        buf.writeDouble(obj.speed);
        buf.writeOptional(obj.closeMenuChoice, FriendlyByteBuf::writeInt);
    }, (buf) -> {
        Component text = ComponentSerialization.STREAM_CODEC.decode(buf);
        String speaker = buf.readUtf();
        List<DialogueChoice> choices = buf.readList((buf2) -> DialogueChoice.STREAM_CODEC.decode((RegistryFriendlyByteBuf)buf2));
        ResourceLocation style = buf.readResourceLocation();
        double speed = buf.readDouble();
        Optional<Integer> closeMenuChoice = buf.readOptional(FriendlyByteBuf::readInt);
        return new DialogueEntry(text, speaker, choices, style, speed, closeMenuChoice);
    });
}
