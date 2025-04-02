package org.example.DAOs;

import org.example.server.util.JsonConverter;
import org.example.server.DAOs.WorkoutDAO;
import org.example.shared.DTOs.WorkoutDTO;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class WorkoutDAOTest {

    @Test
    void testInsertWorkout() throws SQLException {
        WorkoutDAO workoutDAO = new WorkoutDAO();
        WorkoutDTO workout= new WorkoutDTO(15,13,"Strength Training", 45, 350, java.sql.Date.valueOf("2025-02-15"), "Evening session");
        workoutDAO.insertWorkout(workout);

        WorkoutDTO retrievedWorkout = workoutDAO.getWorkoutById(15);

        assertNotNull(retrievedWorkout, "Workout should not be null");
        assertEquals(15, retrievedWorkout.getWorkoutID());
        assertEquals("Strength Training", retrievedWorkout.getWorkoutType());
        assertEquals(45, retrievedWorkout.getDuration());
        assertEquals(350, retrievedWorkout.getCaloriesBurned());
        assertEquals(java.sql.Date.valueOf("2025-02-15"), retrievedWorkout.getWorkoutDate());
        assertEquals("Evening session", retrievedWorkout.getNotes());
    }



    @Test
    void getWorkoutById() throws SQLException {
        WorkoutDAO workoutDAO = new WorkoutDAO();
        WorkoutDTO workout = new WorkoutDTO(15, 13, "Strength Training", 45, 350, java.sql.Date.valueOf("2025-02-15"), "Evening session");
        workoutDAO.insertWorkout(workout);

        WorkoutDTO retrievedWorkout = workoutDAO.getWorkoutById(15);

        assertNotNull(retrievedWorkout, "Workout should not be null");
        assertEquals(15, retrievedWorkout.getWorkoutID());
        assertEquals("Strength Training", retrievedWorkout.getWorkoutType());
        assertEquals(45, retrievedWorkout.getDuration());
        assertEquals(350, retrievedWorkout.getCaloriesBurned());
        assertEquals(java.sql.Date.valueOf("2025-02-15"), retrievedWorkout.getWorkoutDate());
        assertEquals("Evening session", retrievedWorkout.getNotes());



    }
    @Test
    void getWorkoutById_NonExistent() throws SQLException {
        WorkoutDAO workoutDAO = new WorkoutDAO();
        WorkoutDTO result = workoutDAO.getWorkoutById(-1); // Invalid ID
        assertNull(result);
    }

    @Test
    void insertWorkout_InvalidData() throws SQLException {
        WorkoutDAO workoutDAO = new WorkoutDAO();
        // Missing required fields
        WorkoutDTO invalid = new WorkoutDTO(0, 0, null, 0, -1, null, null);
        String result = workoutDAO.insertWorkout(invalid);
        assertTrue(result.contains("Invalid") || result.contains("failed"));
    }

    @Test
    void getAllWorkouts()  throws SQLException {
        WorkoutDAO workoutDAO = new WorkoutDAO();

        WorkoutDTO workout1 = new WorkoutDTO(16, 13, "Cardio", 30, 200, java.sql.Date.valueOf("2025-03-01"), "Morning run");
        workoutDAO.insertWorkout(workout1);

        List<WorkoutDTO> allWorkouts = workoutDAO.getAllWorkouts();

        assertNotNull(allWorkouts, "Workout list should not be null");
        assertFalse(allWorkouts.isEmpty(), "Workout list should not be empty");



    }

    @Test
    void updateWorkout() throws SQLException {
        WorkoutDAO workoutDAO = new WorkoutDAO();
        WorkoutDTO existingWorkout = workoutDAO.getWorkoutById(16);
        assertNotNull(existingWorkout, "Workout should exist before updating");

        WorkoutDTO updatedWorkout = new WorkoutDTO(16, 13, "HIIT", 45, 350, java.sql.Date.valueOf("2025-03-02"), "Intense session");
        workoutDAO.updateWorkout(updatedWorkout);

        WorkoutDTO retrievedWorkout = workoutDAO.getWorkoutById(16);

        assertNotNull(retrievedWorkout, "Workout should still exist after updating");
        assertEquals(16, retrievedWorkout.getWorkoutID(), "Workout ID should match");
        assertEquals("HIIT", retrievedWorkout.getWorkoutType(), "Workout type should be updated");
        assertEquals(45, retrievedWorkout.getDuration(), "Duration should be updated");
        assertEquals(350, retrievedWorkout.getCaloriesBurned(), "Calories burned should be updated");
        assertEquals(java.sql.Date.valueOf("2025-03-02"), retrievedWorkout.getWorkoutDate(), "Workout date should be updated");
        assertEquals("Intense session", retrievedWorkout.getNotes(), "Notes should be updated");




    }

    @Test
    void deleteWorkout() throws SQLException {
        WorkoutDAO workoutDAO = new WorkoutDAO();

        WorkoutDTO existingWorkout = workoutDAO.getWorkoutById(17);
        assertNotNull(existingWorkout, "Workout should exist before deletion");

        workoutDAO.deleteWorkout(17);

        WorkoutDTO deletedWorkout = workoutDAO.getWorkoutById(17);
        assertNull(deletedWorkout, "Workout should be deleted");



    }

    @Test
    void filterWorkoutsByDuration() throws SQLException {
        WorkoutDAO workoutDAO = new WorkoutDAO();
        List<WorkoutDTO> filtered = workoutDAO.filterWorkoutsByDuration(workoutDAO.getAllWorkouts(), 30);

        assertNotNull(filtered, "Workout list should not be null");

    }

    @Test
    void convertEntityToJson() {
        WorkoutDTO workout = new WorkoutDTO(1, 1, "Running", 30, 200,
                java.sql.Date.valueOf("2025-01-01"), "Test");
        String json = JsonConverter.workoutToJsonString(workout);
        assertNotNull(json);
        assertTrue(json.contains("Running"));
        assertTrue(json.contains("2025-01-01"));
    }
    @Test
    void convertListToJson() {
        List<WorkoutDTO> workouts = List.of( new WorkoutDTO(21,21,"Running",30,200,java.sql.Date.valueOf("2025-03-02"),null),
                new WorkoutDTO(22,22,"Cycling",45,300,java.sql.Date.valueOf("2025-03-02"),null));
        String json = JsonConverter.workoutListToJsonString(workouts);
        assertNotNull(json);
        assertTrue(json.contains("Cycling"));
        assertTrue(json.contains("Running"));

    }




}