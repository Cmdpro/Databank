package com.cmdpro.databank.misc;

import org.joml.Math;

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
    public DoubleGradient addPoint(Double color, float time) {
        super.addPoint(color, time);
        return this;
    }
    @Override
    public DoubleGradient sort() {
        super.sort();
        return this;
    }
    public static DoubleGradient singleValue(Double value) {
        return new DoubleGradient(value, 0, value, 1);
    }
}
