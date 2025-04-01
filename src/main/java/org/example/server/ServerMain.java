package org.example.server;

import org.example.server.socket.SocketServer;

public class ServerMain {
    public static void main(String[] args) {
        int port = 12345;
        SocketServer server = new SocketServer(port);
        server.start();
    }
}
