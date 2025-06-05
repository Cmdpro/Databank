package com.cmdpro.databank.model.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class DatabankBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private DatabankBlockEntityModel<T> model;

    public DatabankBlockEntityRenderer(DatabankBlockEntityModel<T> model) {
        this.model = model;
    }
    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 1.5, 0.5);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180));
        getModel().setupModelPose(pBlockEntity, pPartialTick);
        getModel().render(pBlockEntity, pPartialTick, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, 0xFFFFFFFF);
        pPoseStack.popPose();
    }
    public DatabankBlockEntityModel<T> getModel() {
        return model;
    }
}
