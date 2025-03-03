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
    public void insertWorkout(WorkoutDTO workout) throws SQLException {

    }

    @Override
    public WorkoutDTO getWorkoutById(int workoutID) throws SQLException {
        return null;
    }

    @Override
    public List<WorkoutDTO> getAllWorkouts() {
        List<WorkoutDTO> workouts = new ArrayList<>();
        String query  = "SELECT * FROM Workout";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                workouts.add(new WorkoutDTO(
                        rs.getInt("workoutID"),
                        rs.getInt("userID"),
                        rs.getString("workoutType"),
                        rs.getInt("duration"),
                        rs.getInt("caloriesBurned"),
                        rs.getDate("workoutDate"),
                        rs.getString("notes")));
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return workouts;
    }

    @Override
    public void updateWorkout(WorkoutDTO workout) throws SQLException {

    }

    @Override
    public void deleteWorkout(int workoutID) throws SQLException {

    }
}
