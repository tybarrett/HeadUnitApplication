package com.example.headunitapplication;

import android.net.wifi.WifiManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class VehicleStatusUpdater extends CommunicationBackhaul {

    private int rpm = -1;
    private int speed = -1;
    private int gear = -1;
    private double throttle = -1.0;

    MulticastReceiver multicastReceiver;

    public VehicleStatusUpdater(WifiManager wifiManager) {
        multicastReceiver = new MulticastReceiver(new ReceiveDataCallback(), wifiManager);
    }

    class ReceiveDataCallback extends CallbackObject<String> {
        @Override
        public void safe_update(String receivedData) {
            try {
                JSONObject jsonObject = new JSONObject(receivedData);
                String metricName = jsonObject.getString("metricName");
                metricName = metricName.toUpperCase(Locale.ROOT);
                if (metricName.equals("RPM")) {
                    rpm = jsonObject.getInt("value");
                    sendToRoom("RPM", rpm);
                } else if (metricName.equals("SPEED")) {
                    speed = jsonObject.getInt("value");
                    sendToRoom("SPEED", speed);
                } else if (metricName.equals("GEAR")) {
                    gear = jsonObject.getInt("value");
                    sendToRoom("GEAR", gear);
                } else if (metricName.equals("THROTTLE")) {
                    throttle = jsonObject.getDouble("value");
                    sendToRoom("THROTTLE", throttle);
                } else {
                    System.out.println("Encountered unrecognized metric: " + metricName);
                }
            } catch (JSONException e) {
                System.out.println("Encountered a JSONException with the following received String:");
                System.out.println(receivedData);
                e.printStackTrace();
            }
        }
    }

    public int getRpm() {
        return rpm;
    }

    public int getSpeed() {
        return speed;
    }

    public int getGear() {
        return gear;
    }

    public double getThrottle() {
        return throttle;
    }
}
