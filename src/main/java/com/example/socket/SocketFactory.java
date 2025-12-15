package com.example.socket;

import com.example.core.ChatMessage;
import com.example.core.MessageType;
import com.example.core.NetworkFactory;
import com.example.core.PeerConnection;
import com.example.socket.serialization.MessageSerializer;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SocketFactory implements NetworkFactory {
    private final MessageSerializer serializer;
    private final ExecutorService executor;

    public SocketFactory(MessageSerializer serializer, ExecutorService executor) {
        this.serializer = serializer;
        this.executor = executor;
    }

    @Override
    public PeerConnection createOutgoingConnection(String ip, int port) {
        SocketConnection conn = null;
        try {
            Socket socket = new Socket(ip, port);
            conn = new SocketConnection(socket, serializer);
            SocketConnection finalConn = conn;

            Future<Boolean> handshake = executor.submit(() -> {
                finalConn.sendMessage(new ChatMessage(MessageType.HANDSHAKE, "Hello"));

                ChatMessage response = finalConn.receiveMessage();

                return response != null && response.getType() == MessageType.ACK;
            });

            boolean success = handshake.get(5, TimeUnit.SECONDS);

            if (success) {
                return conn;
            } else {
                System.err.println("Handshake failed: Peer did not ACK.");
            }

        } catch (TimeoutException e) {
            System.err.println("Handshake timed out (Peer unresponsive).");
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
        }

        if (conn != null) conn.close();
        return null;
    }

    public PeerConnection createIncomingConnection(Socket socket) {
        SocketConnection conn = null;
        try {
            conn = new SocketConnection(socket, serializer);
            SocketConnection finalConn = conn;

            Future<Boolean> handshake = executor.submit(() -> {
                ChatMessage msg = finalConn.receiveMessage();

                if (msg != null && msg.getType() == MessageType.HANDSHAKE) {
                    finalConn.sendMessage(new ChatMessage(MessageType.ACK, "Ack"));
                    return true;
                }
                return false;
            });

            boolean success = handshake.get(5, TimeUnit.SECONDS);

            if (success) {
                return conn;
            } else {
                System.err.println("Handshake failed: Expected Handshake.");
            }

        } catch (TimeoutException e) {
            System.err.println("Incoming handshake timed out.");
        } catch (Exception e) {
            System.err.println("Incoming connection setup failed: " + e.getMessage());
        }

        if (conn != null) conn.close();
        return null;
    }
}