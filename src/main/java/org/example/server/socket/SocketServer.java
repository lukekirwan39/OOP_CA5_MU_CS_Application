package org.example.server.socket;

import com.google.gson.Gson;
import org.example.server.controller.WorkoutController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketServer {
    private final int port;
    private final WorkoutController workoutController;
    private static final String IMAGE_FOLDER = "server_images/";

    public SocketServer(int port) {
        this.port = port;
        File imageDir = new File(IMAGE_FOLDER);
        if (!imageDir.exists()) {
            boolean created = imageDir.mkdirs();
            if (created) {
                System.out.println("Created missing image directory: " + IMAGE_FOLDER);
            }
        }
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

                new Thread(() -> {
                    try (
                            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                            PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)
                    ) {
                        String request = in.readLine();
                        System.out.println("Received: " + request);

                        if (request == null) return;

                        if (request.equals("GET_LIST")) {
                            handleImageList(clientSocket);
                        } else if (request.startsWith("GET_IMAGE:")) {
                            String filename = request.substring("GET_IMAGE:".length()).trim();
                            handleImageTransfer(clientSocket, filename);
                        } else {
                            String responseJson = workoutController.handleRequest(request);
                            out.println(responseJson);
                            System.out.println("Sent: " + responseJson);
                        }
                    } catch (IOException e) {
                        System.out.println("Client error: " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            System.out.println("Error closing socket: " + e.getMessage());
                        }
                    }
                }).start();
            }

        } catch (IOException e) {
            System.out.println("Server Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleImageList(Socket socket) {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            File folder = new File(IMAGE_FOLDER);
            String[] imageFiles = folder.list((dir, name) ->
                    name.toLowerCase().endsWith(".jpg") ||
                            name.toLowerCase().endsWith(".jpeg") ||
                            name.toLowerCase().endsWith(".png"));

            if (imageFiles == null) {
                imageFiles = new String[0];
            }

            String json = new Gson().toJson(imageFiles);
            out.println(json);

            System.out.println("Sent image list: " + json);

        } catch (IOException e) {
            System.out.println("Image list error: " + e.getMessage());
        }
    }

    private void handleImageTransfer(Socket socket, String filename) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            File file = new File(IMAGE_FOLDER + filename);

            if (!file.exists()) {
                System.out.println("Image not found: " + filename);
                out.writeLong(-1);
                out.flush();
                return;
            }

            long fileSize = file.length();
            out.writeLong(fileSize);
            out.flush();

            try (BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                out.flush();
                System.out.println("Image " + filename + " sent successfully (" + fileSize + " bytes).");
            }

        } catch (IOException e) {
            System.out.println("Image transfer error: " + e.getMessage());
        }
    }
}
