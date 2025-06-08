package com.cmdpro.databank.model.entity;

import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.model.ModelPose;
import com.cmdpro.databank.model.blockentity.DatabankBlockEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec2;


public abstract class DatabankEntityRenderer<T extends Entity> extends EntityRenderer<T> {
    private DatabankEntityModel<T> model;
    public DatabankEntityRenderer(EntityRendererProvider.Context context, DatabankEntityModel<T> model, float shadowRadius) {
        super(context);
        this.shadowRadius = shadowRadius;
        this.model = model;
    }

    @Override
    public void render(T pEntity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(pEntity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.pushPose();
        poseStack.translate(0, 1.5, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        getModel().setupModelPose(pEntity, partialTick);
        getModel().render(pEntity, partialTick, poseStack, bufferSource, packedLight, getOverlayCoords(pEntity), 0xFFFFFFFF, false);
        poseStack.popPose();
    }
    public int getOverlayCoords(T entity) {
        return OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(false));
    }

    public DatabankEntityModel<T> getModel() {
        return model;
    }
}
