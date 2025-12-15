package com.example.socket;

import com.example.core.ChatMessage;
import com.example.core.MessageType;
import com.example.core.NetworkFactory;
import com.example.core.PeerConnection;
import com.example.socket.serialization.MessageSerializer;

import java.io.IOException;
import java.net.Socket;

public class SocketFactory implements NetworkFactory {
    private final MessageSerializer serializer;
    private static final int HANDSHAKE_TIMEOUT_MS = 5000;

    public SocketFactory(MessageSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public PeerConnection createOutgoingConnection(String ip, int port) {
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
            socket.setSoTimeout(HANDSHAKE_TIMEOUT_MS);

            SocketConnection conn = new SocketConnection(socket, serializer);

            conn.sendMessage(new ChatMessage(MessageType.HANDSHAKE, "Hello"));
            ChatMessage response = conn.receiveMessage();

            if (response != null && response.getType() == MessageType.ACK) {
                socket.setSoTimeout(0);
                return conn;
            } else {
                System.err.println("Handshake failed: Invalid response.");
            }
            conn.close();
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }

        return null;
    }

    public PeerConnection createIncomingConnection(Socket socket) {
        try {
            socket.setSoTimeout(HANDSHAKE_TIMEOUT_MS);
            SocketConnection conn = new SocketConnection(socket, serializer);

            ChatMessage msg = conn.receiveMessage();
            if (msg != null && msg.getType() == MessageType.HANDSHAKE) {
                conn.sendMessage(new ChatMessage(MessageType.ACK, "Ack"));
                socket.setSoTimeout(0);
                return conn;
            }
            conn.close();
        } catch (IOException e) {
            System.err.println("Incoming handshake failed: " + e.getMessage());
        }

        return null;
    }
}