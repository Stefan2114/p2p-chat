# Java P2P Chat Application

A lightweight, multithreaded Peer-to-Peer (P2P) chat application written in Java. It establishes direct TCP connections between clients using a strict handshake protocol and handles concurrency using Virtual Threads.

## 🤝 The Handshake Protocol

To ensure valid communication, every connection follows this strict sequence based on the internal `SocketFactory` logic:

1.  **Initialize (`!hello`)**
    * When a peer connects, they must immediately send a `HANDSHAKE` message.
    * The system waits up to **5 seconds** for a response.

2.  **Acknowledge (`!ack`)**
    * The receiving peer must reply with an `ACK` message.
    * **Success:** The connection is promoted to a full session.
    * **Failure:** If the response is not `ACK` (or the timeout expires), the connection is immediately dropped.

3.  **Terminate (`!bye`)**
    * Sending a disconnect command sends a `BYE` message to gracefully clean up resources on both ends.

---

## 🚀 Getting Started

### Prerequisites
* **Java 21** (Required for Virtual Threads).

### Compile
```bash
javac -d out $(find . -name "*.java")
```

### Run (Simulating two peers)

Open two terminal windows to act as two different people.

Terminal 1 (Alice):
```bash
java -cp out com.example.Main 5000
```
Terminal 2 (Bob):
```bash
java -cp out com.example.Main 5001
```

## 🎮 Commands
| Command | Usage | Description |
| :--- | :--- | :--- |
| **Connect** | `connect <ip> <port>` | Connect to a peer (starts handshake). |
| **Send** | `send <ip:port> <msg>` | Send a message to a connected peer. |
| **Disconnect** | `disconnect <ip:port>` | Send `!bye` and close connection. |
| **Quit** | `quit` | Disconnect all peers and close app. |
    