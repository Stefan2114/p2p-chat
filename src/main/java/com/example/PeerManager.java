package com.example;

import com.example.core.ChatMessage;
import com.example.core.MessageType;
import com.example.core.NetworkFactory;
import com.example.core.PeerConnection;
import java.util.concurrent.ConcurrentHashMap;

public class PeerManager {
    private final ConcurrentHashMap<String, PeerConnection> peers = new ConcurrentHashMap<>();
    private final NetworkFactory factory;
    private final ExecutionService executionService;

    public PeerManager(NetworkFactory factory, ExecutionService executionService) {
        this.factory = factory;
        this.executionService = executionService;
    }

    public void createNewConnection(String ip, int port) {
        executionService.execute(() -> {
            String tempId = ip + ":" + port;
            if (peers.containsKey(tempId)) {
                System.out.println("Connection already exists to " + tempId);
                return;
            }

            PeerConnection conn = factory.createOutgoingConnection(ip, port);
            if (conn != null) {
                registerConnection(conn);
            }
        });
    }

    public void registerConnection(PeerConnection conn) {
        String peerId = conn.getId();
        if (peers.putIfAbsent(peerId, conn) != null) {
            System.out.println("Duplicate connection detected for " + peerId);
            conn.close();
        } else {
            System.out.println(">> Connected to: " + peerId);
            executionService.execute(new PeerMessageListener(conn, this));
        }
    }

    public void sendMessage(String peerId, String content) {
        PeerConnection conn = peers.get(peerId);
        if (conn == null) {
            System.out.println("Peer not found: " + peerId);
            return;
        }

        executionService.execute(() -> {
            try {
                conn.sendMessage(new ChatMessage(content));
                System.out.println("Sent to " + peerId);
            } catch (Exception e) {
                System.out.println("Failed to send: " + e.getMessage());
                removeConnection(peerId);
            }
        });
    }

    public void removeConnection(String peerId) {
        PeerConnection conn = peers.remove(peerId);

        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.sendMessage(new ChatMessage(MessageType.BYE, "Closing"));
                }
            } catch (Exception ignored) {}
            conn.close();
            System.out.println(">> Connection removed: " + peerId);
        }
    }

    public void quitConnection() {
        peers.keySet().forEach(this::removeConnection);
    }
}