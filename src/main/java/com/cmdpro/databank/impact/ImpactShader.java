package com.cmdpro.databank.impact;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.config.DatabankClientConfig;
import com.cmdpro.databank.shaders.PostShaderInstance;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;

public class ImpactShader extends PostShaderInstance {
    @Override
    public ResourceLocation getShaderLocation() {
        return Databank.locate("shaders/post/impact.json");
    }
    @Override
    public void setUniforms(PostPass instance) {
        super.setUniforms(instance);
        instance.getEffect().setSampler("ImpactSampler", ImpactFrameHandler.getImpactTarget()::getColorTextureId);
        instance.getEffect().setSampler("FrozenImpactSampler", ImpactFrameHandler.getFrozenImpactTarget()::getColorTextureId);
        float progress = 0;
        if (ImpactFrameHandler.impactFrame != null) {
            float maxProgress = (float) ImpactFrameHandler.impactFrame.startTicks / 20f;
            progress = getTime() / maxProgress;
        }
        instance.getEffect().safeGetUniform("alpha").set(ImpactFrameHandler.impactFrame != null ? ImpactFrameHandler.impactFrame.alpha.getValue(progress) : 0f);
    }

    @Override
    public void beforeProcess() {
        super.beforeProcess();
        if (ImpactFrameHandler.impactFrame == null) {
            setActive(false);
        }
        if (!DatabankClientConfig.allowImpactVisuals) {
            setActive(false);
        }
    }
}
