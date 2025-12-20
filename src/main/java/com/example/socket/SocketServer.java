package com.example.socket;

import com.example.ExecutionService;
import com.example.PeerManager;
import com.example.core.NetworkServer;
import com.example.core.PeerConnection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer implements NetworkServer, Runnable {
    private final int port;
    private final SocketFactory factory;
    private final PeerManager manager;
    private final ExecutionService executionService;
    private volatile boolean running = false;

    public SocketServer(int port, SocketFactory factory, PeerManager manager, ExecutionService executionService) {
        this.port = port;
        this.factory = factory;
        this.manager = manager;
        this.executionService = executionService;
    }

    @Override
    public void start() {
        running = true;
        executionService.execute(this);
    }

    @Override
    public void run() {
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println(">> TCP Server started on port " + port);
            while (running) {
                Socket clientSocket = ss.accept();

                executionService.execute(() -> {
                    PeerConnection conn = factory.createIncomingConnection(clientSocket);
                    if (conn != null) {
                        manager.registerConnection(conn);
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
    }
}