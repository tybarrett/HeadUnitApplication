package com.example.headunitapplication.uicomponents;

import com.example.headunitapplication.CallbackObject;
import com.example.headunitapplication.Room;

public class UIComponent<T> extends CallbackObject<T> {

    public UIComponent() {}

    public void listenToRoom(Room room) {
        room.add(this); // UpdateClass.update() will be called every
    }
}
