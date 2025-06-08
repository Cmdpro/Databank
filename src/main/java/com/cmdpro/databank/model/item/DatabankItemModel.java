package com.cmdpro.databank.model.item;

import com.cmdpro.databank.model.BaseDatabankModel;
import com.cmdpro.databank.model.ModelPose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public abstract class DatabankItemModel<T extends Item> extends BaseDatabankModel<ItemStack> {
    public void render(ItemStack pItem, float partialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, int pColor, boolean flipNormals) {
        renderModel(pItem, partialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor, flipNormals);
    }
    public void renderModel(ItemStack pItem, float partialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, int pColor, boolean flipNormals) {
        for (ModelPose.ModelPosePart i : modelPose.parts) {
            renderPartAndChildren(pItem, partialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor, i, flipNormals);
        }
    }
}