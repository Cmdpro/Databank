package com.cmdpro.databank.rendering;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.ModList;

public class ShaderHelper {
    public static boolean shouldUseAlternateRendering() {
        if (ModList.get().isLoaded("iris")) {
            return IrisHandler.areShadersEnabled() || Minecraft.useShaderTransparency();
        }
        return Minecraft.useShaderTransparency();
    }
    public static boolean isSodiumActive() {
        return ModList.get().isLoaded("sodium");
    }
    public static class IrisHandler {
        public static boolean areShadersEnabled() {
            return IrisApi.getInstance().isShaderPackInUse();
        }
    }
}