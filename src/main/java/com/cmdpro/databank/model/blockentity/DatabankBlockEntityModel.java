package com.cmdpro.databank.model.blockentity;

import com.cmdpro.databank.model.*;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public abstract class DatabankBlockEntityModel<T extends BlockEntity> extends BaseDatabankModel<T> {
    public void render(T pEntity, float partialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, int pColor, boolean flipNormals) {
        renderModel(pEntity, partialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor, flipNormals);
    }
    public void renderModel(T pEntity, float partialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, int pColor, boolean flipNormals) {
        for (ModelPose.ModelPosePart i : modelPose.parts) {
            renderPartAndChildren(pEntity, partialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor, i, flipNormals);
        }
    }
}