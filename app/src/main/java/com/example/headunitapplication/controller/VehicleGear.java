package com.example.headunitapplication.controller;

import com.example.headunitapplication.VehicleStatusUpdater;


public class VehicleGear extends PeriodicComponent {

    VehicleStatusUpdater updater;

    public VehicleGear(VehicleStatusUpdater updater) {
        this.setIterationPeriodMs(200);
        this.updater = updater;

        start();
    }

    @Override
    public Object update() {
        return updater.getGear();
    }
}