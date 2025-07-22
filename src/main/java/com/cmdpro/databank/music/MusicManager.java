package com.cmdpro.databank.music;

import com.cmdpro.databank.Databank;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class MusicManager extends SimpleJsonResourceReloadListener {
    public static HashMap<ResourceLocation, MusicController> musicControllers = new HashMap<>();
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static MusicManager instance;
    protected MusicManager() {
        super(GSON, "databank/music");
    }
    public static MusicManager getOrCreateInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        HashMap<ResourceLocation, MusicController> musicControllers = new HashMap<>();
        Databank.LOGGER.info("[DATABANK] Adding Databank Music");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                MusicController music = serializer.read(i.getKey(), i.getValue().getAsJsonObject());
                if (music == null) {
                    continue;
                }
                musicControllers.put(i.getKey(), music);
            } catch (IllegalArgumentException | JsonParseException e) {
                Databank.LOGGER.error("[DATABANK ERROR] Parsing error loading music controller type {}", location, e);
            }
        }
        MusicManager.musicControllers = musicControllers;
        Databank.LOGGER.info("[DATABANK] Loaded {} Music Controllers", musicControllers.size());
    }
    public static MusicSerializer serializer = new MusicSerializer();
}
