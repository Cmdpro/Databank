package com.cmdpro.cmdlib;

import com.cmdpro.cmdlib.hiddenblocks.HiddenBlockConditions;
import com.cmdpro.cmdlib.hiddenblocks.conditions.AdvancementCondition;
import com.cmdpro.cmdlib.hiddenblocks.conditions.AndCondition;
import com.cmdpro.cmdlib.hiddenblocks.conditions.NotCondition;
import com.cmdpro.cmdlib.hiddenblocks.conditions.OrCondition;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;


import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("cmdlib")
@Mod.EventBusSubscriber(modid = CmdLib.MOD_ID)
public class CmdLib
{

    public static final String MOD_ID = "cmdlib";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public CmdLib()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();



        GeckoLib.initialize();
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        
    }
    private void setup(final FMLCommonSetupEvent event)
    {
        HiddenBlockConditions.conditions.put(new ResourceLocation(CmdLib.MOD_ID, "and"), new AndCondition.AndConditionSerializer());
        HiddenBlockConditions.conditions.put(new ResourceLocation(CmdLib.MOD_ID, "or"), new OrCondition.OrConditionSerializer());
        HiddenBlockConditions.conditions.put(new ResourceLocation(CmdLib.MOD_ID, "not"), new NotCondition.NotConditionSerializer());
        HiddenBlockConditions.conditions.put(new ResourceLocation(CmdLib.MOD_ID, "advancement"), new AdvancementCondition.AdvancementConditionSerializer());
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // Some example code to dispatch IMC to another mod
        InterModComms.sendTo("cmdlib", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // Some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.messageSupplier().get()).
                collect(Collectors.toList()));
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
    }


}
