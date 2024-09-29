package com.cmdpro.databank.hiddenblocks;

import com.cmdpro.databank.Databank;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.HashMap;
import java.util.Map;

public class HiddenBlocksManager extends SimpleJsonResourceReloadListener {
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static HiddenBlocksManager instance;
    protected HiddenBlocksManager() {
        super(GSON, "databank/hidden_blocks");
    }
    public static HiddenBlocksManager getOrCreateInstance() {
        if (instance == null) {
            instance = new HiddenBlocksManager();
        }
        return instance;
    }
    public static Map<ResourceLocation, HiddenBlock> blocks = new HashMap<>();
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        blocks = new HashMap<>();
        Databank.LOGGER.info("Adding Databank Hidden Blocks");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                JsonObject obj = i.getValue().getAsJsonObject();
                HiddenBlock block = serializer.read(i.getKey(), obj);
                blocks.put(i.getKey(), block);
            } catch (IllegalArgumentException | JsonParseException e) {
                Databank.LOGGER.error("Parsing error loading hidden block type {}", location, e);
            }
        }
        Databank.LOGGER.info("Loaded {} hidden blocks", blocks.size());
    }
    public static HiddenBlocksSerializer serializer = new HiddenBlocksSerializer();
}
