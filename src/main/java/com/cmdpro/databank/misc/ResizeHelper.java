package com.cmdpro.databank.misc;

import com.cmdpro.databank.impact.ImpactFrameHandler;
import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.databank.shaders.PostShaderManager;

import java.util.ArrayList;
import java.util.List;

public class ResizeHelper {
    private static final List<ResizeListener> onResize = new ArrayList<>();
    public static void resize(int width, int height) {
        for (ResizeListener i : onResize) {
            i.resize(width, height);
        }
    }
    public static void addListener(ResizeListener listener) {
        if (!onResize.contains(listener)) {
            onResize.add(listener);
        }
    }
    public static void removeListener(ResizeListener listener) {
        onResize.remove(listener);
    }
    public interface ResizeListener {
        void resize(int width, int height);
    }
}
