package com.example.headunitapplication.controller;

import com.github.pires.obd.commands.engine.ThrottlePositionCommand;

import java.io.InputStream;
import java.io.OutputStream;

public class ThrottlePosition extends PeriodicComponent {

    InputStream inputStream;
    OutputStream outputStream;

    public ThrottlePosition() {
        this.setIterationPeriodMs(200);
    }

    @Override
    public Object update() {
        if ((inputStream == null) || (outputStream == null)) {
            return null;
        }

        ThrottlePositionCommand throttlePositionCommand = new ThrottlePositionCommand();
        try {
            throttlePositionCommand.run(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return throttlePositionCommand.getCalculatedResult();
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