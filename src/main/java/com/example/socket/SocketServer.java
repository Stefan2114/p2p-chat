package com.example.socket;

import com.example.PeerManager;
import com.example.core.NetworkServer;
import com.example.core.PeerConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class SocketServer implements NetworkServer, Runnable {
    private final int port;
    private final SocketFactory factory;
    private final PeerManager manager;
    private final ExecutorService executor;
    private volatile boolean running = false;
    private ServerSocket serverSocket;

    public SocketServer(int port, SocketFactory factory, PeerManager manager, ExecutorService executor) {
        this.port = port;
        this.factory = factory;
        this.manager = manager;
        this.executor = executor;
    }

    @Override
    public void start() {
        running = true;
        executor.execute(this);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(">> TCP Server started on port " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();

                executor.execute(() -> {
                    PeerConnection conn = factory.createIncomingConnection(clientSocket);
                    if (conn != null) {
                        manager.joinNetwork(conn);
                    }
                });
            }
        } catch (IOException e) {
            if (running) e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        running = false;
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
    }
}