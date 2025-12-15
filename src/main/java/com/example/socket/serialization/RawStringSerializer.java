package com.example.socket.serialization;

import com.example.core.ChatMessage;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RawStringSerializer implements MessageSerializer {

    @Override
    public byte[] serialize(ChatMessage msg) {
        String content = msg.getContent();
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encode(contentBytes);
    }

    @Override
    public ChatMessage deserialize(byte[] data) {
        byte[] decodedBytes = Base64.getDecoder().decode(data);
        String content = new String(decodedBytes, StandardCharsets.UTF_8);
        return new ChatMessage(content);
    }
}