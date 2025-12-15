package com.example.socket;

import com.example.core.ChatMessage;
import com.example.core.PeerConnection;
import com.example.socket.serialization.MessageSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketConnection implements PeerConnection {
    private final Socket socket;
    private final MessageSerializer serializer;
    private final BufferedReader reader;


    SocketConnection(Socket socket, MessageSerializer serializer) throws IOException{
        this.socket = socket;
        this.serializer = serializer;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));    }

    @Override
    public void sendMessage(ChatMessage msg) throws IOException {
        byte[] data = serializer.serialize(msg);

        synchronized (socket.getOutputStream()) {
            socket.getOutputStream().write(data);
            socket.getOutputStream().write('\n');
            socket.getOutputStream().flush();
        }
    }

    @Override
    public ChatMessage receiveMessage() throws IOException {
        String line = reader.readLine();
        if (line == null) return null;
        return serializer.deserialize(line.getBytes());
    }

    @Override
    public String getId() {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}