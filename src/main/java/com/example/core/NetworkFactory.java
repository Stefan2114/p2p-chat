package com.example.core;


public interface NetworkFactory {
    PeerConnection createOutgoingConnection(String ip, int port);
}