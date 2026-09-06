package com.cmdpro.databank.shaders;

import com.cmdpro.databank.Databank;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.io.IOException;
import java.util.List;

import static com.mojang.blaze3d.platform.GlConst.GL_DRAW_FRAMEBUFFER;
import static com.mojang.blaze3d.platform.GlConst.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_INTERNAL_FORMAT;

public abstract class PostShaderInstance {
    public abstract ResourceLocation getShaderLocation();
    private PostChain postChain;
    public float time;
    public List<PostPass> passes;
    private boolean active;
    public PoseStack viewModelStack;
    private static RenderTarget depthBackupTarget;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (!active) {
            time = 0;
        }
        this.active = active;
    }

    public void resize(int pWidth, int pHeight) {
        if (postChain != null) {
            postChain.resize(pWidth, pHeight);
        }
    }

    public void queueRemoval() {
        if (!PostShaderManager.removalQueue.contains(this)) {
            PostShaderManager.removalQueue.add(this);
        }
    }

    public void process() {
        if (postChain == null) {
            try {
                postChain = new PostChain(Minecraft.getInstance().getTextureManager(), Minecraft.getInstance().getResourceManager(), Minecraft.getInstance().getMainRenderTarget(), getShaderLocation());
                postChain.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
                passes = postChain.passes;
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        if (postChain != null) {
            beforeProcess();
            if (active) {
                for (PostPass i : passes) {
                    i.getEffect().safeGetUniform("time").set(getTime());
                    i.getEffect().safeGetUniform("CameraPosition").set(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().toVector3f());
                    i.getEffect().safeGetUniform("ModelViewMat").set(RenderSystem.getModelViewMatrix());
                    i.getEffect().safeGetUniform("invViewMat").set(new Matrix4f(PostShaderManager.viewStackMatrix).invert());
                    i.getEffect().safeGetUniform("invProjMat").set(new Matrix4f(RenderSystem.getProjectionMatrix()).invert());
                    setUniforms(i);
                }
                processPostChain();
                afterProcess();
            }
        }
    }

    public void processPostChain() {
        var mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
        getDepthBackupTarget().copyDepthFrom(mainRenderTarget);

        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.resetTextureMatrix();
        postChain.process(Minecraft.getInstance().getTimer().getGameTimeDeltaTicks());
        mainRenderTarget.copyDepthFrom(getDepthBackupTarget());

        GlStateManager._bindTexture(mainRenderTarget.getDepthTextureId());
        var mainFormat = GlStateManager._getTexLevelParameter(GL_TEXTURE_2D, 0, GL_TEXTURE_INTERNAL_FORMAT);
        GlStateManager._bindTexture(depthBackupTarget.getDepthTextureId());
        var backupFormat = GlStateManager._getTexLevelParameter(GL_TEXTURE_2D, 0, GL_TEXTURE_INTERNAL_FORMAT);
        Databank.LOGGER.info("Main: {}, Backup: {}", mainFormat, backupFormat);

        mainRenderTarget.bindWrite(true);
    }

    protected static RenderTarget getDepthBackupTarget() {
        if (depthBackupTarget == null) {
            var mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
            depthBackupTarget = new MainTarget(mainRenderTarget.width, mainRenderTarget.height);
            if (mainRenderTarget.isStencilEnabled())
                getDepthBackupTarget().enableStencil();
        }
        return depthBackupTarget;
    }

    public void tick() {
        time += 1/20f;
    }

    public void setUniforms(PostPass instance) {}

    public void beforeProcess() {}

    public void afterProcess() {}

    public float getTime() {
        return time + (Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true) / 20f);
    }

    public RenderLevelStageEvent.Stage getRenderStage() {
        return RenderLevelStageEvent.Stage.AFTER_LEVEL;
    }
}
