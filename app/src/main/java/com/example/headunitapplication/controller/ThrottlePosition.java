package com.example.headunitapplication.controller;

import com.example.headunitapplication.VehicleStatusUpdater;


public class ThrottlePosition extends PeriodicComponent {
    VehicleStatusUpdater updater;

    public ThrottlePosition(VehicleStatusUpdater updater) {
        this.setIterationPeriodMs(200);
        this.updater = updater;

        start();
    }

    @Override
    public Object update() {
        return updater.getThrottle();
    }
}