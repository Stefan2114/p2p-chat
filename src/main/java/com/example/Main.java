package com.example;


import com.example.core.NetworkServer;
import com.example.socket.SocketFactory;
import com.example.socket.SocketServer;
import com.example.socket.serialization.JsonSerializer;
import com.example.socket.serialization.MessageSerializer;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) return;
        int port = Integer.parseInt(args[0]);

        try (ExecutionService executionService = new ExecutionService()) {
            MessageSerializer serializer = new JsonSerializer();
            // MessageSerializer serializer = new MessageSerializer();

            SocketFactory factory = new SocketFactory(serializer);
            PeerManager manager = new PeerManager(factory, executionService);
            NetworkServer server = new SocketServer(port, factory, manager, executionService);
            server.start();
            new ConsoleHandler(manager).run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}