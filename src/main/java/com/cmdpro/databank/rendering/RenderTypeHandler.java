package com.cmdpro.databank.rendering;

import com.cmdpro.databank.Databank;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RenderTypeHandler {
    public static final RenderStateShard.TransparencyStateShard ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public static final RenderStateShard.TransparencyStateShard TRANSPARENCY = new RenderStateShard.TransparencyStateShard("transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    public static List<RenderType> renderTypes = new ArrayList<>();
    public static List<RenderType> normalRenderTypes = new ArrayList<>();
    public static List<RenderType> particleRenderTypes = new ArrayList<>();
    public static final RenderStateShard.ShaderStateShard TRANSLUCENT_SHADER = new RenderStateShard.ShaderStateShard(ShaderTypeHandler::getTranslucent);
    public static final RenderStateShard.ShaderStateShard ADDITIVE_SHADER = new RenderStateShard.ShaderStateShard(ShaderTypeHandler::getAdditive);
    public static final RenderStateShard.TextureStateShard PARTICLE_SHEET = new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_PARTICLES, false, false);
    public static final RenderType TRANSPARENT = registerRenderType(RenderType.create(Databank.MOD_ID + ":transparent",
            DefaultVertexFormat.PARTICLE,
            VertexFormat.Mode.QUADS,
            256,
            true,
            true,
            RenderType.CompositeState.builder().setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).setLightmapState(RenderStateShard.LIGHTMAP).setTransparencyState(TRANSPARENCY).setShaderState(TRANSLUCENT_SHADER).createCompositeState(false)), true);
    public static final RenderType ADDITIVE = registerRenderType(RenderType.create(Databank.MOD_ID + ":additive",
            DefaultVertexFormat.PARTICLE,
            VertexFormat.Mode.QUADS,
            256,
            true,
            true,
            RenderType.CompositeState.builder().setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).setLightmapState(RenderStateShard.LIGHTMAP).setTransparencyState(ADDITIVE_TRANSPARENCY).setShaderState(ADDITIVE_SHADER).createCompositeState(false)), true);
    public static final RenderType TRANSPARENT_PARTICLE = registerRenderType(RenderType.create(Databank.MOD_ID + ":transparent",
            DefaultVertexFormat.PARTICLE,
            VertexFormat.Mode.QUADS,
            256,
            true,
            true,
            RenderType.CompositeState.builder().setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).setLightmapState(RenderStateShard.LIGHTMAP).setTransparencyState(TRANSPARENCY).setTextureState(PARTICLE_SHEET).setShaderState(TRANSLUCENT_SHADER).createCompositeState(false)), false);
    public static final RenderType ADDITIVE_PARTICLE = registerRenderType(RenderType.create(Databank.MOD_ID + ":additive",
            DefaultVertexFormat.PARTICLE,
            VertexFormat.Mode.QUADS,
            256,
            true,
            true,
            RenderType.CompositeState.builder().setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).setLightmapState(RenderStateShard.LIGHTMAP).setTransparencyState(ADDITIVE_TRANSPARENCY).setTextureState(PARTICLE_SHEET).setShaderState(ADDITIVE_SHADER).createCompositeState(false)), false);
    public static RenderType registerRenderType(RenderType type, boolean isParticle) {
        renderTypes.add(type);
        if (isParticle) {
            normalRenderTypes.add(type);
        } else {
            particleRenderTypes.add(type);
        }
        return type;
    }
    public static void load() {
        Databank.LOGGER.info("Loaded Databank Render Types");
    }
}
