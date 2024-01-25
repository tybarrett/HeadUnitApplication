package com.example.headunitapplication.controller;

import com.example.headunitapplication.VehicleStatusUpdater;


public class EngineEffortRpm extends PeriodicComponent {

    VehicleStatusUpdater updater;

    public EngineEffortRpm(VehicleStatusUpdater updater) {
        this.setIterationPeriodMs(200);
        this.updater = updater;

        start();
    }

    @Override
    public Object update() {
        return updater.getRpm();
    }
}