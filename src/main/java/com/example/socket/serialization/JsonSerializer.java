package com.example.socket.serialization;

import com.example.core.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Base64;

public class JsonSerializer implements MessageSerializer {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(ChatMessage msg) throws IOException {
        byte[] jsonBytes = mapper.writeValueAsBytes(msg);
        return Base64.getEncoder().encode(jsonBytes);
    }

    @Override
    public ChatMessage deserialize(byte[] data) throws IOException {
        byte[] jsonBytes = Base64.getDecoder().decode(data);
        return mapper.readValue(jsonBytes, ChatMessage.class);
    }
}