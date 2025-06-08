package com.cmdpro.databank.model.entity;

import com.cmdpro.databank.model.BaseDatabankModel;
import com.cmdpro.databank.model.ModelPose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public abstract class DatabankEntityModel<T extends Entity> extends BaseDatabankModel<T> {
    public void render(T pEntity, float partialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, int pColor, boolean flipNormals) {
        renderModel(pEntity, partialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor, flipNormals);
    }
    public void renderModel(T pEntity, float partialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, int pColor, boolean flipNormals) {
        for (ModelPose.ModelPosePart i : modelPose.parts) {
            renderPartAndChildren(pEntity, partialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor, i, flipNormals);
        }
    }

    @Override
    public RenderType getRenderType(T obj) {
        boolean bodyVisible = isBodyVisible(obj);
        boolean translucent = !bodyVisible && !obj.isInvisibleTo(Minecraft.getInstance().player);
        boolean glowing = Minecraft.getInstance().shouldEntityAppearGlowing(obj);
        ResourceLocation resourcelocation = getTextureLocation();
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (bodyVisible) {
            return super.getRenderType(obj);
        } else {
            return glowing ? RenderType.outline(resourcelocation) : null;
        }
    }
    protected boolean isBodyVisible(T livingEntity) {
        return !livingEntity.isInvisible();
    }
}