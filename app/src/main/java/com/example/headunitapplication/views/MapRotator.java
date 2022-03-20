package com.example.headunitapplication.views;

import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


public class MapRotator extends Thread {

    private static final int ROTATION_PERIOD_MS = 30 * 1000;

    private GoogleMap map = null;
    private long initTime = 0;
    private boolean isSpinning = false;


    public MapRotator(GoogleMap map) {
        this.map = map;
        initTime = System.currentTimeMillis();

        // When the map is loaded, begin spinning
        this.map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            public void onMapLoaded() {
                isSpinning = true;
            }
        });
    }

    public void run() {

        while (true) {

            if (isSpinning) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        LatLng currentTarget = map.getCameraPosition().target;

                        float tilt = map.getCameraPosition().tilt;
                        float zoom = map.getCameraPosition().zoom;

                        long timeAlive = System.currentTimeMillis() - initTime;
                        int bearing = (int) (timeAlive * 360 / ROTATION_PERIOD_MS);
                        bearing = (bearing % 360) - 180;
                        System.out.println(bearing);


                        CameraPosition rotatedPosition = new CameraPosition.Builder().
                                target(currentTarget).
                                zoom(zoom).
                                bearing(bearing).
                                tilt(tilt).
                                build();

                        map.moveCamera(CameraUpdateFactory.newCameraPosition(rotatedPosition));
                    }
                });
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
