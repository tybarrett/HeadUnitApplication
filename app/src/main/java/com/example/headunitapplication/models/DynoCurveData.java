package com.example.headunitapplication.models;

/** The car's dyno curve. */
public final class DynoCurveData {

    // Placeholder figures until the car's dyno-sheet numbers are entered.
    static final int[] RPM = {
            1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 5500, 6000, 6500, 7000
    };
    static final int[] HP = {
            29, 49, 72, 96, 120, 143, 164, 183, 200, 213, 223, 223, 213
    };
    static final int[] TORQUE_LB_FT = {
            150, 170, 190, 202, 210, 214, 215, 213, 210, 203, 195, 180, 160
    };

    private DynoCurveData() {
    }

    public static DynoCurve curve() {
        return new DynoCurve(RPM, HP, TORQUE_LB_FT);
    }
}
