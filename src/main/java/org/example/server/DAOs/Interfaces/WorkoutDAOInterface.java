package org.example.server.DAOs.Interfaces;


import org.example.shared.DTOs.WorkoutDTO;

import java.sql.SQLException;
import java.util.List;

public interface WorkoutDAOInterface {
    String insertWorkout(WorkoutDTO workout) throws SQLException;
    WorkoutDTO getWorkoutById(int workoutID) throws SQLException;
    List<WorkoutDTO> getAllWorkouts() throws SQLException;
    void updateWorkout(WorkoutDTO workout) throws SQLException;
    void deleteWorkout(int workoutID) throws SQLException;
    List<WorkoutDTO> filterWorkoutsByDuration(List<WorkoutDTO> allWorkouts, int duration) throws SQLException;
}
