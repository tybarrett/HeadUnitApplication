package com.example.headunitapplication;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastReceiver {
    private static final String MULTICAST_ADDRESS = "224.1.1.1";

    private MulticastSocket receiveSocket = null;
    private ReceiveDataThread receiveDataThread;
    CallbackObject<String> receivedDataCallback;
    WifiManager.MulticastLock multicastLock;

    // UDP Unicast attributes (trying new version)
    DatagramSocket dsocket;

    public MulticastReceiver(CallbackObject<String> receivedDataCallback, WifiManager wifiManager) {
        boolean successfulConnect = false;
        try {
//            if (wifiManager != null) {
//                multicastLock = wifiManager.createMulticastLock("multicastLock");
//                multicastLock.acquire();
//
//                receiveSocket = new MulticastSocket(8686);
//                InetAddress multicastGroup = InetAddress.getByName(MULTICAST_ADDRESS);
//                receiveSocket.joinGroup(multicastGroup);
//            }

            dsocket = new DatagramSocket(8686);
            successfulConnect = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.receivedDataCallback = receivedDataCallback;

        if (successfulConnect) {
            receiveDataThread = new ReceiveDataThread();
            receiveDataThread.start();
        }
    }

    private class ReceiveDataThread extends Thread {
        private boolean shouldRun = true;
        private byte[] inputBuffer = new byte[256];

        public void run() {

            while (shouldRun) {
                DatagramPacket pkt = new DatagramPacket(inputBuffer, inputBuffer.length);
                try {
                    dsocket.receive(pkt);
                    String lText = new String(inputBuffer, 0, pkt.getLength());
                    receivedDataCallback.update(lText);
                    pkt.setLength(inputBuffer.length);

//                    receiveSocket.receive(pkt);
//                    String dataReceived = new String(pkt.getData(), 0, pkt.getLength());
//                    receivedDataCallback.update(dataReceived);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                InetAddress multicastGroup = InetAddress.getByName(MULTICAST_ADDRESS);
                receiveSocket.leaveGroup(multicastGroup);
                receiveSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        receiveDataThread.shouldRun = false;
    }
}
