package com.cmdpro.cmdlib;

import com.cmdpro.cmdlib.hiddenblocks.HiddenBlockConditions;
import com.cmdpro.cmdlib.hiddenblocks.conditions.AdvancementCondition;
import com.cmdpro.cmdlib.hiddenblocks.conditions.AndCondition;
import com.cmdpro.cmdlib.hiddenblocks.conditions.NotCondition;
import com.cmdpro.cmdlib.hiddenblocks.conditions.OrCondition;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;


import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("cmdlib")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = CmdLib.MOD_ID)
public class CmdLib
{

    public static final String MOD_ID = "cmdlib";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public CmdLib(IEventBus bus)
    {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
    }
    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event)
    {
        HiddenBlockConditions.conditions.put(new ResourceLocation(CmdLib.MOD_ID, "and"), new AndCondition.AndConditionSerializer());
        HiddenBlockConditions.conditions.put(new ResourceLocation(CmdLib.MOD_ID, "or"), new OrCondition.OrConditionSerializer());
        HiddenBlockConditions.conditions.put(new ResourceLocation(CmdLib.MOD_ID, "not"), new NotCondition.NotConditionSerializer());
        HiddenBlockConditions.conditions.put(new ResourceLocation(CmdLib.MOD_ID, "advancement"), new AdvancementCondition.AdvancementConditionSerializer());
    }
}
