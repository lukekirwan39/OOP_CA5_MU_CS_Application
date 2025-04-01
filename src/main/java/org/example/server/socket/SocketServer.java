package org.example.server.socket;

import org.example.server.controller.WorkoutController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    private int port;
    private WorkoutController workoutController;

    public SocketServer(int port) {
        this.port = port;
        try {
            this.workoutController = new WorkoutController();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Workout Server is running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress());

                // Read incoming JSON
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String requestJson = in.readLine(); // receives JSON from client
                System.out.println("Received: " + requestJson);

                // Handle the request using the controller
                String responseJson = workoutController.handleRequest(requestJson);

                // Send back response
                out.println(responseJson);
                System.out.println("Sent: " + responseJson);

                clientSocket.close();
            }

        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}