package com.example.headunitapplication.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.headunitapplication.VehicleStatusUpdater;
import com.example.headunitapplication.models.AudioTrack;

public class CurrentlyPlayingSong extends PeriodicComponent {

    private VehicleStatusUpdater updater = null;

    public CurrentlyPlayingSong(VehicleStatusUpdater updater) {
        this.setIterationPeriodMs(500);
        this.updater = updater;

        start();
    }

    @Override
    public Object update() {
        // For some reason, periodically accessing the BroadcastReceiver allows it to consistently
        // receive its intents. Otherwise, it will stop receiving them after about 30 seconds.
        return updater.getCurrentSong();
    }
}
