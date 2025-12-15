package com.example;

import java.util.Scanner;

public class ConsoleHandler {

    private final PeerManager manager;

    public ConsoleHandler(PeerManager manager) {
        this.manager = manager;
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        printMenu();

        boolean running = true;

        while (running) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String[] parts = input.split(" ", 3);
            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "connect":
                        handleConnect(parts);
                        break;

                    case "send":
                        this.handleSend(parts);
                        break;

                    case "disconnect":
                        handleDisconnect(parts);
                        break;

                    case "help":
                        printMenu();
                        break;

                    case "quit":
                        manager.quitConnection();
                        running = false;
                        break;

                    default:
                        System.out.println("Unknown command. Type 'help' for menu.");
                }
            } catch (Exception e) {
                System.out.println("Error processing command: " + e.getMessage());
            }
        }
    }

    private void handleConnect(String[] parts) {
        if (parts.length < 3) System.out.println("Usage: connect <ip> <port>");
        else manager.createNewConnection(parts[1], Integer.parseInt(parts[2]));
    }

        private void handleSend(String[] parts){
        if (parts.length < 3) System.out.println("Usage: send <addr> <msg>");
        else manager.sendMessage(parts[1], parts[2]);
    }

    private void handleDisconnect(String[] parts) {
        if (parts.length < 2) System.out.println("Usage: disconnect <ip>");
        else manager.removeConnection(parts[1]);
    }

    private void printMenu() {
        System.out.println("\nCommand Menu:");
        System.out.println("  connect <ip> <port>  -> Connect to a peer");
        System.out.println("  send <ip:port> <msg>      -> Send message to a peer");
        System.out.println("  disconnect <ip:port>      -> Disconnect from a peer");
        System.out.println("  help                 -> Print commands");
        System.out.println("  quit                 -> Disconnect from all peers and exit");
    }
}
