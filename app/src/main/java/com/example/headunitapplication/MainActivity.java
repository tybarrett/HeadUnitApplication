package com.example.headunitapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.headunitapplication.controller.CurrentlyPlayingSong;
import com.example.headunitapplication.controller.EngineEffortRpm;
import com.example.headunitapplication.controller.GpsPositionUpdater;
import com.example.headunitapplication.controller.ThrottlePosition;
import com.example.headunitapplication.controller.VehicleGear;
import com.example.headunitapplication.controller.VehicleSpeed;
import com.example.headunitapplication.models.AudioTrack;
import com.example.headunitapplication.models.GpsPosition;
import com.example.headunitapplication.uicomponents.ThrottleView;
import com.example.headunitapplication.uicomponents.ThrottleViewController;
import com.example.headunitapplication.views.MapRotator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    VehicleStatusUpdater vehicleStatusUpdater;
    EngineEffortRpm engineEffortComponent;
    ThrottlePosition throttleComponent;
    VehicleSpeed speedComponent;
    VehicleGear gearComponent;

    ThrottleViewController throttle;

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

    private RemoteErrorLogger remoteErrorLogger;

    private boolean initialized = false;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        remoteErrorLogger = new RemoteErrorLogger();
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.main_layout);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            String[] location_permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            requestPermissions(location_permissions, 0);

            

            if (!initialized) {
                throttle = new ThrottleViewController(findViewById(R.id.throttle_layout));
                throttle.listenToRoom(backend.getRoom("THROTTLE"));

//                CurrentlyPlayingSong audioUpdater = new CurrentlyPlayingSong();
//                audioUpdater.registerIntentReceiver(this);
//                audioUpdater.registerCallback(new AudioTrackUpdater());
//                audioUpdater.start();
//
//                WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
//                vehicleStatusUpdater = new VehicleStatusUpdater(wifiManager);
//
//                engineEffortComponent = new EngineEffortRpm(vehicleStatusUpdater);
//                engineEffortComponent.registerCallback(new EffortUpdater());
//
//                throttleComponent = new ThrottlePosition(vehicleStatusUpdater);
//                throttleComponent.registerCallback(new ThrottleUpdater());
//
//                speedComponent = new VehicleSpeed(vehicleStatusUpdater);
//                speedComponent.registerCallback(new SpeedUpdater());
//
//                gearComponent = new VehicleGear(vehicleStatusUpdater);
//                gearComponent.registerCallback(new GearUpdater());
//
//                initialized = true;
            }

            gpsPositionUpdater = new GpsPositionUpdater();
            gpsPositionUpdater.registerCallback(new LocationUpdater());
            gpsPositionUpdater.start();

            LocationManager locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationmanager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 5000, 10, gpsPositionUpdater);



        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
//            remoteErrorLogger.write_log(stackTrace);

            TextView errorOutput = findViewById(R.id.errorBox);
            errorOutput.setText(stackTrace);


        }
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

        DecimalFormat decimalFormat = new DecimalFormat("00.000");
        DecimalFormat degreesFormat = new DecimalFormat("000");

        @Override
        public void safe_update(GpsPosition pos) {
            TextView latitudeView = findViewById(R.id.latitude);
            final String northSouth;
            double lat = pos.getLat();
            if (lat < 0) {
                northSouth = "S ";
                lat = Math.abs(lat);
            } else {
                 northSouth = "N ";
            }
            int latDegrees = (int) lat;
            String latDegreesString = degreesFormat.format(latDegrees);
            double minutes = (lat % 1.0) * 60.0;
            String minutesString = decimalFormat.format(minutes);

            TextView longitude = findViewById(R.id.longitude);
            final String eastWest;
            double lon = pos.getLon();
            if (lon < 0) {
                eastWest = "W ";
                lon = Math.abs(lon);
            } else {
                eastWest = "E";
            }

            int lonDegrees = (int) lon;
            String lonDegreesString = degreesFormat.format(lonDegrees);
            double lonMinutes = (lon % 1.0) * 60;
            String lonMinutesString = decimalFormat.format(lonMinutes);

            TextView delusionText = findViewById(R.id.delusion);
            String base = "Positional Delusion: ";
            double delusion = pos.getAccuracy();
            String delusionString = decimalFormat.format(delusion);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    latitudeView.setText(northSouth + latDegreesString + "° " + minutesString);
                    longitude.setText(eastWest + lonDegreesString + "° " + lonMinutesString);
                    delusionText.setText(base + delusionString + "m");
                }
            });
        }
    }

    class SpeedUpdater extends CallbackObject<Integer> {
        @Override
        public void safe_update(Integer newSpeed) {
            runOnUiThread(() -> {
                TextView speedText = findViewById(R.id.speed);
                speedText.setText(String.valueOf(newSpeed));
            });
        }
    }

    class EffortUpdater extends CallbackObject<Integer> {
        public int MAX_EFFORT_LEVEL = 9000;
        @Override
        public void safe_update(Integer newEffort) {
            runOnUiThread(() -> {
                TextView effortText = findViewById(R.id.effort_percentage);
                int effort_percent = 100 * newEffort / MAX_EFFORT_LEVEL;
                effortText.setText(String.format("%s%%", effort_percent));
                LinearProgressIndicator effortProgressBar = findViewById(R.id.effort_progress_bar);
                effortProgressBar.setProgress(effort_percent);
            });
        }
    }

    class GearUpdater extends CallbackObject<Integer> {
        @Override
        public void safe_update(Integer newGear) {
            runOnUiThread(() -> {
                TextView gearText = findViewById(R.id.gear);
                TextView gearSuffix = findViewById(R.id.gearSuffix);
                gearText.setText(String.valueOf(newGear));
                if (newGear == 1) {
                    gearSuffix.setText("ST");
                } else if (newGear == 2) {
                    gearSuffix.setText("ND");
                } else if (newGear == 3) {
                    gearSuffix.setText("RD");
                } else {
                    gearSuffix.setText("TH");
                }
            });
        }
    }

    class AudioTrackUpdater extends CallbackObject<AudioTrack> {
        @Override
        public void safe_update(AudioTrack audio) {
            runOnUiThread(() -> {
                TextView songName = findViewById(R.id.song_name);
                String currentText = songName.getText().toString();
                if (!currentText.equals(audio.getAudio())) {
                    songName.setText(audio.getAudio());
                    songName.setSelected(true);
                }

                TextView artistName = findViewById(R.id.song_artist);
                String currentArtist = artistName.getText().toString();
                if (!currentArtist.equals(audio.getArtist())) {
                    artistName.setText(audio.getArtist());
                    artistName.setSelected(true);
                }
            });
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
