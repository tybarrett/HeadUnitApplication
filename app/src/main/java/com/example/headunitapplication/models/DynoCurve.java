package com.example.headunitapplication.models;

import java.util.Arrays;

/** An engine's horsepower and torque across its RPM range. */
public class DynoCurve {

    private final int[] rpm;
    private final int[] hp;
    private final int[] torque;

    public DynoCurve(int[] rpm, int[] hp, int[] torque) {
        if (rpm.length < 2 || hp.length != rpm.length || torque.length != rpm.length) {
            throw new IllegalArgumentException(
                    "rpm, hp and torque must be the same length, with at least two points");
        }
        for (int i = 1; i < rpm.length; i++) {
            if (rpm[i] <= rpm[i - 1]) {
                throw new IllegalArgumentException("rpm must be strictly increasing");
            }
        }

        this.rpm = Arrays.copyOf(rpm, rpm.length);
        this.hp = Arrays.copyOf(hp, hp.length);
        this.torque = Arrays.copyOf(torque, torque.length);
    }

    public int minRpm() {
        return rpm[0];
    }

    public int maxRpm() {
        return rpm[rpm.length - 1];
    }

    public int pointCount() {
        return rpm.length;
    }

    public int rpmPoint(int index) {
        return rpm[index];
    }

    public int hpPoint(int index) {
        return hp[index];
    }

    public int torquePoint(int index) {
        return torque[index];
    }

    public int peakHp() {
        return max(hp);
    }

    public int peakTorque() {
        return max(torque);
    }

    /** Horsepower at the given RPM, interpolated and clamped to the curve's range. */
    public double hpAt(double atRpm) {
        return interpolate(hp, atRpm);
    }

    /** Torque at the given RPM, interpolated and clamped to the curve's range. */
    public double torqueAt(double atRpm) {
        return interpolate(torque, atRpm);
    }

    /** Where the given RPM falls across the curve's range, from 0 at the minimum to 1 at the maximum. */
    public double rpmFraction(double atRpm) {
        double fraction = (atRpm - minRpm()) / (double) (maxRpm() - minRpm());
        return Math.max(0.0, Math.min(1.0, fraction));
    }

    private double interpolate(int[] values, double atRpm) {
        int last = rpm.length - 1;
        if (atRpm <= rpm[0]) {
            return values[0];
        }
        if (atRpm >= rpm[last]) {
            return values[last];
        }

        int upper = 1;
        while (rpm[upper] < atRpm) {
            upper++;
        }
        int lower = upper - 1;
        double t = (atRpm - rpm[lower]) / (double) (rpm[upper] - rpm[lower]);
        return values[lower] + t * (values[upper] - values[lower]);
    }

    private static int max(int[] values) {
        int result = values[0];
        for (int value : values) {
            result = Math.max(result, value);
        }
        return result;
    }
}
