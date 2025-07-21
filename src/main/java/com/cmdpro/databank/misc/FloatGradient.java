package com.cmdpro.databank.misc;

import com.cmdpro.databank.rendering.ColorUtil;
import org.joml.Math;

import java.awt.*;

public class FloatGradient extends BaseGradient<Float> {
    public FloatGradient(Float start, float startTime, Float end, float endTime) {
        super(start, startTime, end, endTime);
    }

    public FloatGradient(Float start, Float end) {
        super(start, end);
    }

    @Override
    public Float blend(Float from, Float to, float progress) {
        return Math.lerp(from, to, progress);
    }
    @Override
    public FloatGradient addPoint(Float color, float time) {
        super.addPoint(color, time);
        return this;
    }
    @Override
    public FloatGradient sort() {
        super.sort();
        return this;
    }
    public static FloatGradient singleValue(Float value) {
        return new FloatGradient(value, 0, value, 1);
    }
}
