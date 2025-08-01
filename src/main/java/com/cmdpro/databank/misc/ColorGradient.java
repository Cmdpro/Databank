package com.cmdpro.databank.misc;

import com.cmdpro.databank.rendering.ColorUtil;

import java.awt.*;

public class ColorGradient extends BaseGradient<Color> {
    public ColorGradient(Color start, float startTime, Color end, float endTime) {
        super(start, startTime, end, endTime);
    }

    public ColorGradient(Color start, Color end) {
        super(start, end);
    }

    @Override
    public Color blend(Color from, Color to, float progress) {
        return ColorUtil.blendColors(from, to, progress);
    }

    public ColorGradient fadeAlpha(float from, float startTime, float to, float endTime) {
        Color startColor = getValue(startTime);
        Color endColor = getValue(endTime);
        addPoint(startColor, startTime);
        addPoint(endColor, endTime);
        for (GradientPoint i : points) {
            float progress = (i.time-startTime)/(endTime-startTime);
            float alpha = from+((to-from)*progress);
            if (i.time > endTime) {
                alpha = to;
            } else if (i.time < startTime) {
                alpha = from;
            }
            i.value = new Color(i.value.getRed(), i.value.getGreen(), i.value.getBlue(), (int)((alpha*255f)*((float)i.value.getAlpha()/255f)));
        }
        sort();
        return this;
    }
    public ColorGradient fadeAlpha(float from, float to) {
        return fadeAlpha(from, startTime, to, endTime);
    }
    @Override
    public ColorGradient addPoint(Color color, float time) {
        super.addPoint(color, time);
        return this;
    }
    @Override
    public ColorGradient addPoint(Color value, float time, boolean instant) {
        super.addPoint(value, time, instant);
        return this;
    }
    @Override
    public ColorGradient sort() {
        super.sort();
        return this;
    }
    public static ColorGradient singleColor(Color value) {
        return new ColorGradient(value, 0, value, 1);
    }
}
