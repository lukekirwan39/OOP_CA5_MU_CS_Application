package org.example.client.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.server.DAOs.WorkoutDAO;
import org.example.server.DAOs.Interfaces.WorkoutDAOInterface;
import org.example.client.DTOs.WorkoutDTO;

import java.sql.SQLException;
import java.util.List;

public class MainGUI extends Application {

    private WorkoutDAOInterface workoutDAO = new WorkoutDAO();
    private TextArea outputArea = new TextArea();

    public MainGUI() throws SQLException {
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
            List<WorkoutDTO> workouts = workoutDAO.getAllWorkouts();
            if (workouts.isEmpty()) {
                outputArea.setText("No workouts found.");
            } else {
                StringBuilder sb = new StringBuilder("All Workouts:\n");
                for (WorkoutDTO w : workouts) {
                    sb.append(w).append("\n");
                }
                outputArea.setText(sb.toString());
            }
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
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
                WorkoutDTO workout = workoutDAO.getWorkoutById(workoutId);
                outputArea.setText(workout != null ? workout.toString() : "Workout not found.");
            } catch (NumberFormatException | SQLException e) {
                outputArea.setText("Invalid ID format.");
            }
        });
    }

    public static void main(String[] args) {
        launch(args); // launches the JavaFX app
    }
}