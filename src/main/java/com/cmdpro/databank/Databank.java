package com.cmdpro.databank;

import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.music.MusicSystem;
import com.cmdpro.databank.registry.*;
import com.cmdpro.databank.rendering.RenderTypeHandler;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
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
        HiddenConditionRegistry.HIDDEN_CONDITIONS.register(bus);
        MusicConditionRegistry.MUSIC_CONDITIONS.register(bus);
        HiddenTypeRegistry.HIDDEN_TYPES.register(bus);
        CriteriaTriggerRegistry.TRIGGERS.register(bus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            DatabankModels.init();
            MusicSystem.init();
        }
    }
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        RenderTypeHandler.load();
    }
}
