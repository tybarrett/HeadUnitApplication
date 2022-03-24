package com.example.headunitapplication.controller;

import android.location.Location;
import android.location.LocationListener;
import android.telephony.CarrierConfigManager;

import com.example.headunitapplication.models.GpsPosition;

import java.util.Random;


public class GpsPositionUpdater extends PeriodicComponent implements LocationListener {

    private final static double MAX_LOCATION_NOISE = 0.00002;

    private double latestLat = 0.0;
    private double latestLon = 0.0;
    private double latestAccuracy = 0;
    private boolean receivedLocation = false;

    private Random r = new Random();

    public GpsPositionUpdater() {
        this.setIterationPeriodMs(100);
    }

    @Override
    public Object update() {
        if (receivedLocation) {

            double noisyLat = latestLat + (r.nextDouble() * MAX_LOCATION_NOISE);

            double noisyLon = latestLon + (r.nextDouble() * MAX_LOCATION_NOISE);

            return new GpsPosition(noisyLat, noisyLon, latestAccuracy);
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
