package com.cmdpro.databank.hidden.types;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.config.DatabankClientConfig;
import com.cmdpro.databank.hidden.*;
import com.cmdpro.databank.hidden.conditions.ActualPlayerCondition;
import com.cmdpro.databank.mixin.client.BlockColorsAccessor;
import com.cmdpro.databank.registry.HiddenTypeRegistry;
import com.cmdpro.databank.rendering.ShaderHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BlockHiddenType extends HiddenTypeInstance.HiddenType<BlockHiddenType.BlockHiddenTypeInstance> {
    public static final BlockHiddenType INSTANCE = new BlockHiddenType();
    public static final MapCodec<BlockHiddenTypeInstance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(Registries.BLOCK).fieldOf("original").xmap(BuiltInRegistries.BLOCK::get, (i) -> BuiltInRegistries.BLOCK.getResourceKey(i).orElseThrow()).forGetter((type) -> type.original),
            ResourceKey.codec(Registries.BLOCK).fieldOf("hidden_as").xmap(BuiltInRegistries.BLOCK::get, (i) -> BuiltInRegistries.BLOCK.getResourceKey(i).orElseThrow()).forGetter((type) -> type.hiddenAs),
            ComponentSerialization.CODEC.optionalFieldOf("name_override").forGetter((type) -> type.nameOverride),
            HiddenSerializer.HIDDEN_CONDITION_CODEC.optionalFieldOf("drop_original_loot_condition", new ActualPlayerCondition()).forGetter((type) -> type.dropOriginalLootCondition),
            Codec.BOOL.optionalFieldOf("should_overwrite_loot_if_hidden", true).forGetter((type) -> type.shouldOverwriteLootIfHidden),
            StatePropertiesPredicate.CODEC.optionalFieldOf("should_apply_predicate").forGetter((type) -> type.shouldApplyPredicate),
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
        buf.writeOptional(val.shouldApplyPredicate, StatePropertiesPredicate.STREAM_CODEC);
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
        Optional<StatePropertiesPredicate> shouldApplyPredicate = buf.readOptional(StatePropertiesPredicate.STREAM_CODEC);
        List<BlockHiddenOverride> overrides = buf.readList((buf2) -> BlockHiddenOverride.STREAM_CODEC.decode((RegistryFriendlyByteBuf)buf2));
        return new BlockHiddenTypeInstance(original, hiddenAs, nameOverride, dropCondition, shouldOverwriteLootIfHidden, shouldApplyPredicate, overrides);
    });

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BlockHiddenTypeInstance> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public void updateClient() {
        ClientDatabankUtils.updateWorld();
    }

    @Override
    public void onRecieveClient() {
        ClientHandler.updateBlockColors();
    }

    public static boolean isVisible(Block block, Player player) {
        if (player.level().isClientSide) {
            return isVisibleClient(block);
        }
        return getHiddenBlock(block, player) == null;
    }
    public static boolean isVisible(BlockState block, Player player) {
        if (player.level().isClientSide) {
            return isVisibleClient(block);
        }
        return getHiddenBlock(block, player) == null;
    }
    public static boolean isVisibleClient(Block block) {
        return getHiddenBlockClient(block) == null;
    }
    public static boolean isVisibleClient(BlockState block) {
        return getHiddenBlockClient(block) == null;
    }
    public static Block getHiddenBlock(Block block, Player player) {
        if (player.level().isClientSide) {
            return getHiddenBlockClient(block);
        }
        return getHiddenBlock(block.defaultBlockState(), player);
    }
    public static Block getHiddenBlock(BlockState block, Player player) {
        if (player.level().isClientSide) {
            return getHiddenBlockClient(block);
        }
        for (Hidden i : HiddenManager.hidden.values()) {
            if (i.type instanceof BlockHiddenTypeInstance type) {
                if (type.original == null || type.hiddenAs == null || i.condition == null) {
                    continue;
                }
                if (type.isHidden(block.getBlock(), player)) {
                    boolean applies = true;
                    if (type.shouldApplyPredicate.isPresent()) {
                        applies = type.shouldApplyPredicate.get().matches(block);
                    }
                    if (applies) {
                        BlockHiddenOverride override = findOverride(type, block);
                        return override != null ? override.hiddenAs : type.hiddenAs;
                    }
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
                    boolean applies = true;
                    if (type.shouldApplyPredicate.isPresent()) {
                        applies = type.shouldApplyPredicate.get().matches(block);
                    }
                    if (applies) {
                        BlockHiddenOverride override = findOverride(type, block);
                        return override != null ? override.dropOriginalLootCondition.isUnlocked(player) : type.dropOriginalLootCondition.isUnlocked(player);
                    }
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
                    boolean applies = true;
                    if (type.shouldApplyPredicate.isPresent()) {
                        applies = type.shouldApplyPredicate.get().matches(block);
                    }
                    if (applies) {
                        BlockHiddenOverride override = findOverride(type, block);
                        return override != null ? override.hiddenAs : type.hiddenAs;
                    }
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
                    boolean applies = true;
                    if (type.shouldApplyPredicate.isPresent()) {
                        applies = type.shouldApplyPredicate.get().matches(block);
                    }
                    if (applies) {
                        BlockHiddenOverride override = findOverride(type, block);
                        return override != null ? override.nameOverride : type.nameOverride;
                    }
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
        for (Map.Entry<ResourceLocation, Hidden> i : new HashMap<>(HiddenManager.hidden).entrySet()) {
            if (i.getValue().type instanceof BlockHiddenTypeInstance type) {
                if (type.original == null || type.hiddenAs == null) {
                    continue;
                }
                if (type.isHiddenClient(block.getBlock())) {
                    boolean applies = true;
                    if (type.shouldApplyPredicate.isPresent()) {
                        applies = type.shouldApplyPredicate.get().matches(block);
                    }
                    if (applies) {
                        BlockHiddenOverride override = findOverride(type, block);
                        return override != null ? override.hiddenAs : type.hiddenAs;
                    }
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
        public Optional<StatePropertiesPredicate> shouldApplyPredicate;
        public List<BlockHiddenOverride> overrides;
        public BlockHiddenTypeInstance(Block original, Block hiddenAs, Optional<Component> nameOverride, HiddenCondition dropOriginalLootCondition, boolean shouldOverwiteLootIfHidden, Optional<StatePropertiesPredicate> shouldApplyPredicate, List<BlockHiddenOverride> overrides) {
            this.original = original;
            this.hiddenAs = hiddenAs;
            this.nameOverride = nameOverride;
            this.dropOriginalLootCondition = dropOriginalLootCondition;
            this.shouldOverwriteLootIfHidden = shouldOverwiteLootIfHidden;
            this.shouldApplyPredicate = shouldApplyPredicate;
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
        public StatePropertiesPredicate predicate;
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
    private static class ClientHandler {
        static HashMap<Block, BlockColor> overriden = new HashMap<>();
        public static void updateBlockColors() {
            if (!ShaderHelper.isSodiumOrSimilarActive() && !DatabankClientConfig.forceAlternateHiddenColors) {
                return;
            }
            BlockColors colors = Minecraft.getInstance().getBlockColors();
            HashMap<Block, List<WrappingData>> wrappingData = new HashMap<>();
            for (Hidden i : HiddenTypeRegistry.BLOCK.get().getHiddenOfType().values()) {
                if (i.type instanceof BlockHiddenTypeInstance instance) {
                    Block block = instance.original;
                    WrappingData data = new WrappingData(instance, instance.shouldApplyPredicate);
                    List<WrappingData> wrapData = wrappingData.getOrDefault(block, new ArrayList<>());
                    wrapData.add(data);
                    wrappingData.put(block, wrapData);
                    if (!overriden.containsKey(block)) {
                        BlockColor color = ((BlockColorsAccessor) colors).getBlockColors().get(block);
                        if (color != null) {
                            overriden.put(block, color);
                        }
                    }
                }
            }
            List<Block> blocksNotWrapped = new ArrayList<>(overriden.keySet());
            for (Map.Entry<Block, List<WrappingData>> i : wrappingData.entrySet()) {
                BlockColor original = overriden.get(i.getKey());
                BlockColor wrapped = createWrapped(original, i.getValue());
                ((BlockColorsAccessor) colors).getBlockColors().remove(i.getKey());
                colors.register(wrapped, i.getKey());
                blocksNotWrapped.remove(i.getKey());
            }
            for (Block i : blocksNotWrapped) {
                ((BlockColorsAccessor) colors).getBlockColors().remove(i);
                colors.register(overriden.get(i), i);
                overriden.remove(i);
            }
            Minecraft.getInstance().levelRenderer.allChanged();
        }
        public static BlockColor createWrapped(BlockColor original, List<WrappingData> wrappingData) {
            BlockColors colors = Minecraft.getInstance().getBlockColors();
            BlockColor wrapped = (state, level, pos, tintIndex) -> {
                BlockHiddenTypeInstance finalType = null;
                for (WrappingData i : wrappingData) {
                    boolean apply = true;
                    if (i.shouldApplyPredicate.isPresent()) {
                        apply = i.shouldApplyPredicate.get().matches(state);
                    }
                    if (apply) {
                        finalType = i.instance;
                    }
                }
                if (finalType != null) {
                    if (finalType.isHiddenClient(finalType.original)) {
                        Block hiddenAs = finalType.hiddenAs;
                        for (BlockHiddenOverride i : finalType.overrides) {
                            if (i.predicate.matches(state)) {
                                hiddenAs = i.hiddenAs;
                            }
                        }
                        return colors.getColor(DatabankUtils.changeBlockType(state, hiddenAs), level, pos, tintIndex);
                    }
                }
                if (original != null) {
                    return original.getColor(state, level, pos, tintIndex);
                }
                return 0xFFFFFFFF;
            };
            return wrapped;
        }
        private record WrappingData(BlockHiddenTypeInstance instance, Optional<StatePropertiesPredicate> shouldApplyPredicate) {}
    }
}
