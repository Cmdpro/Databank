package com.cmdpro.databank;

import com.cmdpro.databank.dialogue.styles.DialogueStyleManager;
import com.cmdpro.databank.impact.ImpactFrameHandler;
import com.cmdpro.databank.misc.ResizeHelper;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.music.MusicSystem;
import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.databank.shaders.PostShaderManager;

public class DatabankClient {
    protected static void addResizeListeners() {
        // Post-Process Shaders
        ResizeHelper.addListener((width, height) -> {
            for (PostShaderInstance i : PostShaderManager.instances) {
                i.resize(width, height);
            }
        });

        // Impact Frames
        ResizeHelper.addListener(ImpactFrameHandler::resize);
    }
    protected static void register() {
        DatabankModels.init();
        MusicSystem.init();
        DialogueStyleManager.init();
        addResizeListeners();
    }
}
