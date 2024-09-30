package com.cmdpro.databank.model.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class DatabankItemRenderer<T extends Item> extends BlockEntityWithoutLevelRenderer {
    private DatabankItemModel<T> model;

    public DatabankItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet, DatabankItemModel<T> model) {
        super(dispatcher, modelSet);
        this.model = model;
    }
    public abstract ResourceLocation getTextureLocation();
    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 1.5, 0.5);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180));
        getModel().root().getAllParts().forEach(ModelPart::resetPose);
        getModel().setupAnim(pStack);
        getModel().renderToBuffer(pPoseStack, pBuffer.getBuffer(getModel().renderType.apply(getTextureLocation())), pPackedLight, pPackedOverlay);
        pPoseStack.popPose();
    }

    public DatabankItemModel<T> getModel() {
        return model;
    }
}
