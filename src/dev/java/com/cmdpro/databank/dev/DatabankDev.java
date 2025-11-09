package com.cmdpro.databank.dev;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DatabankDev.MOD_ID)
public class DatabankDev
{

    public static final String MOD_ID = "databankdev";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public DatabankDev(IEventBus bus)
    {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        DatabankDevSpecialConditions.init();
    }
    public static ResourceLocation locate(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
