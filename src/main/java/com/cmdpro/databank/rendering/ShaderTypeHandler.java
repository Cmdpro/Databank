package com.cmdpro.databank.rendering;

import com.cmdpro.databank.Databank;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ShaderTypeHandler {
    public static ShaderInstance ADDITIVE;
    public static ShaderInstance TRANSPARENT;
    public static ShaderInstance SCREEN_PROJECTION;
    public static ShaderInstance getAdditive() {
        return ADDITIVE;
    }
    public static ShaderInstance getTranslucent() {
        return TRANSPARENT;
    }
    public static ShaderInstance getScreenProjection() {
        return SCREEN_PROJECTION;
    }
    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "additive"), DefaultVertexFormat.POSITION_TEX_COLOR), shader -> { ADDITIVE = shader; });
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "transparent"), DefaultVertexFormat.PARTICLE), shader -> { TRANSPARENT = shader; });
        event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "screen_projection"), DefaultVertexFormat.POSITION_TEX), shader -> { SCREEN_PROJECTION = shader; });
    }
}
