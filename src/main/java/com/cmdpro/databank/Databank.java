package com.cmdpro.databank;

import com.cmdpro.databank.hiddenblock.HiddenBlockConditions;
import com.cmdpro.databank.hiddenblock.conditions.AdvancementCondition;
import com.cmdpro.databank.hiddenblock.conditions.AndCondition;
import com.cmdpro.databank.hiddenblock.conditions.NotCondition;
import com.cmdpro.databank.hiddenblock.conditions.OrCondition;
import com.cmdpro.databank.rendering.RenderTypeHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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
        HiddenBlockConditions.conditions.put(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "and"), AndCondition.AndConditionSerializer.INSTANCE);
        HiddenBlockConditions.conditions.put(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "or"), OrCondition.OrConditionSerializer.INSTANCE);
        HiddenBlockConditions.conditions.put(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "not"), NotCondition.NotConditionSerializer.INSTANCE);
        HiddenBlockConditions.conditions.put(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "advancement"), AdvancementCondition.AdvancementConditionSerializer.INSTANCE);
    }
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        RenderTypeHandler.load();
    }
}
