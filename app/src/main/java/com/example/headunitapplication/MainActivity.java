package com.example.headunitapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.headunitapplication.controller.GpsPositionUpdater;
import com.example.headunitapplication.models.GpsPosition;
import com.example.headunitapplication.views.MapRotator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final CameraPosition SYDNEY =
//            new CameraPosition.Builder().target(new LatLng(-33.87365, 151.20689))
            new CameraPosition.Builder().target(new LatLng(39.11543272916332, -77.16574878472404))
                    .zoom(18)
                    .bearing(0)
                    .tilt(45)
                    .build();

    private GoogleMap map;
    private MapRotator mapRotator;
    private GpsPositionUpdater gpsPositionUpdater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String[] location_permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        requestPermissions(location_permissions, 0);

        gpsPositionUpdater = new GpsPositionUpdater();
        gpsPositionUpdater.registerCallback(new LocationUpdater());
        gpsPositionUpdater.start();

        LocationManager locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationmanager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 5000, 10, gpsPositionUpdater);
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

    class LocationUpdater extends CallbackObject<GpsPosition> {

        DecimalFormat decimalFormat = new DecimalFormat("##.000000");

        @Override
        public void safe_update(GpsPosition pos) {
            TextView latitudeView = findViewById(R.id.latitude);
            String northSouth = " N";
            double lat = pos.getLat();
            if (lat < 0) {
                northSouth = " S";
                lat = Math.abs(lat);
            }
            String latString = decimalFormat.format(lat);
            latitudeView.setText(latString + northSouth);

            TextView longitude = findViewById(R.id.longitude);
            String eastWest = " E";
            double lon = pos.getLon();
            if (lon < 0) {
                eastWest = " W";
                lon = Math.abs(lon);
            }
            String lonString = decimalFormat.format(lon);
            longitude.setText(lonString + eastWest);
        }
    }

    /* "Widgets" to have:
    - Current song / artist
    - "Engine effort" (RPM)
    - Throttle Position
    - Current Gear
    - Vehicle speed
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
