package com.cmdpro.databank.hidden.types;

import com.cmdpro.databank.hidden.Hidden;
import com.cmdpro.databank.hidden.HiddenManager;
import com.cmdpro.databank.hidden.HiddenTypeInstance;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Optional;

public class ItemHiddenType extends HiddenTypeInstance.HiddenType<ItemHiddenType.ItemHiddenTypeInstance> {
    public static final ItemHiddenType INSTANCE = new ItemHiddenType();
    public static final MapCodec<ItemHiddenTypeInstance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceKey.codec(Registries.ITEM).fieldOf("original").xmap(BuiltInRegistries.ITEM::get, (i) -> BuiltInRegistries.ITEM.getResourceKey(i).orElseThrow()).forGetter((type) -> type.original),
            ResourceKey.codec(Registries.ITEM).fieldOf("hidden_as").xmap(BuiltInRegistries.ITEM::get, (i) -> BuiltInRegistries.ITEM.getResourceKey(i).orElseThrow()).forGetter((type) -> type.hiddenAs),
            ComponentSerialization.CODEC.optionalFieldOf("name_override").forGetter((type) -> type.nameOverride)
    ).apply(instance, ItemHiddenTypeInstance::new));


    @Override
    public MapCodec<ItemHiddenTypeInstance> codec() {
        return CODEC;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemHiddenTypeInstance> STREAM_CODEC = StreamCodec.of((buf, val) -> {
        buf.writeResourceKey(BuiltInRegistries.ITEM.getResourceKey(val.original).orElseThrow());
        buf.writeResourceKey(BuiltInRegistries.ITEM.getResourceKey(val.hiddenAs).orElseThrow());
        buf.writeOptional(val.nameOverride, (buf2, val2) -> ComponentSerialization.STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf2, val2));
    }, (buf) -> {
        ResourceKey<Item> originalKey = buf.readResourceKey(Registries.ITEM);
        ResourceKey<Item> hiddenAsKey = buf.readResourceKey(Registries.ITEM);
        Item original = BuiltInRegistries.ITEM.get(originalKey);
        Item hiddenAs = BuiltInRegistries.ITEM.get(hiddenAsKey);
        Optional<Component> nameOverride = buf.readOptional((buf2) -> ComponentSerialization.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf2));
        return new ItemHiddenTypeInstance(original, hiddenAs, nameOverride);
    });

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ItemHiddenTypeInstance> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public void updateClient() {
    }
    public static boolean isVisible(Item item, Player player) {
        if (player.level().isClientSide) {
            return isVisibleClient(item);
        }
        return getHiddenItem(item, player) == null;
    }
    public static boolean isVisibleClient(Item item) {
        return getHiddenItemClient(item) == null;
    }
    public static Item getHiddenItem(Item item, Player player) {
        if (player.level().isClientSide) {
            return getHiddenItemClient(item);
        }
        for (Hidden i : HiddenManager.hidden.values()) {
            if (i.type instanceof ItemHiddenTypeInstance type) {
                if (type.original == null || type.hiddenAs == null || i.condition == null) {
                    continue;
                }
                if (type.isHidden(item, player)) {
                    return type.hiddenAs;
                } else if (type.matches(item)) {
                    break;
                }
            }
        }
        return null;
    }

    public static Item getHiddenItem(Item item) {
        for (Hidden i : HiddenManager.hidden.values()) {
            if (i.type instanceof ItemHiddenTypeInstance type) {
                if (type.original == null || type.hiddenAs == null || i.condition == null) {
                    continue;
                }
                if (type.matches(item)) {
                    return type.hiddenAs;
                }
            }
        }
        return null;
    }

    public static Optional<Component> getHiddenItemNameOverride(Item item) {
        for (Hidden i : HiddenManager.hidden.values()) {
            if (i.type instanceof ItemHiddenTypeInstance type) {
                if (type.original == null) {
                    continue;
                }
                if (type.matches(item)) {
                    return type.nameOverride;
                }
            }
        }
        return Optional.empty();
    }

    public static Item getHiddenItemClient(Item item) {
        for (Map.Entry<ResourceLocation, Hidden> i : HiddenManager.hidden.entrySet()) {
            if (i.getValue().type instanceof ItemHiddenTypeInstance type) {
                if (type.original == null || type.hiddenAs == null) {
                    continue;
                }
                if (type.isHiddenClient(item)) {
                    return type.hiddenAs;
                } else if (type.matches(item)) {
                    break;
                }
            }
        }
        return null;
    }

    public static class ItemHiddenTypeInstance extends HiddenTypeInstance<Item> {
        public Item original;
        public Item hiddenAs;
        public Optional<Component> nameOverride;
        public ItemHiddenTypeInstance(Item original, Item hiddenAs, Optional<Component> nameOverride) {
            this.original = original;
            this.hiddenAs = hiddenAs;
            this.nameOverride = nameOverride;
        }
        @Override
        public boolean matches(Item obj) {
            return obj.equals(original);
        }

        @Override
        public HiddenType<? extends HiddenTypeInstance<Item>> getType() {
            return INSTANCE;
        }

    }
}
