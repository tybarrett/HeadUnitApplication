package com.example.headunitapplication;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(SYDNEY), null);
    }

    /* "Widgets" to have:
    - Current song / artist
    - "Engine effort" (RPM)
    - Vehicle speed
    - Battery level?
    - Engine temperature?
    - Current posted limit? (use OSM API)
    - "Safety distance" (Distance to next speed camera)

    Additional things to have:
    - Idle animation
    - Depth of field effect? (blur some elements while others are in focus?)
    - put OSM streets on the map in a 3D view? (ooh yeah that's a good idea)
     */
}
