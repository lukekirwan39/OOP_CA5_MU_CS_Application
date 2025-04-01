package org.example.server.socket;

import java.io.*;
import java.net.*;
import org.json.JSONObject;
import org.json.JSONArray;

public class SocketServer {
    public static void main(String[] args) {
        int port = 12345;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");

                handleClient(clientSocket);

                clientSocket.close();
                System.out.println("Client disconnected.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String request = in.readLine();
            if (request != null) {
                System.out.println("Received request: " + request);
                JSONObject jsonRequest = new JSONObject(request);

                String action = jsonRequest.getString("action");
                JSONObject jsonResponse = new JSONObject();

                switch (action) {
                    case "getWorkouts":
                        // Simulated DAO call
                        JSONArray workouts = new JSONArray();

                        JSONObject workout1 = new JSONObject();
                        workout1.put("id", 1);
                        workout1.put("name", "Push Day");
                        workout1.put("duration", 45);

                        JSONObject workout2 = new JSONObject();
                        workout2.put("id", 2);
                        workout2.put("name", "Cardio Blast");
                        workout2.put("duration", 30);

                        workouts.put(workout1);
                        workouts.put(workout2);

                        out.println(workouts.toString());
                        break;

                    default:
                        jsonResponse.put("error", "Unknown action");
                        out.println(jsonResponse.toString());
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
