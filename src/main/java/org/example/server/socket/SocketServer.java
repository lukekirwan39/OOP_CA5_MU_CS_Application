package org.example.server.socket;

import com.google.gson.Gson;
import org.example.server.controller.WorkoutController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    private int port;
    private WorkoutController workoutController;
    private static final String IMAGE_FOLDER = "server_images/";

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

                // Peek into first line to determine request type
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String request = in.readLine();
                System.out.println("Received: " + request);

                if (request == null) {
                    clientSocket.close();
                    continue;
                }

                // If image-related, handle differently
                if (request.equals("GET_LIST")) {
                    handleImageList(clientSocket);
                } else if (request.startsWith("GET_IMAGE:")) {
                    handleImageTransfer(clientSocket, request.substring("GET_IMAGE:".length()).trim());
                } else {
                    // Default: treat as JSON request
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    String responseJson = workoutController.handleRequest(request);
                    out.println(responseJson);
                    System.out.println("Sent: " + responseJson);
                }

                clientSocket.close();
            }

        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleImageList(Socket socket) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            File folder = new File(IMAGE_FOLDER);
            String[] imageFiles = folder.list((dir, name) ->
                    name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg"));

            if (imageFiles == null) {
                imageFiles = new String[0];
            }

            String json = new Gson().toJson(imageFiles);
            out.writeBytes(json + "\n");

        } catch (IOException e) {
            System.out.println("Image list error: " + e.getMessage());
        }
    }

    private void handleImageTransfer(Socket socket, String filename) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            File file = new File(IMAGE_FOLDER + filename);

            if (!file.exists()) {
                out.writeInt(-1); // Signal file not found
                return;
            }

            try (BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(file))) {
                out.writeInt((int) file.length());

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

        } catch (IOException e) {
            System.out.println("Image transfer error: " + e.getMessage());
        }
    }
}
