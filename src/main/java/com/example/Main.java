package com.example;

import com.example.core.NetworkServer;
import com.example.socket.SocketFactory;
import com.example.socket.SocketServer;
import com.example.socket.serialization.JsonSerializer;
import com.example.socket.serialization.MessageSerializer;
import com.example.socket.serialization.RawStringSerializer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static void main(String[] args) throws IOException {
        if (args.length == 0) return;
        int port = Integer.parseInt(args[0]);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            // MessageSerializer serializer = new JsonSerializer();
            MessageSerializer serializer = new RawStringSerializer();
            SocketFactory factory = new SocketFactory(serializer);

            PeerManager manager = new PeerManager(factory, executor);
            NetworkServer server = new SocketServer(port, factory, manager, executor);

            server.start();
            new ConsoleHandler(manager).run();

            server.stop();
            manager.quitConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}