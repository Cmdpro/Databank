package com.cmdpro.databank.misc;

import org.joml.Math;

import java.awt.*;

public class DoubleGradient extends BaseGradient<Double> {
    public DoubleGradient(Double start, float startTime, Double end, float endTime) {
        super(start, startTime, end, endTime);
    }

    public DoubleGradient(Double start, Double end) {
        super(start, end);
    }

    @Override
    public Double blend(Double from, Double to, float progress) {
        return Math.lerp(from, to, progress);
    }
    @Override
    public DoubleGradient addPoint(Double value, float time) {
        super.addPoint(value, time);
        return this;
    }
    @Override
    public DoubleGradient addPoint(Double value, float time, boolean instant) {
        super.addPoint(value, time, instant);
        return this;
    }
    @Override
    public DoubleGradient sort() {
        super.sort();
        return this;
    }
    public DoubleGradient fade(double fromMult, float startTime, double toMult, float endTime) {
        double start = getValue(startTime);
        double end = getValue(endTime);
        addPoint(start, startTime);
        addPoint(end, endTime);
        for (GradientPoint i : points) {
            float progress = (i.time-startTime)/(endTime-startTime);
            double mult = fromMult+((toMult-fromMult)*progress);
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
    public DoubleGradient fade(double fromMult, double toMult) {
        return fade(fromMult, startTime, toMult, endTime);
    }
    public static DoubleGradient singleValue(Double value) {
        return new DoubleGradient(value, 0, value, 1);
    }
}
