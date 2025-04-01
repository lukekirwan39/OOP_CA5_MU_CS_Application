package org.example.client.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.client.socket.SocketClient;
import org.example.client.DTOs.WorkoutDTO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainGUI extends Application {

    private SocketClient connection;
    private TextArea outputArea = new TextArea();

    public MainGUI() throws SQLException {
        try {
            connection = new SocketClient("localhost", 12345);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Workout Tracker");

        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 15");

        Button listAllButton = new Button("List All Workouts");
        Button findByIdButton = new Button("Find Workout by ID");
        Button addWorkoutButton = new Button("Add New Workout");

        listAllButton.setOnAction(e -> listAllWorkouts());
        findByIdButton.setOnAction(e -> findWorkoutById());

        root.getChildren().addAll(
                listAllButton,
                findByIdButton,
                addWorkoutButton,
                outputArea
        );

        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void listAllWorkouts() {
        try {
            String requestJson = "{\"action\":\"getAllWorkouts\"}";
            String response = connection.sendMessage(requestJson);

            JSONArray workoutsArray = new JSONArray(response);
            if (workoutsArray.length() == 0) {
                outputArea.setText("No workouts found.");
            } else {
                StringBuilder sb = new StringBuilder("All Workouts:\n");
                for (int i = 0; i < workoutsArray.length(); i++) {
                    JSONObject obj = workoutsArray.getJSONObject(i);

                    // Parse the workoutDate from String to java.sql.Date
                    String dateStr = obj.getString("workoutDate");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate localDate = LocalDate.parse(dateStr, formatter);
                    Date sqlDate = Date.valueOf(localDate);

                    WorkoutDTO workout = new WorkoutDTO(
                            obj.getInt("workoutID"),
                            obj.getInt("userID"),
                            obj.getString("workoutType"),
                            obj.getInt("duration"),
                            obj.getInt("caloriesBurned"),
                            sqlDate,
                            obj.getString("notes")
                    );
                    sb.append(workout).append("\n");
                }
                outputArea.setText(sb.toString());
            }
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void findWorkoutById() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Find Workout");
        dialog.setHeaderText("Enter workout ID:");
        dialog.setContentText("ID:");

        dialog.showAndWait().ifPresent(id -> {
            try {
                int workoutId = Integer.parseInt(id);

                // Build JSON request
                JSONObject request = new JSONObject();
                request.put("action", "getWorkoutById");
                request.put("id", workoutId);

                // Send request
                String response = connection.sendMessage(request.toString());

                // Parse response
                JSONObject responseJson = new JSONObject(response);

                String dateStr = responseJson.getString("workoutDate");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(dateStr, formatter);
                Date sqlDate = Date.valueOf(localDate);

                if (responseJson.has("error")) {
                    outputArea.setText("Server Error: " + responseJson.getString("error"));
                } else {
                    WorkoutDTO workout = new WorkoutDTO(
                            responseJson.getInt("workoutID"),
                            responseJson.getInt("userID"),
                            responseJson.getString("workoutType"),
                            responseJson.getInt("duration"),
                            responseJson.getInt("caloriesBurned"),
                            sqlDate,
                            responseJson.getString("notes")
                    );
                    outputArea.setText(workout.toString());
                }

            } catch (NumberFormatException e) {
                outputArea.setText("Invalid ID format.");
            } catch (IOException e) {
                outputArea.setText("Connection error: " + e.getMessage());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}