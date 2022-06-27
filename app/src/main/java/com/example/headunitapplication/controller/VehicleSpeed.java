package com.example.headunitapplication.controller;

import com.github.pires.obd.commands.SpeedCommand;

import java.io.InputStream;
import java.io.OutputStream;

public class VehicleSpeed extends PeriodicComponent {

    InputStream inputStream;
    OutputStream outputStream;

    public VehicleSpeed() {
        this.setIterationPeriodMs(200);
    }

    @Override
    public Object update() {
        if ((inputStream == null) || (outputStream == null)) {
            return null;
        }

        SpeedCommand speedCommand = new SpeedCommand();
        try {
            speedCommand.run(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return speedCommand.getCalculatedResult();
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

}
