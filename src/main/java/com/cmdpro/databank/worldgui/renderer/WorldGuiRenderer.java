package com.cmdpro.databank.worldgui.renderer;

import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.RenderProjectionUtil;
import com.cmdpro.databank.worldgui.WorldGuiEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class WorldGuiRenderer extends EntityRenderer<WorldGuiEntity> {
    public WorldGuiRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(WorldGuiEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        if (entity.guiType != null && entity.gui != null) {
            MultiBufferSource.BufferSource source = RenderHandler.createBufferSource();
            Vec2 size = entity.guiType.getRenderSize();
            RenderProjectionUtil.project((graphics) -> {
                if (entity.gui != null) {
                    entity.gui.renderGui(graphics);
                }
            }, source, entity.getBoundsCorner(-1, 1), entity.getBoundsCorner(1, 1), entity.getBoundsCorner(1, -1), entity.getBoundsCorner(-1, -1), (int) size.x, (int) size.y);
        }
    }
    @Override
    public boolean shouldRender(WorldGuiEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(WorldGuiEntity entity) {
        return null;
    }
}
