package com.cmdpro.databank.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec2;

public abstract class DatabankLivingEntityModel<T extends LivingEntity> extends DatabankEntityModel<T> {
    @Override
    public void renderModel(T pEntity, float partialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, int pColor, boolean flipNormals) {
        pPoseStack.pushPose();
        float scale = pEntity.getScale();
        pPoseStack.scale(scale, scale, scale);
        setupRotations(pEntity, pPoseStack, partialTick, scale);
        super.renderModel(pEntity, partialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor, flipNormals);
        pPoseStack.popPose();
    }

    public Vec2 getHeadRot(T entity, float partialTicks) {
        float bodyRot = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float headRot = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
        float yRot = headRot - bodyRot;
        if (getShouldSit(entity) && entity.getVehicle() instanceof LivingEntity livingentity) {
            bodyRot = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            yRot = headRot - bodyRot;
            float f7 = Mth.wrapDegrees(yRot);
            if (f7 < -85.0F) {
                f7 = -85.0F;
            }

            if (f7 >= 85.0F) {
                f7 = 85.0F;
            }

            bodyRot = headRot - f7;
            if (f7 * f7 > 2500.0F) {
                bodyRot += f7 * 0.2F;
            }

            yRot = headRot - bodyRot;
        }
        float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        if (isEntityUpsideDown(entity)) {
            xRot *= -1.0F;
            yRot *= -1.0F;
        }

        yRot = Mth.wrapDegrees(yRot);
        return new Vec2(xRot, yRot);
    }
    public float getYBodyRot(T entity, float partialTicks) {
        float bodyRot = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float headRot = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
        if (getShouldSit(entity) && entity.getVehicle() instanceof LivingEntity livingentity) {
            bodyRot = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
            float yRot = headRot - bodyRot;
            float f7 = Mth.wrapDegrees(yRot);
            if (f7 < -85.0F) {
                f7 = -85.0F;
            }

            if (f7 >= 85.0F) {
                f7 = 85.0F;
            }

            bodyRot = headRot - f7;
            if (f7 * f7 > 2500.0F) {
                bodyRot += f7 * 0.2F;
            }
        }
        if (this.isShaking(entity)) {
            bodyRot += (float)(Math.cos((double)entity.tickCount * 3.25) * Math.PI * 0.4F);
        }
        return bodyRot;
    }
    public boolean isEntityUpsideDown(T entity) {
        return LivingEntityRenderer.isEntityUpsideDown(entity);
    }
    public boolean getShouldSit(T entity) {
        return entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
    }
    protected boolean isShaking(T entity) {
        return entity.isFullyFrozen();
    }

    protected void setupRotations(T entity, PoseStack poseStack, float partialTick, float scale) {
        float yBodyRot = getYBodyRot(entity, partialTick);
        if (!entity.hasPose(Pose.SLEEPING)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yBodyRot));
        }

        if (entity.deathTime > 0) {
            float f = ((float)entity.deathTime + partialTick - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            poseStack.mulPose(Axis.ZP.rotationDegrees(f * this.getFlipDegrees(entity)));
        } else if (entity.isAutoSpinAttack()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F - entity.getXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees(((float)entity.tickCount + partialTick) * -75.0F));
        } else if (entity.hasPose(Pose.SLEEPING)) {
            Direction direction = entity.getBedOrientation();
            float f1 = direction != null ? sleepDirectionToRotation(direction) : yBodyRot;
            poseStack.mulPose(Axis.YP.rotationDegrees(f1));
            poseStack.mulPose(Axis.ZP.rotationDegrees(this.getFlipDegrees(entity)));
            poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
        } else if (isEntityUpsideDown(entity)) {
            poseStack.translate(0.0F, (entity.getBbHeight() + 0.1F) / scale, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }
    private static float sleepDirectionToRotation(Direction facing) {
        switch (facing) {
            case SOUTH:
                return 90.0F;
            case WEST:
                return 0.0F;
            case NORTH:
                return 270.0F;
            case EAST:
                return 180.0F;
            default:
                return 0.0F;
        }
    }
    protected float getFlipDegrees(T livingEntity) {
        return 90.0F;
    }
}