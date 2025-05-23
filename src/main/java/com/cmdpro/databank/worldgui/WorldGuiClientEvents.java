package com.cmdpro.databank.worldgui;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.networking.ModMessages;
import com.cmdpro.databank.networking.packet.WorldGuiInteractC2SPacket;
import com.cmdpro.databank.rendering.RenderTypeHandler;
import com.cmdpro.databank.rendering.ShaderHelper;
import com.cmdpro.databank.worldgui.components.WorldGuiComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.joml.Matrix4f;

import java.util.SequencedMap;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID)
public class WorldGuiClientEvents {
    @SubscribeEvent
    public static void interactionKeyMappingTriggeredEvent(InputEvent.InteractionKeyMappingTriggered event) {
        HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult instanceof WorldGuiHitResult result) {
            if (result.getEntity() instanceof WorldGuiEntity entity) {
                if (entity.gui != null && entity.guiType != null) {
                    Vec2 normal = result.result.normal;
                    int x = (int)(normal.x*entity.guiType.getRenderSize().x);
                    int y = (int)(normal.y*entity.guiType.getRenderSize().y);
                    if (event.getKeyMapping().equals(Minecraft.getInstance().options.keyAttack)) {
                        entity.gui.leftClick(true, Minecraft.getInstance().player, x, y);
                        for (WorldGuiComponent i : entity.gui.components.stream().toList()) {
                            if (entity.gui.tryLeftClickComponent(true, Minecraft.getInstance().player, i, x, y)) {
                                i.leftClick(true, Minecraft.getInstance().player, x, y);
                            }
                        }
                        ModMessages.sendToServer(new WorldGuiInteractC2SPacket(entity.getId(), 0, x, y));
                        event.setCanceled(true);
                    } else if (event.getKeyMapping().equals(Minecraft.getInstance().options.keyUse)) {
                        entity.gui.rightClick(true, Minecraft.getInstance().player, x, y);
                        for (WorldGuiComponent i : entity.gui.components.stream().toList()) {
                            if (entity.gui.tryLeftClickComponent(true, Minecraft.getInstance().player, i, x, y)) {
                                i.rightClick(true, Minecraft.getInstance().player, x, y);
                            }
                        }
                        ModMessages.sendToServer(new WorldGuiInteractC2SPacket(entity.getId(), 1, x, y));
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}