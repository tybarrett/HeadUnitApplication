package com.example.headunitapplication.controller;

import android.location.Location;
import android.location.LocationListener;

import com.example.headunitapplication.models.GpsPosition;


public class GpsPositionUpdater extends PeriodicComponent implements LocationListener {

    private double latestLat = 0.0;
    private double latestLon = 0.0;
    private double latestAccuracy = 0;
    private boolean receivedLocation = false;

    @Override
    public Object update() {
        if (receivedLocation) {
            return new GpsPosition(latestLat, latestLon, latestAccuracy);
        } else {
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location loc) {

        latestLat = loc.getLatitude();
        latestLon = loc.getLongitude();
        latestAccuracy = loc.getAccuracy();

        receivedLocation = true;
    }

}
