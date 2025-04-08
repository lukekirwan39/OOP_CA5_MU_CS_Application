package org.example.client.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/workout-view.fxml"));
        VBox root = loader.load();

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Workout Tracker");
        primaryStage.show();
    }

    @FXML
    private void addNewWorkout() {


        Dialog<WorkoutDTO> dialog = new Dialog<>();
        dialog.setTitle("Add New Workout");
        dialog.setHeaderText("Enter Workout Details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField userIdField = new TextField();
        TextField typeField = new TextField();
        TextField durationField = new TextField();
        TextField caloriesField = new TextField();
        DatePicker datePicker = new DatePicker();
        TextArea notesArea = new TextArea();

        durationField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                durationField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        caloriesField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                caloriesField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        grid.add(new Label("User ID:"), 0, 0);
        grid.add(userIdField, 1, 0);
        grid.add(new Label("Workout Type:"), 0, 1);
        grid.add(typeField, 1, 1);
        grid.add(new Label("Duration (minutes):"), 0, 2);
        grid.add(durationField, 1, 2);
        grid.add(new Label("Calories Burned:"), 0, 3);
        grid.add(caloriesField, 1, 3);
        grid.add(new Label("Date:"), 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(new Label("Notes:"), 0, 5);
        grid.add(notesArea, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    if (userIdField.getText().isEmpty() || durationField.getText().isEmpty() ||
                            caloriesField.getText().isEmpty() || datePicker.getValue() == null) {
                        outputArea.setText("Please fill in all required fields");
                        return null;
                    }

                    // Parse and validate numeric values
                    int userId = Integer.parseInt(userIdField.getText());
                    int duration = Integer.parseInt(durationField.getText());
                    int calories = Integer.parseInt(caloriesField.getText());

                    if (userId <= 0 || duration <= 0 || calories < 0) {
                        outputArea.setText("User ID and Duration must be > 0, Calories must be >= 0");
                        return null;
                    }

                    if (typeField.getText().trim().isEmpty()) {
                        outputArea.setText("Workout type cannot be empty");
                        return null;
                    }

                    return new WorkoutDTO(
                            0,
                            userId,
                            typeField.getText().trim(),
                            duration,
                            calories,
                            Date.valueOf(datePicker.getValue()),
                            notesArea.getText()
                    );
                }
                catch (NumberFormatException e) {
                    outputArea.setText("Please enter valid numbers for ID, Duration and Calories");
                    return null;
                }
                catch (Exception e) {
                    outputArea.setText("Error preparing workout: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<WorkoutDTO> result = dialog.showAndWait();
        result.ifPresent(workout -> {
            try {
                // Create request JSON
                JSONObject request = new JSONObject();
                request.put("action", "insertWorkout");
                request.put("userID", workout.getUserID());
                request.put("workoutType", workout.getWorkoutType());
                request.put("duration", workout.getDuration());
                request.put("caloriesBurned", workout.getCaloriesBurned());
                request.put("workoutDate", workout.getWorkoutDate().toString());
                request.put("notes", workout.getNotes());

                // Debug output
                System.out.println("Sending workout data: " + request.toString());

                // Send request and get response
                String response = connection.sendMessage(request.toString());
                System.out.println("Server response: " + response); // Debug response

                JSONObject responseJson = new JSONObject(response);

                if (responseJson.has("success")) {
                    outputArea.setText("Workout added successfully!");
                    listAllWorkouts();
                }
                else {
                    String errorMessage = "Error adding workout: ";

                    if (responseJson.has("error")) {
                        errorMessage += responseJson.getString("error");
                    }
                    else if (responseJson.has("message")) {
                        errorMessage += responseJson.getString("message");
                    }
                    else {
                        errorMessage += "Unknown server error. Response: " + response;

                        if (response.contains("SQL")) {
                            errorMessage += "\nPossible database error detected";
                        }
                        else if (response.contains("null")) {
                            errorMessage += "\nNull value detected in response";
                        }
                    }

                    outputArea.setText(errorMessage);
                }
            }
            catch (Exception e) {
                String errorDetails = "Failed to add workout:\n" +
                        "Error: " + e.getMessage() + "\n" +
                        "Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "none") + "\n";

                StackTraceElement[] stackTrace = e.getStackTrace();
                for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
                    errorDetails += stackTrace[i].toString() + "\n";
                }

                outputArea.setText("Failed to add workout. See console for details.");
                System.err.println(errorDetails);
            }
        });
    }

    @FXML
    private void filterByDuration() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Filter Workouts");
        dialog.setHeaderText("Enter duration (hours) to filter by:");
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
                outputArea.setText("No workouts found with duration of " + duration + " hours.");
            }
            else {
                StringBuilder sb = new StringBuilder("Workouts with duration of " + duration + " hours:\n");
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

    @FXML
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

    @FXML
    private void updateWorkout() {

        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Update Workout");
        idDialog.setHeaderText("Enter Workout ID to update:");
        idDialog.setContentText("Workout ID:");

        Optional<String> idResult = idDialog.showAndWait();
        if(!idResult.isPresent()) {
            return;
        }

        try {
            int workoutId = Integer.parseInt(idResult.get());

            // Debug: Print the workout ID we're trying to update
            System.out.println("Attempting to update workout ID: " + workoutId);

            // Get current workout data
            JSONObject request = new JSONObject();
            request.put("action", "getWorkoutById");
            request.put("id", workoutId);

            String response = connection.sendMessage(request.toString());
            System.out.println("Current workout data response: " + response); // Debug response

            JSONObject currentWorkout = new JSONObject(response);

            if (currentWorkout.has("error")) {
                outputArea.setText("Error: " + currentWorkout.getString("error"));
                return;
            }

            // Create update dialog
            Dialog<WorkoutDTO> updateDialog = new Dialog<>();
            updateDialog.setTitle("Update Workout");
            updateDialog.setHeaderText("Edit Workout Details");

            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            updateDialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Create input fields with current values
            TextField userIdField = new TextField(String.valueOf(currentWorkout.getInt("userID")));
            TextField typeField = new TextField(currentWorkout.getString("workoutType"));
            TextField durationField = new TextField(String.valueOf(currentWorkout.getInt("duration")));
            TextField caloriesField = new TextField(String.valueOf(currentWorkout.getInt("caloriesBurned")));
            DatePicker datePicker = new DatePicker(LocalDate.parse(currentWorkout.getString("workoutDate")));
            TextArea notesArea = new TextArea(currentWorkout.getString("notes"));

            // Add validation listeners
            durationField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    durationField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            caloriesField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    caloriesField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            // Add components to grid
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
                        // Validate inputs
                        if (userIdField.getText().isEmpty() || durationField.getText().isEmpty() ||
                                caloriesField.getText().isEmpty() || datePicker.getValue() == null) {
                            outputArea.setText("Please fill in all required fields");
                            return null;
                        }

                        int userId = Integer.parseInt(userIdField.getText());
                        int duration = Integer.parseInt(durationField.getText());
                        int calories = Integer.parseInt(caloriesField.getText());

                        if (userId <= 0 || duration <= 0 || calories < 0) {
                            outputArea.setText("User ID and Duration must be > 0, Calories must be >= 0");
                            return null;
                        }

                        if (typeField.getText().trim().isEmpty()) {
                            outputArea.setText("Workout type cannot be empty");
                            return null;
                        }

                        return new WorkoutDTO(
                                workoutId,
                                userId,
                                typeField.getText().trim(),
                                duration,
                                calories,
                                Date.valueOf(datePicker.getValue()),
                                notesArea.getText()
                        );
                    }
                    catch (NumberFormatException e) {
                        outputArea.setText("Invalid number format in one of the fields");
                        return null;
                    }
                    catch (Exception e) {
                        outputArea.setText("Error preparing update: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            Optional<WorkoutDTO> updateResult = updateDialog.showAndWait();
            updateResult.ifPresent(updatedWorkout -> {
                try {
                    // Create update request
                    JSONObject updateRequest = new JSONObject();
                    updateRequest.put("action", "updateWorkout");
                    updateRequest.put("workoutID", updatedWorkout.getWorkoutID());
                    updateRequest.put("userID", updatedWorkout.getUserID());
                    updateRequest.put("workoutType", updatedWorkout.getWorkoutType());
                    updateRequest.put("duration", updatedWorkout.getDuration());
                    updateRequest.put("caloriesBurned", updatedWorkout.getCaloriesBurned());
                    updateRequest.put("workoutDate", updatedWorkout.getWorkoutDate().toString());
                    updateRequest.put("notes", updatedWorkout.getNotes());

                    // Debug: Print the update request
                    System.out.println("Sending update request: " + updateRequest.toString());

                    // Send update request
                    String updateResponse = connection.sendMessage(updateRequest.toString());
                    System.out.println("Update response: " + updateResponse); // Debug response

                    JSONObject responseJson = new JSONObject(updateResponse);

                    if (responseJson.has("success")) {
                        // Verify the update by fetching the workout again
                        JSONObject verifyRequest = new JSONObject();
                        verifyRequest.put("action", "getWorkoutById");
                        verifyRequest.put("id", workoutId);

                        String verifyResponse = connection.sendMessage(verifyRequest.toString());
                        JSONObject updatedWorkoutData = new JSONObject(verifyResponse);

                        System.out.println("Verification data: " + updatedWorkoutData.toString());

                        outputArea.setText("Workout updated successfully!\n" +
                                "Updated details:\n" +
                                "Type: " + updatedWorkoutData.getString("workoutType") + "\n" +
                                "Duration: " + updatedWorkoutData.getInt("duration") + " mins\n" +
                                "Calories: " + updatedWorkoutData.getInt("caloriesBurned"));

                        listAllWorkouts(); // Refresh the workout list
                    }
                    else {
                        // Enhanced error handling
                        String errorMsg = "Update failed: ";
                        if (responseJson.has("error")) {
                            errorMsg += responseJson.getString("error");
                        } else if (responseJson.has("message")) {
                            errorMsg += responseJson.getString("message");
                        } else {
                            errorMsg += "Unknown error. Server response: " + updateResponse;

                            // Check for common issues
                            if (updateResponse.contains("SQL")) {
                                errorMsg += "\nPossible database error";
                            } else if (updateResponse.contains("null")) {
                                errorMsg += "\nNull value detected in response";
                            }
                        }
                        outputArea.setText(errorMsg);
                    }
                }
                catch (Exception e) {
                    // Detailed error reporting
                    String errorDetails = "Failed to update workout:\n" +
                            "Error: " + e.getMessage() + "\n" +
                            "Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "none") + "\n";

                    // Add first few lines of stack trace
                    StackTraceElement[] stackTrace = e.getStackTrace();
                    for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
                        errorDetails += stackTrace[i].toString() + "\n";
                    }

                    outputArea.setText("Failed to update workout. See console for details.");
                    System.err.println(errorDetails);
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