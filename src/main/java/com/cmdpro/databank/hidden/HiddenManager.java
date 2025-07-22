package com.cmdpro.databank.hidden;

import com.cmdpro.databank.Databank;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HiddenManager extends SimpleJsonResourceReloadListener {
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static HiddenManager instance;
    protected HiddenManager() {
        super(GSON, "databank/hidden");
    }
    public static HiddenManager getOrCreateInstance() {
        if (instance == null) {
            instance = new HiddenManager();
        }
        return instance;
    }
    public static Map<ResourceLocation, Hidden> hidden = new HashMap<>();
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        Map<ResourceLocation, Hidden> hidden = new HashMap<>();
        Databank.LOGGER.info("[DATABANK] Adding Databank Hidden Entries");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                JsonObject obj = i.getValue().getAsJsonObject();
                Hidden value = serializer.read(i.getKey(), obj);
                if (value == null) {
                    continue;
                }
                value.id = i.getKey();
                hidden.put(i.getKey(), value);
            } catch (Exception e) {
                Databank.LOGGER.error("[DATABANK ERROR] Parsing error loading hidden entry {}", location, e);
            }
        }
        HiddenManager.hidden = hidden;
        Databank.LOGGER.info("[DATABANK] Loaded {} hidden entries", hidden.size());
    }
    public static HiddenSerializer serializer = new HiddenSerializer();
}
