package com.example.headunitapplication.controller;

import com.example.headunitapplication.CallbackObject;

import java.util.ArrayList;

public class PeriodicComponent extends Thread {

    private long iterationPeriodMs = 250;

    ArrayList<CallbackObject> callbacks = new ArrayList<>();

    public Object update() {
        return null;
    }

    public void run() {
        while (true) {

            Object generatedData = update();

            if (generatedData != null) {
                for (CallbackObject c : callbacks) {
                    c.update(generatedData);
                }
            }

            try {
                Thread.sleep(getIterationPeriodMs());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public long getIterationPeriodMs() {
        return iterationPeriodMs;
    }

    public void setIterationPeriodMs(long iterationPeriodMs) {
        this.iterationPeriodMs = iterationPeriodMs;
    }

    public void registerCallback(CallbackObject cb) {
        callbacks.add(cb);
    }
}
