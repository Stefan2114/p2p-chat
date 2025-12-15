package com.example;


import com.example.core.NetworkServer;
import com.example.socket.SocketFactory;
import com.example.socket.SocketServer;
import com.example.socket.serialization.JsonSerializer;
import com.example.socket.serialization.MessageSerializer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java Main <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        ExecutorService ioPool = Executors.newCachedThreadPool();


        // Socket
        MessageSerializer serializer = new JsonSerializer();
        // MessageSerializer serializer = new RawStringSerializer();
        SocketFactory factory = new SocketFactory(serializer, ioPool);
        PeerManager manager = new PeerManager(factory, ioPool);
        NetworkServer server = new SocketServer(port, factory, manager, ioPool);


        server.start();
        new ConsoleHandler(manager).run();
        server.stop();
        ioPool.shutdownNow();
    }
}