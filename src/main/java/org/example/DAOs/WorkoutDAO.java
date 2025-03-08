package org.example.DAOs;

import org.example.DBConnection;
import org.example.DTOs.WorkoutDTO;
import org.example.Interfaces.WorkoutDAOInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDAO implements WorkoutDAOInterface {
    private Connection conn;

    public WorkoutDAO() throws SQLException {
        this.conn = DBConnection.getConnection();
    }

    @Override
    public void insertWorkout(WorkoutDTO workout) {
        // Check if workout object is null
        if (workout == null) {
            System.out.println("Workout object is null, please try again.");
            return;
        }

        // Validate workout data
        if (workout.getUserID() <= 0){
            System.out.println("Workout ID and User ID must be greater than 0, please try again.");
            return;
        }
        if (workout.getDuration() <= 0){
            System.out.println("Duration must be greater than 0, please try again.");
            return;
        }
        if (workout.getCaloriesBurned() < 0) {
            System.out.println("Error: Calories burned cannot be negative.");
            return;
        }
        if (workout.getWorkoutDate() == null) {
            System.out.println("Error: Workout date cannot be null.");
            return;
        }

        String checkQuery = "SELECT COUNT(*) FROM Workout WHERE workoutID = ? AND userID = ?";
        String query = "INSERT INTO Workout (userID, workoutType, duration, caloriesBurned, workoutDate, notes) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(query)
        ){
            checkStmt.setInt(1, workout.getWorkoutID());
            checkStmt.setInt(2, workout.getUserID());  // Ensure userID is correctly set in WorkoutDTO
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Workout with this ID and User ID already exists, please try again.");
                    return;
                }
            }

            insertStmt.setInt(1, workout.getUserID());
            insertStmt.setString(2, workout.getWorkoutType());
            insertStmt.setInt(3, workout.getDuration());
            insertStmt.setInt(4, workout.getCaloriesBurned());
            insertStmt.setDate(5, new java.sql.Date(workout.getWorkoutDate().getTime()));
            insertStmt.setString(6, workout.getNotes());
            int rowsInserted = insertStmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Workout added successfully.");
            } else {
                System.out.println("Error: Workout not added.");
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public WorkoutDTO getWorkoutById(int workoutID) {
        // Check if workoutID is valid
        if (workoutID <= 0){
            System.out.println("Workout ID must be greater than 0, please try again.");
            return null;
        }

        String query = "SELECT * FROM Workout WHERE workoutID = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, workoutID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return new WorkoutDTO(
                        rs.getInt("workoutID"),
                        rs.getInt("userID"),
                        rs.getString("workoutType"),
                        rs.getInt("duration"),
                        rs.getInt("caloriesBurned"),
                        rs.getDate("workoutDate"),
                        rs.getString("notes")
                );
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<WorkoutDTO> getAllWorkouts() {

        List<WorkoutDTO> workouts = new ArrayList<>();
        String query  = "SELECT * FROM Workout";

        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()){
                workouts.add(new WorkoutDTO(
                        rs.getInt("workoutID"),
                        rs.getInt("userID"),
                        rs.getString("workoutType"),
                        rs.getInt("duration"),
                        rs.getInt("caloriesBurned"),
                        rs.getDate("workoutDate"),
                        rs.getString("notes")
                ));
            }

            if (workouts.isEmpty()) {
                System.out.println("No workouts found in the database.");
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return workouts;
    }

    @Override
    public void updateWorkout(WorkoutDTO workout) {
        // Check if workout object is null
        if (workout == null) {
            System.out.println("Workout object is null, please try again.");
            return;
        }

        // Validate workout data
        if (workout.getWorkoutID() <= 0 || workout.getUserID() <= 0){
            System.out.println("Workout ID and User ID must be greater than 0, please try again.");
            return;
        }
        if (workout.getDuration() <= 0){
            System.out.println("Duration must be greater than 0, please try again.");
            return;
        }
        if (workout.getCaloriesBurned() < 0) {
            System.out.println("Error: Calories burned cannot be negative.");
            return;
        }
        if (workout.getWorkoutDate() == null) {
            System.out.println("Error: Workout date cannot be null.");
            return;
        }

        // Check if workout exists before updating
        String checkQuery = "SELECT COUNT(*) FROM Workout WHERE workoutID = ?";
        String updateQuery = "UPDATE Workout SET userID = ?, workoutType = ?, duration = ?, caloriesBurned = ?, workoutDate = ?, notes = ? WHERE workoutID = ?";

        try(PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            checkStmt.setInt(1, workout.getWorkoutID());
            try(ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() || rs.getInt(1) == 0) {
                    System.out.println("Workout does not exist, please try again.");
                    return;
                }
            }

            updateStmt.setInt(1, workout.getUserID());
            updateStmt.setString(2, workout.getWorkoutType());
            updateStmt.setInt(3, workout.getDuration());
            updateStmt.setInt(4, workout.getCaloriesBurned());
            updateStmt.setDate(5, (Date) workout.getWorkoutDate());
            updateStmt.setString(6, workout.getNotes());
            updateStmt.setInt(7, workout.getWorkoutID());

            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Workout updated successfully.");
            } else {
                System.out.println("Error: Workout update failed. No changes were made.");
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteWorkout(int workoutID) {
        // Check if workoutID is valid
        if (workoutID <= 0){
            System.out.println("Workout ID must be greater than 0, please try again.");
            return;
        }

        String checkQuery = "SELECT COUNT(*) FROM Workout WHERE workoutID = ?";
        String deleteQuery = "DELETE FROM Workout WHERE workoutID = ?";

        try(PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            // check if workout exists before deleting
            checkStmt.setInt(1, workoutID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() || rs.getInt(1) == 0) {
                    System.out.println("Workout does not exist, please try again.");
                    return;
                }

            }
            deleteStmt.setInt(1, workoutID);
            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Workout deleted successfully.");
            } else {
                System.out.println("Error: Workout deletion failed.");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    //Filter Workout by Duration Using Comparator
    @Override
    public List<WorkoutDTO> filterWorkoutsByDuration(List<WorkoutDTO> allWorkouts, int duration) {
        List<WorkoutDTO> filteredWorkouts = new ArrayList<>();

        try {
            for (WorkoutDTO workout : allWorkouts) {
                if (compareWorkoutsByDuration(workout.getDuration(), duration) == 0) {
                    filteredWorkouts.add(workout);
                }
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return filteredWorkouts;
    }

    // Comparator method for Duration
    private int compareWorkoutsByDuration(int duration1, int duration2) {
        return Integer.compare(duration1, duration2);
    }

}

