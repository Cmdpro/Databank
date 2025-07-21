package com.cmdpro.databank.misc;

import com.cmdpro.databank.rendering.ColorUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Gradient {
    public List<GradientPoint> points = new ArrayList<>();
    public float startTime;
    public float endTime;
    public Gradient(Color start, float startTime, Color end, float endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        addPoint(start, startTime);
        addPoint(end, endTime);
    }
    public Gradient(Color start, Color end) {
        this(start, 0, end, 1);
    }
    public static Gradient singleColor(Color color) {
        return new Gradient(color, 0, color, 1);
    }
    public Gradient fadeAlpha(float from, float startTime, float to, float endTime) {
        for (GradientPoint i : points) {
            float progress = (i.time-startTime)/(endTime-startTime);
            float alpha = from+((to-from)*progress);
            i.color = new Color(i.color.getRed(), i.color.getGreen(), i.color.getBlue(), (int)(alpha*255f));
        }
        sort();
        return this;
    }
    public Gradient fadeAlpha(float from, float to) {
        return fadeAlpha(from, startTime, to, endTime);
    }
    public Gradient addPoint(Color color, float time) {
        GradientPoint point = new GradientPoint(color, time);
        points.add(point);
        sort();
        return this;
    }
    public Color getColor(float time) {
        if (time < startTime) {
            return points.getFirst().color;
        }
        if (time > endTime) {
            return points.getLast().color;
        }
        List<GradientPoint> beforeList = new ArrayList<>(points.stream().filter((i) -> i.time <= time).toList());
        if (beforeList.isEmpty()) {
            return points.getFirst().color;
        }
        GradientPoint before = beforeList.getLast();
        List<GradientPoint> afterList = points.stream().filter((i) -> i.time > time).toList();
        if (afterList.isEmpty()) {
            return points.getLast().color;
        }
        GradientPoint after = afterList.getFirst();
        float progress = (time-before.time)/(after.time-before.time);
        return ColorUtil.blendColors(before.color, after.color, progress);
    }
    public Gradient sort() {
        points.sort(Comparator.comparing((i) -> i.time));
        return this;
    }
    public static class GradientPoint {
        public Color color;
        public float time;
        public GradientPoint(Color color, float time) {
            this.color = color;
            this.time = time;
        }
    }
}
