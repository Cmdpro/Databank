package com.cmdpro.databank;

import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.HiddenTypeInstance;
import com.cmdpro.databank.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.databank.music.MusicCondition;
import com.cmdpro.databank.worldgui.WorldGuiType;
import com.cmdpro.databank.worldgui.components.WorldGuiComponentType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = Databank.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DatabankRegistries {
    public static ResourceKey<Registry<MultiblockPredicateSerializer<?>>> MULTIBLOCK_PREDICATE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "multiblock_predicates"));
    public static Registry<MultiblockPredicateSerializer<?>> MULTIBLOCK_PREDICATE_REGISTRY = new RegistryBuilder<>(MULTIBLOCK_PREDICATE_REGISTRY_KEY).sync(true).create();
    public static ResourceKey<Registry<WorldGuiType>> WORLD_GUI_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "world_gui_types"));
    public static Registry<WorldGuiType> WORLD_GUI_TYPE_REGISTRY = new RegistryBuilder<>(WORLD_GUI_TYPE_REGISTRY_KEY).sync(true).create();
    public static ResourceKey<Registry<WorldGuiComponentType>> WORLD_GUI_COMPONENT_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "world_gui_components"));
    public static Registry<WorldGuiComponentType> WORLD_GUI_COMPONENT_REGISTRY = new RegistryBuilder<>(WORLD_GUI_COMPONENT_REGISTRY_KEY).sync(true).create();
    public static ResourceKey<Registry<MusicCondition.Serializer<?>>> MUSIC_CONDITION_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "music_conditions"));
    public static Registry<MusicCondition.Serializer<?>> MUSIC_CONDITION_REGISTRY = new RegistryBuilder<>(MUSIC_CONDITION_REGISTRY_KEY).sync(true).create();
    public static ResourceKey<Registry<HiddenCondition.Serializer<?>>> HIDDEN_CONDITION_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "hidden_conditions"));
    public static Registry<HiddenCondition.Serializer<?>> HIDDEN_CONDITION_REGISTRY = new RegistryBuilder<>(HIDDEN_CONDITION_REGISTRY_KEY).sync(true).create();
    public static ResourceKey<Registry<HiddenTypeInstance.HiddenType<?>>> HIDDEN_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "hidden_types"));
    public static Registry<HiddenTypeInstance.HiddenType<?>> HIDDEN_TYPE_REGISTRY = new RegistryBuilder<>(HIDDEN_TYPE_REGISTRY_KEY).sync(true).create();
    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(MULTIBLOCK_PREDICATE_REGISTRY);
        event.register(WORLD_GUI_TYPE_REGISTRY);
        event.register(WORLD_GUI_COMPONENT_REGISTRY);
        event.register(MUSIC_CONDITION_REGISTRY);
        event.register(HIDDEN_CONDITION_REGISTRY);
        event.register(HIDDEN_TYPE_REGISTRY);
    }
}
