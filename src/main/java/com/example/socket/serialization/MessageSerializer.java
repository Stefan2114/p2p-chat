package com.example.socket.serialization;

import com.example.core.ChatMessage;

import java.io.IOException;

public interface MessageSerializer {
    byte[] serialize(ChatMessage msg) throws IOException;
    ChatMessage deserialize(byte[] data) throws IOException;
}
