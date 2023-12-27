package com.example.headunitapplication.controller;

import com.example.headunitapplication.VehicleStatusUpdater;


public class VehicleSpeed extends PeriodicComponent {

    VehicleStatusUpdater updater;

    public VehicleSpeed(VehicleStatusUpdater updater) {
        this.setIterationPeriodMs(200);
        this.updater = updater;
    }

    @Override
    public Object update() {
        return updater.getSpeed();
    }
}
