package com.example.headunitapplication.controller;

import com.example.headunitapplication.VehicleStatusUpdater;

public class TelemetryStatus extends PeriodicComponent {

    VehicleStatusUpdater updater;

    public TelemetryStatus(VehicleStatusUpdater updater) {
        this.setIterationPeriodMs(1000);
        this.updater = updater;

        start();
    }

    @Override
    public Object update() {
        return updater.getLastHeartbeatTime();
    }
}
