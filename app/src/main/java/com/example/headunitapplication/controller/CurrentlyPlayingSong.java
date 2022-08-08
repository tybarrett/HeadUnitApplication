package com.example.headunitapplication.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.headunitapplication.models.AudioTrack;

public class CurrentlyPlayingSong extends PeriodicComponent {

    private AudioTrack currentSong;
    private IntentFilter iF;

    public CurrentlyPlayingSong() {
        this.setIterationPeriodMs(500);

        iF = new IntentFilter();
        iF.addAction("com.spotify.music.metadatachanged");
        iF.addAction("com.spotify.music.playbackstatechanged");
        iF.addAction("com.spotify.music.playbackcomplete");
        iF.addAction("com.spotify.music.queuechanged");
    }

    public void registerIntentReceiver(AppCompatActivity activity) {
        activity.registerReceiver(mReceiver, iF);
    }

    @Override
    public Object update() {
        // For some reason, periodically accessing the BroadcastReceiver allows it to consistently
        // receive its intents. Otherwise, it will stop receiving them after about 30 seconds.
        // TODO - determine why this is the case, and fix the root cause.
        if (mReceiver != null);
        return currentSong;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            Log.v("tag ", action + " / " + cmd);
            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
            currentSong = new AudioTrack(track, artist, album);
        }
    };
}
