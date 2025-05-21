package com.cmdpro.databank;

import com.cmdpro.databank.hiddenblock.HiddenBlockConditions;
import com.cmdpro.databank.hiddenblock.conditions.AdvancementCondition;
import com.cmdpro.databank.hiddenblock.conditions.AndCondition;
import com.cmdpro.databank.hiddenblock.conditions.NotCondition;
import com.cmdpro.databank.hiddenblock.conditions.OrCondition;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.music.MusicConditions;
import com.cmdpro.databank.music.MusicController;
import com.cmdpro.databank.music.MusicSystem;
import com.cmdpro.databank.music.conditions.AndMusicCondition;
import com.cmdpro.databank.music.conditions.EntityNearbyMusicCondition;
import com.cmdpro.databank.music.conditions.NotMusicCondition;
import com.cmdpro.databank.music.conditions.OrMusicCondition;
import com.cmdpro.databank.registry.*;
import com.cmdpro.databank.rendering.RenderTypeHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Databank.MOD_ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Databank.MOD_ID)
public class Databank
{

    public static final String MOD_ID = "databank";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public Databank(IEventBus bus)
    {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        ItemRegistry.ITEMS.register(bus);
        BlockRegistry.BLOCKS.register(bus);
        BlockEntityRegistry.BLOCK_ENTITIES.register(bus);
        MultiblockPredicateRegistry.MULTIBLOCK_PREDICATE_TYPES.register(bus);
        AttachmentTypeRegistry.ATTACHMENT_TYPES.register(bus);
        EntityRegistry.ENTITY_TYPES.register(bus);

        HiddenBlockConditions.conditions.put(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "and"), AndCondition.AndConditionSerializer.INSTANCE);
        HiddenBlockConditions.conditions.put(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "or"), OrCondition.OrConditionSerializer.INSTANCE);
        HiddenBlockConditions.conditions.put(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "not"), NotCondition.NotConditionSerializer.INSTANCE);
        HiddenBlockConditions.conditions.put(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "advancement"), AdvancementCondition.AdvancementConditionSerializer.INSTANCE);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            DatabankModels.init();
            MusicSystem.init();
        }
    }
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        RenderTypeHandler.load();
        MusicConditions.conditions.put(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "and"), AndMusicCondition.AndConditionSerializer.INSTANCE);
        MusicConditions.conditions.put(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "or"), OrMusicCondition.OrConditionSerializer.INSTANCE);
        MusicConditions.conditions.put(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "not"), NotMusicCondition.NotConditionSerializer.INSTANCE);
        MusicConditions.conditions.put(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "entity_nearby"), EntityNearbyMusicCondition.EntityNearbyConditionSerializer.INSTANCE);
    }
}
