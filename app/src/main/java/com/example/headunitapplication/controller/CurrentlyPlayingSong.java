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
        this.setIterationPeriodMs(100);

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
