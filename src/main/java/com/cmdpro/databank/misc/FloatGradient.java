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
    public FloatGradient addPoint(Float value, float time) {
        super.addPoint(value, time);
        return this;
    }
    @Override
    public FloatGradient addPoint(Float value, float time, boolean instant) {
        super.addPoint(value, time, instant);
        return this;
    }
    @Override
    public FloatGradient sort() {
        super.sort();
        return this;
    }
    public FloatGradient fade(float fromMult, float startTime, float toMult, float endTime) {
        float start = getValue(startTime);
        float end = getValue(endTime);
        addPoint(start, startTime);
        addPoint(end, endTime);
        for (GradientPoint i : points) {
            float progress = (i.time-startTime)/(endTime-startTime);
            float mult = fromMult+((toMult-fromMult)*progress);
            if (i.time > endTime) {
                mult = toMult;
            } else if (i.time < startTime) {
                mult = fromMult;
            }
            i.value *= mult;
        }
        sort();
        return this;
    }
    public FloatGradient fade(float fromMult, float toMult) {
        return fade(fromMult, startTime, toMult, endTime);
    }
    public static FloatGradient singleValue(Float value) {
        return new FloatGradient(value, 0, value, 1);
    }
}
