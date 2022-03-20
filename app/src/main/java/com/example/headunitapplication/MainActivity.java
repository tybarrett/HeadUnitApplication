package com.example.headunitapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final CameraPosition SYDNEY =
            new CameraPosition.Builder().target(new LatLng(-33.87365, 151.20689))
                    .zoom(18)
                    .bearing(0)
                    .tilt(45)
                    .build();

    private GoogleMap map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String[] location_permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        requestPermissions(location_permissions, 0);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style));
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);

        if (!success) {
            System.err.println("Unable to parse style JSON.");
        }

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(SYDNEY));

        mapRotator = new MapRotator(googleMap);
        mapRotator.start();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        map.setMyLocationEnabled(true);
    }

    /* "Widgets" to have:
    - Current song / artist
    - "Engine effort" (RPM)
    - Throttle Position
    - Current Gear
    - Vehicle speed
    - Battery level?
    - Engine temperature?
    - Car Battery level?
    - Current posted limit? (use OSM API)
    - "Safety distance" (Distance to next speed camera)

    Additional things to have:
    - Idle animation
    - Depth of field effect? (blur some elements while others are in focus?)
    - put OSM streets on the map in a 3D view? (ooh yeah that's a good idea)
     */
}
