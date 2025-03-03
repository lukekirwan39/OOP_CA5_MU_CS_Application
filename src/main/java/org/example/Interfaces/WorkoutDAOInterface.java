package org.example.Interfaces;

import org.example.DTOs.WorkoutDTO;

import java.sql.SQLException;
import java.util.List;

public interface WorkoutDAOInterface {
    void insertWorkout(WorkoutDTO workout) throws SQLException;
    WorkoutDTO getWorkoutById(int workoutID) throws SQLException;
    List<WorkoutDTO> getAllWorkouts() throws SQLException;
    void updateWorkout(WorkoutDTO workout) throws SQLException;
    void deleteWorkout(int workoutID) throws SQLException;
}
