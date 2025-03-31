package com.cmdpro.databank.multiblock;

import com.cmdpro.databank.DatabankRegistries;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.*;
import java.util.stream.Collectors;

public class MultiblockSerializer {
    public Multiblock read(ResourceLocation entryId, JsonObject json) {
        return ICondition.getWithWithConditionsCodec(CODEC, JsonOps.INSTANCE, json).orElse(null);
    }
    public static final Codec<MultiblockPredicate> PREDICATE_CODEC = DatabankRegistries.MULTIBLOCK_PREDICATE_REGISTRY.byNameCodec().dispatch(MultiblockPredicate::getSerializer, pageSerializer -> pageSerializer.getCodec());
    public static final StreamCodec<RegistryFriendlyByteBuf, MultiblockPredicate> PREDICATE_STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeResourceLocation(DatabankRegistries.MULTIBLOCK_PREDICATE_REGISTRY.getKey(pValue.getSerializer()));
        pValue.getSerializer().getStreamCodec().encode(pBuffer, pValue);
    }, pBuffer -> {
        ResourceLocation type = pBuffer.readResourceLocation();
        MultiblockPredicateSerializer pageSerializer = DatabankRegistries.MULTIBLOCK_PREDICATE_REGISTRY.get(type);
        MultiblockPredicate predicate = (MultiblockPredicate)pageSerializer.getStreamCodec().decode(pBuffer);
        return predicate;
    });
    public static final MapCodec<Multiblock> ORIGINAL_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.unboundedMap(Codec.STRING, PREDICATE_CODEC).fieldOf("key").forGetter((multiblock) -> {
                Map<String, MultiblockPredicate> map = multiblock.key.entrySet().stream().map((a) -> Map.entry(a.getKey().toString(), a.getValue())).collect(Collectors.toMap((a) -> a.getKey(), (a) -> a.getValue()));
                return map;
            }),
            Codec.STRING.listOf().listOf().fieldOf("layers").forGetter((multiblock) -> multiblock.getMultiblockLayersList()),
            BlockPos.CODEC.fieldOf("center").forGetter((multiblock) -> multiblock.center)
    ).apply(instance, (key, layers, offset) -> {
        Map<Character, MultiblockPredicate> key2 = key.entrySet().stream().map((a) -> Map.entry(a.getKey().charAt(0), a.getValue())).collect(Collectors.toMap((a) -> a.getKey(), (a) -> a.getValue()));
        return new Multiblock(layers.stream().map((a) -> a.toArray(new String[0])).toList().toArray(new String[0][]), key2, offset);
    }));
    public static final Codec<Optional<WithConditions<Multiblock>>> CODEC = ConditionalOps.createConditionalCodecWithConditions(ORIGINAL_CODEC.codec());
    public static final StreamCodec<RegistryFriendlyByteBuf, Multiblock> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeMap(pValue.key, (a, b) -> a.writeChar(b), (a, b) -> PREDICATE_STREAM_CODEC.encode((RegistryFriendlyByteBuf) a, b));
        pBuffer.writeBlockPos(pValue.center);
        List<List<String>> layers = new ArrayList<>();
        for (String[] i : pValue.multiblockLayers) {
            layers.add(List.of(i));
        }
        pBuffer.writeCollection(layers, (a, b) -> {
            a.writeCollection(b, FriendlyByteBuf::writeUtf);
        });
    }, pBuffer -> {
        HashMap<Character, MultiblockPredicate> key = new HashMap<>(pBuffer.readMap((a) -> a.readChar(), (a) -> PREDICATE_STREAM_CODEC.decode(pBuffer)));
        BlockPos offset = pBuffer.readBlockPos();
        List<String[]> layers = pBuffer.readList((a) -> a.readList(FriendlyByteBuf::readUtf).toArray(new String[0]));
        return new Multiblock(layers.toArray(new String[0][]), key, offset);
    });
}