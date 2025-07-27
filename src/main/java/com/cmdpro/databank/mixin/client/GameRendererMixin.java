package com.cmdpro.databank.mixin.client;

import com.cmdpro.databank.misc.DatabankRenderLevelStages;
import com.cmdpro.databank.misc.ResizeHelper;
import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.databank.shaders.PostShaderManager;
import com.cmdpro.databank.worldgui.WorldGuiEntity;
import com.cmdpro.databank.worldgui.WorldGuiHitResult;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.client.ClientHooks;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "renderLevel", at = @At(value = "TAIL"), remap = false)
    private void Databank$renderLevel(DeltaTracker deltaTracker, CallbackInfo ci, @Local(name="matrix4f") Matrix4f matrix4f, @Local(name="matrix4f") Matrix4f matrix4f1, @Local(name="camera") Camera camera) {
        Minecraft mc = Minecraft.getInstance();
        LevelRenderer levelRenderer = mc.levelRenderer;
        ClientHooks.dispatchRenderStage(DatabankRenderLevelStages.AFTER_HAND, levelRenderer, null, matrix4f1, matrix4f, levelRenderer.getTicks(), camera, levelRenderer.getFrustum());
    }
    @Inject(method = "resize", at = @At(value = "TAIL"), remap = false)
    private void resize(int pWidth, int pHeight, CallbackInfo ci) {
        ResizeHelper.resize(pWidth, pHeight);
    }
    @Inject(method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private void Databank$pick(Entity entity, double blockInteractionRange, double entityInteractionRange, float partialTick, CallbackInfoReturnable<HitResult> cir) {
        HitResult result = cir.getReturnValue();

        Vec3 vec3 = entity.getEyePosition(partialTick);

        double range = Math.min(entityInteractionRange, result.getLocation().distanceTo(vec3));

        Vec3 vec31 = entity.getViewVector(partialTick);
        Vec3 vec32 = vec3.add(vec31.x * range, vec31.y * range, vec31.z * range);

        Level level = entity.level();
        List<WorldGuiEntity> guis = level.getEntitiesOfClass(WorldGuiEntity.class, AABB.ofSize(entity.position(), 24, 24, 24));
        double closestDistance = -1;
        WorldGuiEntity closest = null;
        WorldGuiEntity.WorldGuiIntersectionResult closestResult = null;
        for (WorldGuiEntity i : guis) {
            if (i.guiType == null || i.gui == null) {
                continue;
            }
            WorldGuiEntity.WorldGuiIntersectionResult intersectionResult = i.getLineIntersectResult(vec3, vec32);
            if (intersectionResult != null) {
                Vec3 pos = intersectionResult.pos;
                double distance = vec3.distanceTo(pos);
                if (closestDistance < 0 || distance < closestDistance) {
                    closestDistance = distance;
                    closest = i;
                    closestResult = intersectionResult;
                }
            }
        }
        if (closest != null) {
            double distance = vec3.distanceTo(closestResult.pos);
            if (distance < vec3.distanceTo(result.getLocation())) {
                WorldGuiHitResult hitResult = new WorldGuiHitResult(closest, closestResult.pos, closestResult);
                cir.setReturnValue(hitResult);
            }
        }
    }
}
