package org.example.client.socket;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    private String host;
    private int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String sendMessage(String message) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(message);
            return in.readLine(); // JSON response from server

        } catch (IOException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
}