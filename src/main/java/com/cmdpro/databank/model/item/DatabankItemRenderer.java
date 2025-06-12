package com.cmdpro.databank.model.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.phys.Vec3;

public abstract class DatabankItemRenderer<T extends Item> extends BlockEntityWithoutLevelRenderer {
    private DatabankItemModel<T> model;

    public DatabankItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet, DatabankItemModel<T> model) {
        super(dispatcher, modelSet);
        this.model = model;
    }
    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0, 0.5);
        getModel().setupModelPose(pStack, partialTick);
        getModel().render(pStack, partialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, 0xFFFFFFFF, pDisplayContext == ItemDisplayContext.GUI ? new Vec3(1, -1, -1) : new Vec3(1, 1, 1));
        pPoseStack.popPose();
    }

    public DatabankItemModel<T> getModel() {
        return model;
    }
}
