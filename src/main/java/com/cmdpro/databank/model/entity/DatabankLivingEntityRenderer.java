package com.cmdpro.databank.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec2;


public abstract class DatabankLivingEntityRenderer<T extends LivingEntity> extends DatabankEntityRenderer<T> {
    public DatabankLivingEntityRenderer(EntityRendererProvider.Context context, DatabankEntityModel<T> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Override
    public void render(T pEntity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(pEntity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
    @Override
    public boolean shouldShowName(T entity) {
        return super.shouldShowName(entity)
                && (entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
    }
    @Override
    public int getOverlayCoords(T entity) {
        return OverlayTexture.pack(OverlayTexture.u(0), OverlayTexture.v(entity.hurtTime > 0 || entity.deathTime > 0));
    }

    @Override
    public float getShadowRadius(T entity) {
        return super.getShadowRadius(entity) * entity.getAgeScale();
    }
}
