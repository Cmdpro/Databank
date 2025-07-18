package com.cmdpro.databank.hidden.types;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.hidden.*;
import com.cmdpro.databank.hidden.conditions.ActualPlayerCondition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

    public static Block getHiddenBlock(Block block, String[] properties, Player player) {
        for (Hidden i : HiddenManager.hidden.values()) {
            if (i.type instanceof BlockHiddenTypeInstance type) {
                if (type.original == null || type.hiddenAs == null || i.condition == null) {
                    continue;
                }
                if (type.isHidden(block, player)) {
                    BlockHiddenOverride override = findOverride(type, properties);
                    return override != null ? override.hiddenAs : type.hiddenAs;
                } else if (type.matches(block)) {
                    break;
                }
            }
        }
        return null;
    }
    public static boolean shouldDropOriginalBlock(Block block, String[] properties, Player player) {
        for (Hidden i : HiddenManager.hidden.values()) {
            if (i.type instanceof BlockHiddenTypeInstance type) {
                if (type.original == null || type.hiddenAs == null || i.condition == null) {
                    continue;
                }
                if (type.isHidden(block, player) && type.shouldOverwriteLootIfHidden) {
                    return false;
                } else if (type.matches(block)) {
                    BlockHiddenOverride override = findOverride(type, properties);
                    return override != null ? override.dropOriginalLootCondition.isUnlocked(player) : type.dropOriginalLootCondition.isUnlocked(player);
                }
            }
        }
        return false;
    }

    public static Block getHiddenBlock(Block block, String[] properties) {
        for (Hidden i : HiddenManager.hidden.values()) {
            if (i.type instanceof BlockHiddenTypeInstance type) {
                if (type.original == null || type.hiddenAs == null || i.condition == null) {
                    continue;
                }
                if (type.matches(block)) {
                    BlockHiddenOverride override = findOverride(type, properties);
                    return override != null ? override.hiddenAs : type.hiddenAs;
                }
            }
        }
        return null;
    }

    public static Optional<Component> getHiddenBlockNameOverride(Block block, String[] properties) {
        for (Hidden i : HiddenManager.hidden.values()) {
            if (i.type instanceof BlockHiddenTypeInstance type) {
                if (type.original == null) {
                    continue;
                }
                if (type.matches(block)) {
                    BlockHiddenOverride override = findOverride(type, properties);
                    return override != null ? override.nameOverride : type.nameOverride;
                }
            }
        }
        return Optional.empty();
    }

    public static BlockHiddenOverride findOverride(BlockHiddenTypeInstance instance, String[] blockProperties) {
        for (BlockHiddenOverride j : instance.overrides) {
            boolean valid = true;
            String[] split = j.properties.split(",");
            for (String k : split) {
                String[] split2 = k.split("=");
                if (Arrays.stream(blockProperties).noneMatch((a) -> {
                    String[] split3 = a.split("=");
                    return split3[0].equals(split2[0]) && split3[1].equals(split2[1]);
                })) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                return j;
            }
        }
        return null;
    }
    public static String[] getProperties(BlockState state) {
        List<String> strings = new ArrayList<>();
        for (Property<?> i : state.getProperties()) {
            strings.add(i.getName() + "=" + state.getValue(i));
        }
        return strings.toArray(new String[0]);
    }
    public static Block getHiddenBlockClient(Block block, String[] properties) {
        for (Map.Entry<ResourceLocation, Hidden> i : HiddenManager.hidden.entrySet()) {
            if (i.getValue().type instanceof BlockHiddenTypeInstance type) {
                if (type.original == null || type.hiddenAs == null) {
                    continue;
                }
                if (type.isHiddenClient(block)) {
                    BlockHiddenOverride override = findOverride(type, properties);
                    return override != null ? override.hiddenAs : type.hiddenAs;
                } else if (type.matches(block)) {
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
        public final String properties;
        public Block hiddenAs;
        public Optional<Component> nameOverride;
        public HiddenCondition dropOriginalLootCondition;
        public boolean shouldOverwriteLootIfHidden;
        public BlockHiddenOverride(String properties, Block hiddenAs, Optional<Component> nameOverride, HiddenCondition dropOriginalLootCondition, boolean shouldOverwiteLootIfHidden) {
            this.properties = properties;
            this.hiddenAs = hiddenAs;
            this.nameOverride = nameOverride;
            this.dropOriginalLootCondition = dropOriginalLootCondition;
            this.shouldOverwriteLootIfHidden = shouldOverwiteLootIfHidden;
        }
        public static final StreamCodec<RegistryFriendlyByteBuf, BlockHiddenOverride> STREAM_CODEC = StreamCodec.of((buf, val) -> {
            buf.writeUtf(val.properties);
            buf.writeResourceKey(BuiltInRegistries.BLOCK.getResourceKey(val.hiddenAs).orElseThrow());
            buf.writeOptional(val.nameOverride, (buf2, val2) -> ComponentSerialization.STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf2, val2));
            buf.writeResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY.getResourceKey(val.dropOriginalLootCondition.getSerializer()).orElseThrow());
            val.dropOriginalLootCondition.getSerializer().streamCodec().encode(buf, val.dropOriginalLootCondition);
            buf.writeBoolean(val.shouldOverwriteLootIfHidden);
        }, (buf) -> {
            String properties = buf.readUtf();
            ResourceKey<Block> hiddenAsKey = buf.readResourceKey(Registries.BLOCK);
            Block hiddenAs = BuiltInRegistries.BLOCK.get(hiddenAsKey);
            Optional<Component> nameOverride = buf.readOptional((buf2) -> ComponentSerialization.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf2));
            ResourceKey<HiddenCondition.Serializer<?>> dropConditionKey = buf.readResourceKey(DatabankRegistries.HIDDEN_CONDITION_REGISTRY_KEY);
            HiddenCondition.Serializer<?> dropConditionSerializer = DatabankRegistries.HIDDEN_CONDITION_REGISTRY.get(dropConditionKey);
            HiddenCondition dropCondition = dropConditionSerializer.streamCodec().decode(buf);
            boolean shouldOverwriteLootIfHidden = buf.readBoolean();
            return new BlockHiddenOverride(properties, hiddenAs, nameOverride, dropCondition, shouldOverwriteLootIfHidden);
        });
        public static final MapCodec<BlockHiddenOverride> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("properties").forGetter((type) -> type.properties),
                ResourceKey.codec(Registries.BLOCK).fieldOf("hidden_as").xmap(BuiltInRegistries.BLOCK::get, (i) -> BuiltInRegistries.BLOCK.getResourceKey(i).orElseThrow()).forGetter((type) -> type.hiddenAs),
                ComponentSerialization.CODEC.optionalFieldOf("name_override").forGetter((type) -> type.nameOverride),
                HiddenSerializer.HIDDEN_CONDITION_CODEC.optionalFieldOf("drop_original_loot_condition", new ActualPlayerCondition()).forGetter((type) -> type.dropOriginalLootCondition),
                Codec.BOOL.optionalFieldOf("should_overwrite_loot_if_hidden", true).forGetter((type) -> type.shouldOverwriteLootIfHidden)
        ).apply(instance, BlockHiddenOverride::new));
    }
}
