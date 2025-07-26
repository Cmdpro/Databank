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
        forceAlternateHiddenColorsValue = buildBoolean(builder, "forceAlternateHiddenColors", false, "Should the alternate method for block/item coloring be forcibly enabled? This is normally only applied for sodium and embeddium, Do not change unless there are problems", true);
        forceAlternateChunkReloadValue = buildBoolean(builder, "forceAlternateChunkReload", false, "Should the alternate method for reloading chunks when hidden blocks are updated be forcibly enabled? This is normally only applied for sodium and embeddium, Do not change unless there are problems", true);
        builder.pop();
        builder.push("accessibility");
        screenshakeMultiplierValue = buildDouble(builder, "screenshakeMultiplier", 1d, 0, 1, "The multiplier to apply for screenshake intensity");
        allowImpactVisualsValue = buildBoolean(builder, "allowImpactVisuals", true, "Should impact visuals be allowed?");
        allowFlashOnImpactVisualsValue = buildBoolean(builder, "allowFlashOnImpactVisuals", true, "Should impact visuals be allowed to flash?");
        builder.pop();
    }
    private static ModConfigSpec.BooleanValue buildBoolean(ModConfigSpec.Builder builder, String name, boolean defaultValue, String comment, boolean gameRestart) {
        builder.comment(comment).translation(name);
        if (gameRestart) {
            builder.gameRestart();
        }
        return builder.define(name, defaultValue);
    }
    private static ModConfigSpec.BooleanValue buildBoolean(ModConfigSpec.Builder builder, String name, boolean defaultValue, String comment) {
        return buildBoolean(builder, name, defaultValue, comment, false);
    }
    private static ModConfigSpec.IntValue buildInteger(ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment, boolean gameRestart) {
        builder.comment(comment).translation(name);
        if (gameRestart) {
            builder.gameRestart();
        }
        return builder.defineInRange(name, defaultValue, min, max);
    }
    private static ModConfigSpec.IntValue buildInteger(ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment) {
        return buildInteger(builder, name, defaultValue, min, max, name, false);
    }
    private static ModConfigSpec.DoubleValue buildDouble(ModConfigSpec.Builder builder, String name, double defaultValue, double min, double max, String comment, boolean gameRestart) {
        builder.comment(comment).translation(name);
        if (gameRestart) {
            builder.gameRestart();
        }
        return builder.defineInRange(name, defaultValue, min, max);
    }
    private static ModConfigSpec.DoubleValue buildDouble(ModConfigSpec.Builder builder, String name, double defaultValue, double min, double max, String comment) {
        return buildDouble(builder, name, defaultValue, min, max, name, false);
    }
    public static boolean forceAlternateHiddenColors = true;
    public final ModConfigSpec.BooleanValue forceAlternateHiddenColorsValue;
    public static boolean forceAlternateChunkReload = true;
    public final ModConfigSpec.BooleanValue forceAlternateChunkReloadValue;
    public static double screenshakeMultiplier = 1f;
    public final ModConfigSpec.DoubleValue screenshakeMultiplierValue;
    public static boolean allowImpactVisuals = true;
    public final ModConfigSpec.BooleanValue allowImpactVisualsValue;
    public static boolean allowFlashOnImpactVisuals = true;
    public final ModConfigSpec.BooleanValue allowFlashOnImpactVisualsValue;
    public static void bake(ModConfig config) {
        try {
            forceAlternateHiddenColors = CLIENT.forceAlternateHiddenColorsValue.get();
            forceAlternateChunkReload = CLIENT.forceAlternateChunkReloadValue.get();
            screenshakeMultiplier = CLIENT.screenshakeMultiplierValue.get();
            allowImpactVisuals = CLIENT.allowImpactVisualsValue.get();
            allowFlashOnImpactVisuals = CLIENT.allowFlashOnImpactVisualsValue.get();
        } catch (Exception e) {
            Databank.LOGGER.warn("[DATABANK] Failed to load client config!");
            e.printStackTrace();
        }
    }
}
