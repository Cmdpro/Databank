package com.cmdpro.databank.dialogue;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class DialogueTree {
    public ResourceLocation id;
    public Map<String, DialogueEntry> entries;
    public Map<String, DialogueSpeaker> speakers;
    public static final Codec<DialogueTree> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
            Codec.unboundedMap(Codec.STRING, DialogueEntry.CODEC).fieldOf("entries").forGetter((obj) -> obj.entries),
            Codec.unboundedMap(Codec.STRING, DialogueSpeaker.CODEC.codec()).fieldOf("speakers").forGetter((obj) -> obj.speakers)
    ).apply(builder, DialogueTree::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueTree> STREAM_CODEC = StreamCodec.of((buf, obj) -> {
        buf.writeMap(obj.entries, FriendlyByteBuf::writeUtf, (buf2, obj2) -> DialogueEntry.STREAM_CODEC.encode((RegistryFriendlyByteBuf)buf2, obj2));
        buf.writeMap(obj.speakers, FriendlyByteBuf::writeUtf, (buf2, obj2) -> DialogueSpeaker.STREAM_CODEC.encode((RegistryFriendlyByteBuf)buf2, obj2));
    }, (buf) -> {
        Map<String, DialogueEntry> entries = buf.readMap(FriendlyByteBuf::readUtf, (buf2) -> DialogueEntry.STREAM_CODEC.decode((RegistryFriendlyByteBuf)buf2));
        Map<String, DialogueSpeaker> speakers = buf.readMap(FriendlyByteBuf::readUtf, (buf2) -> DialogueSpeaker.STREAM_CODEC.decode((RegistryFriendlyByteBuf)buf2));
        return new DialogueTree(entries, speakers);
    });
    public DialogueTree(Map<String, DialogueEntry> entries, Map<String, DialogueSpeaker> speakers) {
        this.entries = entries;
        for (Map.Entry<String, DialogueEntry> i : entries.entrySet()) {
            i.getValue().tree = this;
            i.getValue().id = i.getKey();
        }
        this.speakers = speakers;
    }
    public DialogueInstance createInstance(String entry) {
        return new DialogueInstance(this, entries.get(entry));
    }
    public DialogueInstance open(Player player, String entry) {
        DialogueInstance instance = createInstance(entry);
        instance.setForPlayer(player);
        return instance;
    }
}
