package com.example.core;

import java.io.IOException;

public interface NetworkServer {
    void start() throws IOException;
    void stop();
}