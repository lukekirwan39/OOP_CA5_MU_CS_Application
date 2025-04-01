package org.example.client.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.client.socket.SocketClient;
import org.example.shared.DTOs.WorkoutDTO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainGUI extends Application {

    private static SocketClient connection;
    @FXML
    private TextArea outputArea = new TextArea();
    public static void setConnection(SocketClient conn) {
        connection = conn;
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/workout-view.fxml"));
        try {
            VBox root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        primaryStage.setTitle("Workout Tracker");

        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 15");

        Button listAllButton = new Button("List All Workouts");
        Button findByIdButton = new Button("Find Workout by ID");
        Button addWorkoutButton = new Button("Add New Workout"); // (Feature stub)

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

    @FXML
    private void listAllWorkouts() {
        try {
            String requestJson = "{\"action\":\"getAllWorkouts\"}";
            String response = connection.sendMessage(requestJson);

            if (response.startsWith("{") && new JSONObject(response).has("error")) {
                outputArea.setText("Server Error: " + new JSONObject(response).getString("error"));
                return;
            }

            JSONArray workoutsArray = new JSONArray(response);
            if (workoutsArray.length() == 0) {
                outputArea.setText("No workouts found.");
            } else {
                StringBuilder sb = new StringBuilder("All Workouts:\n");
                for (int i = 0; i < workoutsArray.length(); i++) {
                    JSONObject obj = workoutsArray.getJSONObject(i);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate localDate = LocalDate.parse(obj.getString("workoutDate"), formatter);
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

    @FXML
    private void findWorkoutById() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Find Workout");
        dialog.setHeaderText("Enter workout ID:");
        dialog.setContentText("ID:");

        dialog.showAndWait().ifPresent(id -> {
            try {
                int workoutId = Integer.parseInt(id);

                JSONObject request = new JSONObject();
                request.put("action", "getWorkoutById");
                request.put("id", workoutId);

                String response = connection.sendMessage(request.toString());

                JSONObject responseJson = new JSONObject(response);
                if (responseJson.has("error")) {
                    outputArea.setText("Server Error: " + responseJson.getString("error"));
                    return;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(responseJson.getString("workoutDate"), formatter);
                Date sqlDate = Date.valueOf(localDate);

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

            } catch (NumberFormatException e) {
                outputArea.setText("Invalid ID format.");
            } catch (Exception e) {
                outputArea.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        setConnection(new SocketClient("localhost", 12345)); // Init connection before launch
        launch(args);
    }
}