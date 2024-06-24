package com.cmdpro.cmdlib.shaders;

import java.util.ArrayList;
import java.util.List;

public class PostShaderManager {
    public static List<PostShaderInstance> instances = new ArrayList<>();
    public static void addShader(PostShaderInstance instance) {
        instances.add(instance);
    }
}
