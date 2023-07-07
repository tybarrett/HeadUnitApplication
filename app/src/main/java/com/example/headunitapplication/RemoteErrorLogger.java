package com.example.headunitapplication;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RemoteErrorLogger {

    private static final String REMOTE_IP = "157.245.246.125:5000";
    private URL url = null;

    public RemoteErrorLogger() {

        try {
            url = new URL("https://" + REMOTE_IP + "/write_to_log");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void write_log(String msg) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        conn.setDoInput(true);
        conn.setDoOutput(true);

        byte[] outputBytes = msg.getBytes(StandardCharsets.UTF_8);
        OutputStream os = null;
        try {
//            os = conn.getOutputStream();
            os = new BufferedOutputStream(conn.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (os != null) {
            try {
                os.write(outputBytes);
                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            int resp = conn.getResponseCode();
            System.out.println(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        conn.disconnect();
    }

    public static void main(String[] args) {
        RemoteErrorLogger rel = new RemoteErrorLogger();
        rel.write_log("Hello!");
    }

}
