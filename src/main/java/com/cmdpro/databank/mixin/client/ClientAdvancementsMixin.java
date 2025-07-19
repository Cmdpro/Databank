package com.cmdpro.databank.mixin.client;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.SequencedMap;

@Mixin(ClientAdvancements.class)
public interface ClientAdvancementsMixin {
    @Accessor("progress")
    public Map<AdvancementHolder, AdvancementProgress> getProgress();
}
