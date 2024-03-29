package com.example.headunitapplication;

import java.util.HashMap;

public class CommunicationBackhaul {
    HashMap<String, Room> roomsMap;

    public Room getRoom(String roomName) {
        if (!(roomsMap.containsKey(roomName))) {
            roomsMap.put(roomName, new Room<>(roomName));
        }
        return roomsMap.get(roomName);
    }

    public void sendToRoom(String roomName, Object publishedObject) {
        if (!(roomsMap.containsKey(roomName))) {
            roomsMap.put(roomName, new Room<>(roomName));
        }

        Room room = roomsMap.get(roomName);
        for (int i=0; i < room.size(); i++) {
            final CallbackObject cbo = (CallbackObject) room.get(i);
            new Thread(() -> cbo.update(publishedObject)).start();
        }
    }
}
