package com.cmdpro.databank.misc;

import com.cmdpro.databank.rendering.ColorUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class BaseGradient<T> {
    public List<GradientPoint> points = new ArrayList<>();
    public float startTime;
    public float endTime;
    public BaseGradient(T start, float startTime, T end, float endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        addPoint(start, startTime, true);
        addPoint(end, endTime, false);
    }
    public BaseGradient(T start, T end) {
        this(start, 0, end, 1);
    }
    public BaseGradient<T> addPoint(T value, float time, boolean instant) {
        GradientPoint point = new GradientPoint(value, time, instant);
        points.add(point);
        sort();
        return this;
    }
    public BaseGradient<T> addPoint(T value, float time) {
        return addPoint(value, time, false);
    }
    public T getValue(float time) {
        if (time < startTime) {
            return points.getFirst().value;
        }
        if (time > endTime) {
            return points.getLast().value;
        }
        List<GradientPoint> beforeList = new ArrayList<>(points.stream().filter((i) -> i.time <= time).toList());
        if (beforeList.isEmpty()) {
            return points.getFirst().value;
        }
        GradientPoint before = beforeList.getLast();
        List<GradientPoint> afterList = points.stream().filter((i) -> i.time > time).toList();
        if (afterList.isEmpty()) {
            return points.getLast().value;
        }
        GradientPoint after = afterList.getFirst();
        if (after.instant) {
            return before.value;
        }
        float progress = (time-before.time)/(after.time-before.time);
        return blend(before.value, after.value, progress);
    }
    public abstract T blend(T from, T to, float progress);
    public BaseGradient<T> sort() {
        points.sort(Comparator.comparing((i) -> i.time));
        return this;
    }
    protected class GradientPoint {
        public T value;
        public float time;
        public boolean instant;
        public GradientPoint(T value, float time, boolean instant) {
            this.value = value;
            this.time = time;
            this.instant = instant;
        }
    }
}
