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
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.headunitapplication.controller.CurrentlyPlayingSong;
import com.example.headunitapplication.controller.EngineEffortRpm;
import com.example.headunitapplication.controller.GpsPositionUpdater;
import com.example.headunitapplication.controller.ThrottlePosition;
import com.example.headunitapplication.controller.VehicleSpeed;
import com.example.headunitapplication.models.AudioTrack;
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

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.UUID;

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

    private boolean initialized = false;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String[] location_permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        requestPermissions(location_permissions, 0);

        if (!initialized) {
            CurrentlyPlayingSong audioUpdater = new CurrentlyPlayingSong();
            audioUpdater.registerIntentReceiver(this);
            audioUpdater.registerCallback(new AudioTrackUpdater());
            audioUpdater.start();

            initialized = true;
        }

        gpsPositionUpdater = new GpsPositionUpdater();
        gpsPositionUpdater.registerCallback(new LocationUpdater());
        gpsPositionUpdater.start();

        LocationManager locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationmanager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 5000, 10, gpsPositionUpdater);

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            // Device does not support Bluetooth
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice dev = null;
        for (BluetoothDevice device : pairedDevices) {
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address
            dev = device;
            // TODO - save the MAC address of the OBD2 scanner
        }

        // Lifted from github.com/pires/android-obd-reader. Not sure if applicable for our case.
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        BluetoothSocket sock = null;
        try {
            sock = dev.createRfcommSocketToServiceRecord(uuid);
        } catch (Exception e) {
            System.err.println("Unable to create Rfcomm socket.");
            e.printStackTrace();
        }

        if (sock != null) {
            try {
                InputStream is = sock.getInputStream();
                OutputStream os = sock.getOutputStream();

                VehicleSpeed vehicleSpeed = new VehicleSpeed();
                vehicleSpeed.setInputStream(is);
                vehicleSpeed.setOutputStream(os);

                ThrottlePosition throttlePosition = new ThrottlePosition();
                throttlePosition.setInputStream(is);
                throttlePosition.setOutputStream(os);

                EngineEffortRpm engineEffortRpm = new EngineEffortRpm();
                engineEffortRpm.setInputStream(is);
                engineEffortRpm.setOutputStream(os);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            String northSouth = "N ";
            double lat = pos.getLat();
            if (lat < 0) {
                northSouth = "S ";
                lat = Math.abs(lat);
            }
            int latDegrees = (int) lat;
            String latDegreesString = degreesFormat.format(latDegrees);
            double minutes = (lat % 1.0) * 60.0;
            String minutesString = decimalFormat.format(minutes);
            latitudeView.setText(northSouth + latDegreesString + "° " + minutesString);

            TextView longitude = findViewById(R.id.longitude);
            String eastWest = "E ";
            double lon = pos.getLon();
            if (lon < 0) {
                eastWest = "W ";
                lon = Math.abs(lon);
            }
            int lonDegrees = (int) lon;
            String lonDegreesString = degreesFormat.format(lonDegrees);
            double lonMinutes = (lon % 1.0) * 60;
            String lonMinutesString = decimalFormat.format(lonMinutes);
            longitude.setText(eastWest + lonDegreesString + "° " + lonMinutesString);
        }
    }

    class AudioTrackUpdater extends CallbackObject<AudioTrack> {
        @Override
        public void safe_update(AudioTrack audio) {
            runOnUiThread(() -> {
                TextView songName = findViewById(R.id.song_name);
                songName.setText(audio.getAudio());
                TextView artistName = findViewById(R.id.song_artist);
                artistName.setText(audio.getArtist());
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
