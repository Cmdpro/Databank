package com.cmdpro.databank.dialogue;

import com.cmdpro.databank.DatabankRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class DialogueChoice {
    public static final Codec<DialogueChoice> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            ComponentSerialization.CODEC.fieldOf("text").forGetter((obj) -> obj.text),
            DialogueChoiceAction.CODEC.listOf().fieldOf("actions").forGetter((obj) -> obj.actions)
    ).apply(builder, DialogueChoice::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueChoice> STREAM_CODEC = StreamCodec.of((buf, obj) -> {
        ComponentSerialization.STREAM_CODEC.encode(buf, obj.text);
        buf.writeCollection(obj.actions, (buf2, obj2) -> {
            buf.writeResourceLocation(DatabankRegistries.DIALOGUE_CHOICE_ACTION_REGISTRY.getKey(obj2.getCodecs()));
            obj2.getStreamCodec().encode((RegistryFriendlyByteBuf)buf2, obj2);
        });
    }, (buf) -> {
        Component text = ComponentSerialization.STREAM_CODEC.decode(buf);
        List<DialogueChoiceAction> actions = buf.readList((buf2) -> {
            ResourceLocation dialogueActionType = buf.readResourceLocation();
            return DatabankRegistries.DIALOGUE_CHOICE_ACTION_REGISTRY.get(dialogueActionType).streamCodec().decode((RegistryFriendlyByteBuf)buf2);
        });
        return new DialogueChoice(text, actions);
    });
    public DialogueChoice(Component text, List<DialogueChoiceAction> actions) {
        this.text = text;
        this.actions = actions;
    }
    public DialogueEntry entry;
    public Component text;
    public List<DialogueChoiceAction> actions;
    public void onClick(Player player, DialogueInstance instance, DialogueChoice choice) {
        for (DialogueChoiceAction i : actions) {
            i.onClick(player, instance, choice);
        }
    }
}
