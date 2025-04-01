package org.example.client.socket;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public SocketClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public String sendMessage(String message) throws IOException {
        out.println(message);
        return in.readLine();  // Adjust if multiline or JSON
    }

    public void close() throws IOException {
        socket.close();
    }
}
