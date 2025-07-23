package com.cmdpro.databank.config;

import com.cmdpro.databank.Databank;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class DatabankClientConfig {
    public static final ModConfigSpec CLIENT_SPEC;
    public static final DatabankClientConfig CLIENT;
    static {
        {
            final Pair<DatabankClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(DatabankClientConfig::new);
            CLIENT = specPair.getLeft();
            CLIENT_SPEC = specPair.getRight();
        }
    }
    public DatabankClientConfig(ModConfigSpec.Builder builder) {
        builder.push("compatibility");
        forceAlternateHiddenColorsValue = buildBoolean(builder, "forceAlternateHiddenColors", false, "Should the alternate method for block/item coloring be forcibly enabled? This is normally only applied for sodium and embeddium, Do not change unless there are problems");
        forceAlternateChunkReloadValue = buildBoolean(builder, "forceAlternateChunkReload", false, "Should the alternate method for reloading chunks when hidden blocks are updated be forcibly enabled? This is normally only applied for sodium and embeddium, Do not change unless there are problems");
        builder.pop();
    }
    private static ModConfigSpec.BooleanValue buildBoolean(ModConfigSpec.Builder builder, String name, boolean defaultValue, String comment) {
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }
    private static ModConfigSpec.IntValue buildInteger(ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }
    private static ModConfigSpec.DoubleValue buildDouble(ModConfigSpec.Builder builder, String name, double defaultValue, double min, double max, String comment) {
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }
    public static boolean forceAlternateHiddenColors = true;
    public final ModConfigSpec.BooleanValue forceAlternateHiddenColorsValue;
    public static boolean forceAlternateChunkReload = true;
    public final ModConfigSpec.BooleanValue forceAlternateChunkReloadValue;
    public static void bake(ModConfig config) {
        try {
            forceAlternateHiddenColors = CLIENT.forceAlternateHiddenColorsValue.get();
            forceAlternateChunkReload = CLIENT.forceAlternateChunkReloadValue.get();
        } catch (Exception e) {
            Databank.LOGGER.warn("[DATABANK] Failed to load client config!");
            e.printStackTrace();
        }
    }
}
