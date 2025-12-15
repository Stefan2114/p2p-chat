package com.example.core;

import java.io.IOException;

public interface PeerConnection extends AutoCloseable {
    String getId();
    void sendMessage(ChatMessage msg) throws IOException;
    ChatMessage receiveMessage() throws IOException;
    boolean isClosed();
    void close();
}