package com.cmdpro.databank.hidden.types;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.hidden.*;
import com.cmdpro.databank.hidden.conditions.ActualPlayerCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.*;

public class BlockHiddenType extends HiddenTypeInstance.HiddenType<BlockHiddenType.BlockHiddenTypeInstance> {
    public static final BlockHiddenType INSTANCE = new BlockHiddenType();
    public static final MapCodec<BlockHiddenTypeInstance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(Registries.BLOCK).fieldOf("original").xmap(BuiltInRegistries.BLOCK::get, (i) -> BuiltInRegistries.BLOCK.getResourceKey(i).orElseThrow()).forGetter((type) -> type.original),
            ResourceKey.codec(Registries.BLOCK).fieldOf("hidden_as").xmap(BuiltInRegistries.BLOCK::get, (i) -> BuiltInRegistries.BLOCK.getResourceKey(i).orElseThrow()).forGetter((type) -> type.hiddenAs),
            ComponentSerialization.CODEC.optionalFieldOf("name_override").forGetter((type) -> type.nameOverride),
            HiddenSerializer.HIDDEN_CONDITION_CODEC.optionalFieldOf("drop_original_loot_condition", new ActualPlayerCondition()).forGetter((type) -> type.dropOriginalLootCondition),
            Codec.BOOL.optionalFieldOf("should_overwrite_loot_if_hidden", true).forGetter((type) -> type.shouldOverwriteLootIfHidden),
            BlockHiddenOverride.CODEC.codec().listOf().optionalFieldOf("overrides", new ArrayList<>()).forGetter((type) -> type.overrides)
    ).apply(instance, BlockHiddenTypeInstance::new));


    @Override
    public MapCodec<BlockHiddenTypeInstance> codec() {
        return CODEC;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockHiddenTypeInstance> STREAM_CODEC = StreamCodec.of((buf, val) -> {
        buf.writeResourceKey(BuiltInRegistries.BLOCK.getResourceKey(val.original).orElseThrow());
        buf.writeResourceKey(BuiltInRegistries.BLOCK.getResourceKey(val.hiddenAs).orElseThrow());
        buf.writeOptional(val.nameOverride, (buf2, val2) -> ComponentSerialization.STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf2, val2));
        buf.writeResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY.getResourceKey(val.dropOriginalLootCondition.getSerializer()).orElseThrow());
        val.dropOriginalLootCondition.getSerializer().streamCodec().encode(buf, val.dropOriginalLootCondition);
        buf.writeBoolean(val.shouldOverwriteLootIfHidden);
        buf.writeCollection(val.overrides, (buf2, override) -> BlockHiddenOverride.STREAM_CODEC.encode((RegistryFriendlyByteBuf)buf2, override));
    }, (buf) -> {
        ResourceKey<Block> originalKey = buf.readResourceKey(Registries.BLOCK);
        ResourceKey<Block> hiddenAsKey = buf.readResourceKey(Registries.BLOCK);
        Block original = BuiltInRegistries.BLOCK.get(originalKey);
        Block hiddenAs = BuiltInRegistries.BLOCK.get(hiddenAsKey);
        Optional<Component> nameOverride = buf.readOptional((buf2) -> ComponentSerialization.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf2));
        ResourceKey<HiddenCondition.Serializer<?>> dropConditionKey = buf.readResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY_KEY);
        HiddenCondition.Serializer<?> dropConditionSerializer = DatabankRegistries.HIDDEN_CONDITION_REGISTRY.get(dropConditionKey);
        HiddenCondition dropCondition = dropConditionSerializer.streamCodec().decode(buf);
        boolean shouldOverwriteLootIfHidden = buf.readBoolean();
        List<BlockHiddenOverride> overrides = buf.readList((buf2) -> BlockHiddenOverride.STREAM_CODEC.decode((RegistryFriendlyByteBuf)buf2));
        return new BlockHiddenTypeInstance(original, hiddenAs, nameOverride, dropCondition, shouldOverwriteLootIfHidden, overrides);
    });

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BlockHiddenTypeInstance> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public void updateClient() {
        ClientDatabankUtils.updateWorld();
    }

    public static Block getHiddenBlock(Block block, Player player) {
        return getHiddenBlock(block.defaultBlockState(), player);
    }
    public static Block getHiddenBlock(BlockState block, Player player) {
        for (Hidden i : HiddenManager.hidden.values()) {
            if (i.type instanceof BlockHiddenTypeInstance type) {
                if (type.original == null || type.hiddenAs == null || i.condition == null) {
                    continue;
                }
                if (type.isHidden(block.getBlock(), player)) {
                    BlockHiddenOverride override = findOverride(type, block);
                    return override != null ? override.hiddenAs : type.hiddenAs;
                } else if (type.matches(block.getBlock())) {
                    break;
                }
            }
        }
        return null;
    }
    public static boolean shouldDropOriginalBlock(Block block, Player player) {
        return shouldDropOriginalBlock(block.defaultBlockState(), player);
    }
    public static boolean shouldDropOriginalBlock(BlockState block, Player player) {
        for (Hidden i : HiddenManager.hidden.values()) {
            if (i.type instanceof BlockHiddenTypeInstance type) {
                if (type.original == null || type.hiddenAs == null || i.condition == null) {
                    continue;
                }
                if (type.isHidden(block.getBlock(), player) && type.shouldOverwriteLootIfHidden) {
                    return false;
                } else if (type.matches(block.getBlock())) {
                    BlockHiddenOverride override = findOverride(type, block);
                    return override != null ? override.dropOriginalLootCondition.isUnlocked(player) : type.dropOriginalLootCondition.isUnlocked(player);
                }
            }
        }
        return false;
    }

    public static Block getHiddenBlock(Block block) {
        return getHiddenBlock(block.defaultBlockState());
    }
    public static Block getHiddenBlock(BlockState block) {
        for (Hidden i : HiddenManager.hidden.values()) {
            if (i.type instanceof BlockHiddenTypeInstance type) {
                if (type.original == null || type.hiddenAs == null || i.condition == null) {
                    continue;
                }
                if (type.matches(block.getBlock())) {
                    BlockHiddenOverride override = findOverride(type, block);
                    return override != null ? override.hiddenAs : type.hiddenAs;
                }
            }
        }
        return null;
    }

    public static Optional<Component> getHiddenBlockNameOverride(Block block) {
        return getHiddenBlockNameOverride(block.defaultBlockState());
    }
    public static Optional<Component> getHiddenBlockNameOverride(BlockState block) {
        for (Hidden i : HiddenManager.hidden.values()) {
            if (i.type instanceof BlockHiddenTypeInstance type) {
                if (type.original == null) {
                    continue;
                }
                if (type.matches(block.getBlock())) {
                    BlockHiddenOverride override = findOverride(type, block);
                    return override != null ? override.nameOverride : type.nameOverride;
                }
            }
        }
        return Optional.empty();
    }

    public static BlockHiddenOverride findOverride(BlockHiddenTypeInstance instance, BlockState state) {
        for (BlockHiddenOverride j : instance.overrides) {
            if (j.predicate.matches(state)) {
                return j;
            }
        }
        return null;
    }
    public static Block getHiddenBlockClient(Block block) {
        return getHiddenBlockClient(block.defaultBlockState());
    }
    public static Block getHiddenBlockClient(BlockState block) {
        for (Map.Entry<ResourceLocation, Hidden> i : HiddenManager.hidden.entrySet()) {
            if (i.getValue().type instanceof BlockHiddenTypeInstance type) {
                if (type.original == null || type.hiddenAs == null) {
                    continue;
                }
                if (type.isHiddenClient(block.getBlock())) {
                    BlockHiddenOverride override = findOverride(type, block);
                    return override != null ? override.hiddenAs : type.hiddenAs;
                } else if (type.matches(block.getBlock())) {
                    break;
                }
            }
        }
        return null;
    }
    public static class BlockHiddenTypeInstance extends HiddenTypeInstance<Block> {
        public Block original;
        public Block hiddenAs;
        public Optional<Component> nameOverride;
        public HiddenCondition dropOriginalLootCondition;
        public boolean shouldOverwriteLootIfHidden;
        public List<BlockHiddenOverride> overrides;
        public BlockHiddenTypeInstance(Block original, Block hiddenAs, Optional<Component> nameOverride, HiddenCondition dropOriginalLootCondition, boolean shouldOverwiteLootIfHidden, List<BlockHiddenOverride> overrides) {
            this.original = original;
            this.hiddenAs = hiddenAs;
            this.nameOverride = nameOverride;
            this.dropOriginalLootCondition = dropOriginalLootCondition;
            this.shouldOverwriteLootIfHidden = shouldOverwiteLootIfHidden;
            this.overrides = overrides;
        }

        @Override
        public boolean matches(Block obj) {
            return obj.equals(original);
        }

        @Override
        public HiddenType<? extends HiddenTypeInstance<Block>> getType() {
            return INSTANCE;
        }
    }
    public static class BlockHiddenOverride {
        public final StatePropertiesPredicate predicate;
        public Block hiddenAs;
        public Optional<Component> nameOverride;
        public HiddenCondition dropOriginalLootCondition;
        public boolean shouldOverwriteLootIfHidden;
        public BlockHiddenOverride(StatePropertiesPredicate predicate, Block hiddenAs, Optional<Component> nameOverride, HiddenCondition dropOriginalLootCondition, boolean shouldOverwiteLootIfHidden) {
            this.predicate = predicate;
            this.hiddenAs = hiddenAs;
            this.nameOverride = nameOverride;
            this.dropOriginalLootCondition = dropOriginalLootCondition;
            this.shouldOverwriteLootIfHidden = shouldOverwiteLootIfHidden;
        }
        public static final StreamCodec<RegistryFriendlyByteBuf, BlockHiddenOverride> STREAM_CODEC = StreamCodec.of((buf, val) -> {
            StatePropertiesPredicate.STREAM_CODEC.encode(buf, val.predicate);
            buf.writeResourceKey(BuiltInRegistries.BLOCK.getResourceKey(val.hiddenAs).orElseThrow());
            buf.writeOptional(val.nameOverride, (buf2, val2) -> ComponentSerialization.STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf2, val2));
            buf.writeResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY.getResourceKey(val.dropOriginalLootCondition.getSerializer()).orElseThrow());
            val.dropOriginalLootCondition.getSerializer().streamCodec().encode(buf, val.dropOriginalLootCondition);
            buf.writeBoolean(val.shouldOverwriteLootIfHidden);
        }, (buf) -> {
            StatePropertiesPredicate predicate = StatePropertiesPredicate.STREAM_CODEC.decode(buf);
            ResourceKey<Block> hiddenAsKey = buf.readResourceKey(Registries.BLOCK);
            Block hiddenAs = BuiltInRegistries.BLOCK.get(hiddenAsKey);
            Optional<Component> nameOverride = buf.readOptional((buf2) -> ComponentSerialization.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf2));
            ResourceKey<HiddenCondition.Serializer<?>> dropConditionKey = buf.readResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY_KEY);
            HiddenCondition.Serializer<?> dropConditionSerializer = DatabankRegistries.HIDDEN_CONDITION_REGISTRY.get(dropConditionKey);
            HiddenCondition dropCondition = dropConditionSerializer.streamCodec().decode(buf);
            boolean shouldOverwriteLootIfHidden = buf.readBoolean();
            return new BlockHiddenOverride(predicate, hiddenAs, nameOverride, dropCondition, shouldOverwriteLootIfHidden);
        });
        public static final MapCodec<BlockHiddenOverride> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StatePropertiesPredicate.CODEC.fieldOf("predicate").forGetter((type) -> type.predicate),
                ResourceKey.codec(Registries.BLOCK).fieldOf("hidden_as").xmap(BuiltInRegistries.BLOCK::get, (i) -> BuiltInRegistries.BLOCK.getResourceKey(i).orElseThrow()).forGetter((type) -> type.hiddenAs),
                ComponentSerialization.CODEC.optionalFieldOf("name_override").forGetter((type) -> type.nameOverride),
                HiddenSerializer.HIDDEN_CONDITION_CODEC.optionalFieldOf("drop_original_loot_condition", new ActualPlayerCondition()).forGetter((type) -> type.dropOriginalLootCondition),
                Codec.BOOL.optionalFieldOf("should_overwrite_loot_if_hidden", true).forGetter((type) -> type.shouldOverwriteLootIfHidden)
        ).apply(instance, BlockHiddenOverride::new));
    }
}
