package com.example.headunitapplication.controller;

import com.github.pires.obd.commands.engine.RPMCommand;

import java.io.InputStream;
import java.io.OutputStream;

public class EngineEffortRpm extends PeriodicComponent {

    InputStream inputStream;
    OutputStream outputStream;

    public EngineEffortRpm() {
        this.setIterationPeriodMs(200);
    }

    @Override
    public Object update() {
        if ((inputStream == null) || (outputStream == null)) {
            return null;
        }

        RPMCommand rpmCommand = new RPMCommand();
        try {
            rpmCommand.run(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return rpmCommand.getCalculatedResult();
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