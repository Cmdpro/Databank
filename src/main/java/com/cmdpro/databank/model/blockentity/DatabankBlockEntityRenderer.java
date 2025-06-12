package com.cmdpro.databank.model.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public abstract class DatabankBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private DatabankBlockEntityModel<T> model;

    public DatabankBlockEntityRenderer(DatabankBlockEntityModel<T> model) {
        this.model = model;
    }
    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0, 0.5);
        getModel().setupModelPose(pBlockEntity, pPartialTick);
        getModel().render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, 0xFFFFFFFF, new Vec3(1, 1, 1));
        pPoseStack.popPose();
    }
    public DatabankBlockEntityModel<T> getModel() {
        return model;
    }
}
