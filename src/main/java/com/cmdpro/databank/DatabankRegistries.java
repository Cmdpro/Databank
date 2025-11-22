package com.cmdpro.databank;

import com.cmdpro.databank.dialogue.DialogueChoiceAction;
import com.cmdpro.databank.dialogue.DialogueStyle;
import com.cmdpro.databank.hidden.HiddenCondition;
import com.cmdpro.databank.hidden.HiddenTypeInstance;
import com.cmdpro.databank.model.DatabankPartData;
import com.cmdpro.databank.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.databank.music.MusicCondition;
import com.cmdpro.databank.worldgui.WorldGuiType;
import com.cmdpro.databank.worldgui.components.WorldGuiComponentType;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = Databank.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DatabankRegistries {
    public static ResourceKey<Registry<MultiblockPredicateSerializer<?>>> MULTIBLOCK_PREDICATE_REGISTRY_KEY = ResourceKey.createRegistryKey(Databank.locate("multiblock_predicates"));
    public static ResourceKey<Registry<WorldGuiType>> WORLD_GUI_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Databank.locate("world_gui_types"));
    public static ResourceKey<Registry<WorldGuiComponentType>> WORLD_GUI_COMPONENT_REGISTRY_KEY = ResourceKey.createRegistryKey(Databank.locate("world_gui_components"));
    public static ResourceKey<Registry<MusicCondition.Serializer<?>>> MUSIC_CONDITION_REGISTRY_KEY = ResourceKey.createRegistryKey(Databank.locate("music_conditions"));
    public static ResourceKey<Registry<HiddenCondition.Serializer<?>>> HIDDEN_CONDITION_REGISTRY_KEY = ResourceKey.createRegistryKey(Databank.locate("hidden_conditions"));
    public static ResourceKey<Registry<HiddenTypeInstance.HiddenType<?>>> HIDDEN_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Databank.locate("hidden_types"));
    public static ResourceKey<Registry<MapCodec<? extends DatabankPartData>>> MODEL_PART_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Databank.locate("model_part_type"));
    public static ResourceKey<Registry<MapCodec<? extends DialogueStyle>>> DIALOGUE_STYLE_REGISTRY_KEY = ResourceKey.createRegistryKey(Databank.locate("dialogue_style"));
    public static ResourceKey<Registry<DialogueChoiceAction.Codecs>> DIALOGUE_CHOICE_ACTION_REGISTRY_KEY = ResourceKey.createRegistryKey(Databank.locate("dialogue_choice_action"));

    public static Registry<MultiblockPredicateSerializer<?>> MULTIBLOCK_PREDICATE_REGISTRY = new RegistryBuilder<>(MULTIBLOCK_PREDICATE_REGISTRY_KEY).sync(true).create();
    public static Registry<WorldGuiType> WORLD_GUI_TYPE_REGISTRY = new RegistryBuilder<>(WORLD_GUI_TYPE_REGISTRY_KEY).sync(true).create();
    public static Registry<WorldGuiComponentType> WORLD_GUI_COMPONENT_REGISTRY = new RegistryBuilder<>(WORLD_GUI_COMPONENT_REGISTRY_KEY).sync(true).create();
    public static Registry<MusicCondition.Serializer<?>> MUSIC_CONDITION_REGISTRY = new RegistryBuilder<>(MUSIC_CONDITION_REGISTRY_KEY).sync(true).create();
    public static Registry<HiddenCondition.Serializer<?>> HIDDEN_CONDITION_REGISTRY = new RegistryBuilder<>(HIDDEN_CONDITION_REGISTRY_KEY).sync(true).create();
    public static Registry<HiddenTypeInstance.HiddenType<?>> HIDDEN_TYPE_REGISTRY = new RegistryBuilder<>(HIDDEN_TYPE_REGISTRY_KEY).sync(true).create();
    public static Registry<MapCodec<? extends DatabankPartData>> MODEL_PART_TYPE_REGISTRY = new RegistryBuilder<>(MODEL_PART_TYPE_REGISTRY_KEY).create();
    public static Registry<MapCodec<? extends DialogueStyle>> DIALOGUE_STYLE_REGISTRY = new RegistryBuilder<>(DIALOGUE_STYLE_REGISTRY_KEY).create();
    public static Registry<DialogueChoiceAction.Codecs> DIALOGUE_CHOICE_ACTION_REGISTRY = new RegistryBuilder<>(DIALOGUE_CHOICE_ACTION_REGISTRY_KEY).create();
    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(MULTIBLOCK_PREDICATE_REGISTRY);
        event.register(WORLD_GUI_TYPE_REGISTRY);
        event.register(WORLD_GUI_COMPONENT_REGISTRY);
        event.register(MUSIC_CONDITION_REGISTRY);
        event.register(HIDDEN_CONDITION_REGISTRY);
        event.register(HIDDEN_TYPE_REGISTRY);
        event.register(MODEL_PART_TYPE_REGISTRY);
        event.register(DIALOGUE_STYLE_REGISTRY);
        event.register(DIALOGUE_CHOICE_ACTION_REGISTRY);
    }
}
