package com.example.headunitapplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class VehicleStatusUpdater {

    private int rpm = -1;
    private double speed = -1.0;
    private int gear = -1;

    MulticastReceiver multicastReceiver;

    // la de da de da
    public VehicleStatusUpdater() {
        multicastReceiver = new MulticastReceiver(new ReceiveDataCallback());
    }

    class ReceiveDataCallback extends CallbackObject<String> {
        @Override
        public void safe_update(String receivedData) {
            try {
                JSONObject jsonObject = null;
                jsonObject = new JSONObject(receivedData);
                String metricName = jsonObject.getString("metricName");
                metricName = metricName.toUpperCase(Locale.ROOT);
                if (metricName.equals("RPM")) {
                    rpm = jsonObject.getInt("value");
                } else if (metricName.equals("SPEED")) {
                    speed = jsonObject.getDouble("value");
                } else if (metricName.equals("GEAR")) {
                    gear = jsonObject.getInt("value");
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

    public double getSpeed() {
        return speed;
    }

    public int getGear() {
        return gear;
    }



}
