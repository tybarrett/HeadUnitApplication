package com.example.headunitapplication.controller;

import com.example.headunitapplication.VehicleStatusUpdater;


public class DynoMode extends PeriodicComponent {

    VehicleStatusUpdater updater;

    public DynoMode(VehicleStatusUpdater updater) {
        this.updater = updater;

        start();
    }

    @Override
    public Object update() {
        return updater.isDynoMode();
    }
}
