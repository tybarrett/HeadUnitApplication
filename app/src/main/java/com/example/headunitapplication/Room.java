package com.example.headunitapplication;

import java.util.ArrayList;

public class Room<T> extends ArrayList<CallbackObject<T>> {
    String roomName;

    public Room(String roomName) {
        this.roomName = roomName;
    }
}
