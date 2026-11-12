package com.example.headunitapplication.models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DynoCurveTest {

    private static final double DELTA = 1e-9;

    private static final DynoCurve CURVE = new DynoCurve(
            new int[]{1000, 2000, 4000},
            new int[]{100, 200, 300},
            new int[]{50, 150, 250});

    @Test
    public void interpolatesAtSamplePoints() {
        assertEquals(200, CURVE.hpAt(2000), DELTA);
        assertEquals(150, CURVE.torqueAt(2000), DELTA);
    }

    @Test
    public void interpolatesBetweenPoints() {
        assertEquals(150, CURVE.hpAt(1500), DELTA);
        assertEquals(200, CURVE.torqueAt(3000), DELTA);
    }

    @Test
    public void clampsOutsideRange() {
        assertEquals(100, CURVE.hpAt(500), DELTA);
        assertEquals(300, CURVE.hpAt(9000), DELTA);
        assertEquals(50, CURVE.torqueAt(-1), DELTA);
        assertEquals(250, CURVE.torqueAt(9000), DELTA);
    }

    @Test
    public void reportsRangeAndPeaks() {
        assertEquals(1000, CURVE.minRpm());
        assertEquals(4000, CURVE.maxRpm());
        assertEquals(300, CURVE.peakHp());
        assertEquals(250, CURVE.peakTorque());
    }

    @Test
    public void mapsRpmToFractionOfRange() {
        assertEquals(0.0, CURVE.rpmFraction(1000), DELTA);
        assertEquals(0.5, CURVE.rpmFraction(2500), DELTA);
        assertEquals(1.0, CURVE.rpmFraction(4000), DELTA);
    }

    @Test
    public void clampsFractionOutsideRange() {
        assertEquals(0.0, CURVE.rpmFraction(0), DELTA);
        assertEquals(1.0, CURVE.rpmFraction(8000), DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMismatchedLengths() {
        new DynoCurve(new int[]{1000, 2000}, new int[]{1, 2}, new int[]{1});
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsRpmThatDoesNotIncrease() {
        new DynoCurve(new int[]{1000, 1000}, new int[]{1, 2}, new int[]{1, 2});
    }

    @Test
    public void hardCodedDataArraysAreTheSameLength() {
        assertEquals(DynoCurveData.RPM.length, DynoCurveData.HP.length);
        assertEquals(DynoCurveData.RPM.length, DynoCurveData.TORQUE_LB_FT.length);
    }

    @Test
    public void hardCodedRpmIsStrictlyIncreasing() {
        for (int i = 1; i < DynoCurveData.RPM.length; i++) {
            assertTrue("rpm at index " + i + " does not increase",
                    DynoCurveData.RPM[i] > DynoCurveData.RPM[i - 1]);
        }
    }

    @Test
    public void hardCodedCurveBuilds() {
        DynoCurve curve = DynoCurveData.curve();
        assertEquals(DynoCurveData.RPM.length, curve.pointCount());
    }
}
