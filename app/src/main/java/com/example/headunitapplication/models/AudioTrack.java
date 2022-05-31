package com.example.headunitapplication.models;

public class AudioTrack {
    String audio;
    String artist;
    String album;

    public AudioTrack(String audio, String artist, String album) {
        this.audio = audio;
        this.artist = artist;
        this.album = album;
    }

    public AudioTrack() {}

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
