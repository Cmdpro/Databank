package com.cmdpro.databank.multiblock.predicates.serializers;

import com.cmdpro.databank.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.databank.multiblock.predicates.AnyMultiblockPredicate;
import com.cmdpro.databank.multiblock.predicates.TagMultiblockPredicate;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class AnyMultiblockPredicateSerializer extends MultiblockPredicateSerializer<AnyMultiblockPredicate> {
    public static final AnyMultiblockPredicateSerializer INSTANCE = new AnyMultiblockPredicateSerializer();
    public static TagKey<Block> getTagFromString(String str) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(str));
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, AnyMultiblockPredicate> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {}, (pBuffer) -> new AnyMultiblockPredicate());
    public static final MapCodec<AnyMultiblockPredicate> CODEC = MapCodec.assumeMapUnsafe(Codec.unit(AnyMultiblockPredicate::new));
    @Override
    public MapCodec<AnyMultiblockPredicate> getCodec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AnyMultiblockPredicate> getStreamCodec() {
        return STREAM_CODEC;
    }
}
