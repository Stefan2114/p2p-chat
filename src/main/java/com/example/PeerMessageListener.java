package com.example;

import com.example.core.ChatMessage;
import com.example.core.MessageType;
import com.example.core.PeerConnection;

import java.io.IOException;

public class PeerMessageListener implements Runnable {
    private final PeerConnection connection;
    private final PeerManager manager;

    public PeerMessageListener(PeerConnection connection, PeerManager manager) {
        this.connection = connection;
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            while (!connection.isClosed()) {
                ChatMessage msg = connection.receiveMessage();
                if (msg == null) break;

                if (msg.getType() == MessageType.BYE) {
                    System.out.println("Peer requested disconnect: " + connection.getId());
                    break;
                } else if (msg.getType() == MessageType.MESSAGE) {
                    System.out.printf("[%s]: %s%n", connection.getId(), msg.getContent());
                }
            }
        } catch (IOException ignored) {
        } finally {
            manager.removeConnection(connection.getId());
        }
    }
}