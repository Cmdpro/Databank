package com.cmdpro.databank.model.blockentity;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Function;

public abstract class DatabankBlockEntityModel<T extends BlockEntity> extends Model {
    public Function<ResourceLocation, RenderType> renderType;
    protected DatabankBlockEntityModel() {
        this(RenderType::entityCutoutNoCull);
    }

    protected DatabankBlockEntityModel(Function<ResourceLocation, RenderType> pRenderType) {
        super(pRenderType);
        this.renderType = pRenderType;
    }
    public abstract void setupAnim(T pEntity);
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();
    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int pColor) {
        this.root().render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor);
    }

    public abstract ModelPart root();

    public Optional<ModelPart> getAnyDescendantWithName(String pName) {
        return pName.equals("root")
                ? Optional.of(this.root())
                : this.root().getAllParts().filter(p_233400_ -> p_233400_.hasChild(pName)).findFirst().map(p_233397_ -> p_233397_.getChild(pName));
    }

    protected void animate(AnimationState pAnimationState, AnimationDefinition pAnimationDefinition) {
        this.animate(pAnimationState, pAnimationDefinition, 1.0F);
    }
    public float getAgeInTicks() {
        return (float)(Blaze3D.getTime() * 20d);
    }
    protected void animate(AnimationState pAnimationState, AnimationDefinition pAnimationDefinition, float pSpeed) {
        pAnimationState.updateTime(getAgeInTicks(), pSpeed);
        pAnimationState.ifStarted(p_233392_ -> BlockEntityKeyframeAnimations.animate(this, pAnimationDefinition, p_233392_.getAccumulatedTime(), 1.0F, ANIMATION_VECTOR_CACHE));
    }

    protected void applyStatic(AnimationDefinition pAnimationDefinition) {
        BlockEntityKeyframeAnimations.animate(this, pAnimationDefinition, 0L, 1.0F, ANIMATION_VECTOR_CACHE);
    }
}