package com.cmdpro.databank;

import com.cmdpro.databank.config.DatabankClientConfig;
import com.cmdpro.databank.dialogue.styles.BasicDialogueStyle;
import com.cmdpro.databank.specialconditions.DatabankSpecialConditionManager;
import com.cmdpro.databank.specialconditions.BuiltinDatabankSpecialConditions;
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
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
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

        modLoadingContext.getActiveContainer().registerConfig(ModConfig.Type.CLIENT, DatabankClientConfig.CLIENT_SPEC, "databank-client.toml");

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
        ModelPartRegistry.MODEL_PART_TYPES.register(bus);
        DialogueActionRegistry.DIALOGUE_ACTIONS.register(bus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            DialogueStyleRegistry.DIALOGUE_STYLES.register(bus);
            DatabankClient.register();
            modLoadingContext.getActiveContainer().registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
        BuiltinDatabankSpecialConditions.init();
    }
    public static ResourceLocation locate(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        RenderTypeHandler.load();
    }
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        DatabankSpecialConditionManager.init();
    }
    @SubscribeEvent
    public static void onModConfigEvent(ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == DatabankClientConfig.CLIENT_SPEC) {
            DatabankClientConfig.bake(config);
        }
    }
}
