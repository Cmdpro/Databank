package com.cmdpro.databank.megastructures;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.music.MusicController;
import com.cmdpro.databank.music.MusicSerializer;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.HashMap;
import java.util.Map;

public class MegastructureManager extends SimpleJsonResourceReloadListener {
    public static HashMap<ResourceLocation, Megastructure> megastructures = new HashMap<>();
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static MegastructureManager instance;
    protected MegastructureManager() {
        super(GSON, "databank/megastructures");
    }
    public static MegastructureManager getOrCreateInstance() {
        if (instance == null) {
            instance = new MegastructureManager();
        }
        return instance;
    }
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        megastructures = new HashMap<>();
        Databank.LOGGER.info("[DATABANK] Adding Databank Megastructures");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                JsonObject obj = i.getValue().getAsJsonObject();
                Megastructure megastructure = ICondition.getWithWithConditionsCodec(Megastructure.CONDITION_CODEC, JsonOps.INSTANCE, obj).orElse(null);
                megastructures.put(i.getKey(), megastructure);
            } catch (IllegalArgumentException | JsonParseException e) {
                Databank.LOGGER.error("[DATABANK ERROR] Parsing error loading music controller type {}", location, e);
            }
        }
        Databank.LOGGER.info("[DATABANK] Loaded {} Music Controllers", megastructures.size());
    }
    public static MusicSerializer serializer = new MusicSerializer();
}
