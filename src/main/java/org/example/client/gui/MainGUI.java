package org.example.client.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Button updateWorkoutButton = new Button("Update Workout");
        Button deleteWorkoutButton = new Button("Delete Workout");
        Button filterByDurationButton = new Button("Filter by Duration");

        listAllButton.setOnAction(e -> listAllWorkouts());
        findByIdButton.setOnAction(e -> findWorkoutById());
        updateWorkoutButton.setOnAction(e -> updateWorkout());
        deleteWorkoutButton.setOnAction(e -> deleteWorkout());
        filterByDurationButton.setOnAction(e -> filterByDuration());
        
        root.getChildren().addAll(
                listAllButton,
                findByIdButton,
                addWorkoutButton,
                updateWorkoutButton,
                deleteWorkoutButton,
                filterByDurationButton,
                outputArea
        );

        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void filterByDuration() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Filter Workouts");
        dialog.setHeaderText("Enter duration (minutes) to filter by:");
        dialog.setContentText("Duration:");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return;
        }
        try {
            int duration = Integer.parseInt(result.get());

            if (duration <= 0) {
                outputArea.setText("Duration must be greater than 0");
                return;
            }


            String requestJson = "{\"action\":\"getAllWorkouts\"}";
            String response = connection.sendMessage(requestJson);

            if (response.startsWith("{") && new JSONObject(response).has("error")) {
                outputArea.setText("Server Error: " + new JSONObject(response).getString("error"));
                return;
            }

            JSONArray workoutsArray = new JSONArray(response);
            if (workoutsArray.length() == 0) {
                outputArea.setText("No workouts found.");
                return;
            }


            List<WorkoutDTO> allWorkouts = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (int i = 0; i < workoutsArray.length(); i++) {
                JSONObject obj = workoutsArray.getJSONObject(i);
                LocalDate localDate = LocalDate.parse(obj.getString("workoutDate"), formatter);

                allWorkouts.add(new WorkoutDTO(
                        obj.getInt("workoutID"),
                        obj.getInt("userID"),
                        obj.getString("workoutType"),
                        obj.getInt("duration"),
                        obj.getInt("caloriesBurned"),
                        Date.valueOf(localDate),
                        obj.getString("notes")
                ));
            }


            List<WorkoutDTO> filteredWorkouts = filterWorkoutsByDuration(allWorkouts, duration);


            if (filteredWorkouts.isEmpty()) {
                outputArea.setText("No workouts found with duration of " + duration + " minutes.");
            }
            else {
                StringBuilder sb = new StringBuilder("Workouts with duration of " + duration + " minutes:\n");
                for (WorkoutDTO workout : filteredWorkouts) {
                    sb.append(workout).append("\n");
                }
                outputArea.setText(sb.toString());
            }

        }
        catch (NumberFormatException e) {
            outputArea.setText("Please enter a valid duration number");
        }
        catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<WorkoutDTO> filterWorkoutsByDuration(List<WorkoutDTO> allWorkouts, int duration) {
        List<WorkoutDTO> filteredWorkouts = new ArrayList<>();

        for (WorkoutDTO workout : allWorkouts) {
            if (workout.getDuration() == duration) {
                filteredWorkouts.add(workout);
            }
        }

        return filteredWorkouts;

    }

    private void deleteWorkout() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Workout");
        dialog.setHeaderText("Enter Workout ID to delete:");
        dialog.setContentText("Workout ID:");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return;
        }
        try {
            int workoutId = Integer.parseInt(result.get());

            if (workoutId <= 0) {
                outputArea.setText("Workout ID must be greater than 0");
                return;
            }


            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Deletion");
            confirmation.setHeaderText("Are you sure you want to delete workout #" + workoutId + "?");
            confirmation.setContentText("This action cannot be undone.");

            Optional<ButtonType> confirmationResult = confirmation.showAndWait();
            if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {

                JSONObject request = new JSONObject();
                request.put("action", "deleteWorkout");
                request.put("workoutID", workoutId);

                String response = connection.sendMessage(request.toString());
                JSONObject responseJson = new JSONObject(response);

                if (responseJson.has("success")) {
                    outputArea.setText("Workout #" + workoutId + " deleted successfully!");
                }
                else {
                    outputArea.setText("Delete failed: " +
                            (responseJson.has("error") ? responseJson.getString("error") : "Unknown error"));
                }
            }
            else {
                outputArea.setText("Deletion cancelled.");
            }
        }
        catch (NumberFormatException e) {
            outputArea.setText("Please enter a valid workout ID number");
        }
        catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateWorkout() {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Update Workout");
        idDialog.setHeaderText("Enter Workout ID to update:");
        idDialog.setContentText("Workout ID:");

        Optional<String> idResult = idDialog.showAndWait();
        if(!idResult.isPresent()) {
            return;
        }
        try{
            int workoutId = Integer.parseInt(idResult.get());
            JSONObject request = new JSONObject();
            request.put("action", "getWorkoutById");
            request.put("id", workoutId);

            String response = connection.sendMessage(request.toString());
            JSONObject currentWorkout = new JSONObject(response);

            if (currentWorkout.has("error")) {
                outputArea.setText("Error: " + currentWorkout.getString("error"));
                return;
            }

            Dialog<WorkoutDTO> updateDialog = new Dialog<>();
            updateDialog.setTitle("Update Workout");
            updateDialog.setHeaderText("Edit Workout Details");

            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            updateDialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField userIdField = new TextField(String.valueOf(currentWorkout.getInt("userID")));
            TextField typeField = new TextField(currentWorkout.getString("workoutType"));
            TextField durationField = new TextField(String.valueOf(currentWorkout.getInt("duration")));
            TextField caloriesField = new TextField(String.valueOf(currentWorkout.getInt("caloriesBurned")));
            DatePicker datePicker = new DatePicker(LocalDate.parse(currentWorkout.getString("workoutDate")));
            TextArea notesArea = new TextArea(currentWorkout.getString("notes"));

            grid.add(new Label("User ID:"), 0, 0);
            grid.add(userIdField, 1, 0);
            grid.add(new Label("Workout Type:"), 0, 1);
            grid.add(typeField, 1, 1);
            grid.add(new Label("Duration (mins):"), 0, 2);
            grid.add(durationField, 1, 2);
            grid.add(new Label("Calories Burned:"), 0, 3);
            grid.add(caloriesField, 1, 3);
            grid.add(new Label("Date:"), 0, 4);
            grid.add(datePicker, 1, 4);
            grid.add(new Label("Notes:"), 0, 5);
            grid.add(notesArea, 1, 5);

            updateDialog.getDialogPane().setContent(grid);

            // Convert the result to a WorkoutDTO when the update button is clicked
            updateDialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    try {
                        if (datePicker.getValue() == null) {
                            outputArea.setText("Please select a date");
                            return null;
                        }
                        return new WorkoutDTO(
                                workoutId,
                                Integer.parseInt(userIdField.getText()),
                                typeField.getText(),
                                Integer.parseInt(durationField.getText()),
                                Integer.parseInt(caloriesField.getText()),
                                Date.valueOf(datePicker.getValue()),
                                notesArea.getText()
                        );
                    }
                    catch (NumberFormatException e) {
                        outputArea.setText("Invalid number format in one of the fields");
                        return null;
                    }
                    catch (Exception e) {
                        outputArea.setText("Error: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            Optional<WorkoutDTO> updateResult = updateDialog.showAndWait();
            updateResult.ifPresent(updatedWorkout -> {
                try {
                    JSONObject updateRequest = new JSONObject();
                    updateRequest.put("action", "updateWorkout");
                    updateRequest.put("workoutID", updatedWorkout.getWorkoutID());
                    updateRequest.put("userID", updatedWorkout.getUserID());
                    updateRequest.put("workoutType", updatedWorkout.getWorkoutType());
                    updateRequest.put("duration", updatedWorkout.getDuration());
                    updateRequest.put("caloriesBurned", updatedWorkout.getCaloriesBurned());
                    updateRequest.put("workoutDate", updatedWorkout.getWorkoutDate().toString());
                    updateRequest.put("notes", updatedWorkout.getNotes());

                    String updateResponse = connection.sendMessage(updateRequest.toString());
                    JSONObject responseJson = new JSONObject(updateResponse);

                    if (responseJson.has("success")) {
                        outputArea.setText("Workout updated successfully!");
                    }
                    else {
                        outputArea.setText("Update failed: " +
                                (responseJson.has("error") ? responseJson.getString("error") : "Unknown error"));
                    }
                }
                catch (Exception e) {
                    outputArea.setText("Error updating workout: " + e.getMessage());
                    e.printStackTrace();
                }
            });

        }
        catch (NumberFormatException e) {
            outputArea.setText("Please enter a valid workout ID number");
        }
        catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
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