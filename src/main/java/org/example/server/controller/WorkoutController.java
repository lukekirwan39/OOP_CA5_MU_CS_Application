package org.example.server.controller;

import org.example.server.DAOs.WorkoutDAO;
import org.example.shared.DTOs.WorkoutDTO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class WorkoutController {
    private final WorkoutDAO workoutDAO;

    public WorkoutController() throws Exception {
        workoutDAO = new WorkoutDAO();
    }

    public String handleRequest(String jsonRequest) {
        JSONObject request = new JSONObject(jsonRequest);
        String action = request.getString("action");

        switch (action) {
            case "getAllWorkouts":
                return getAllWorkouts();
            case "insertWorkout":
                return insertWorkout(request.getJSONObject("workout"));

            case "getWorkoutById":
                return getWorkoutById(request.getInt("id"));

            default:
                return new JSONObject().put("error", "Unknown action").toString();
        }
    }

    private String getAllWorkouts() {
        List<WorkoutDTO> workouts = workoutDAO.getAllWorkouts();
        JSONArray array = new JSONArray();
        for (WorkoutDTO dto : workouts) {
            JSONObject obj = new JSONObject();
            obj.put("workoutID", dto.getWorkoutID());
            obj.put("userID", dto.getUserID());
            obj.put("workoutType", dto.getWorkoutType());
            obj.put("duration", dto.getDuration());
            obj.put("caloriesBurned", dto.getCaloriesBurned());
            obj.put("workoutDate", dto.getWorkoutDate().toString());
            obj.put("notes", dto.getNotes());
            array.put(obj);
        }
        return array.toString();
    }

    private String insertWorkout(JSONObject workoutJson) {
        WorkoutDTO workout = new WorkoutDTO(
                0,
                workoutJson.getInt("userID"),
                workoutJson.getString("workoutType"),
                workoutJson.getInt("duration"),
                workoutJson.getInt("caloriesBurned"),
                java.sql.Date.valueOf(workoutJson.getString("workoutDate")),
                workoutJson.getString("notes")
        );
        return new JSONObject().put("message", workoutDAO.insertWorkout(workout)).toString();
    }

    private String getWorkoutById(int id) {
        WorkoutDTO dto = workoutDAO.getWorkoutById(id);
        if (dto == null) {
            return new JSONObject().put("error", "Workout not found").toString();
        }

        JSONObject obj = new JSONObject();
        obj.put("workoutID", dto.getWorkoutID());
        obj.put("userID", dto.getUserID());
        obj.put("workoutType", dto.getWorkoutType());
        obj.put("duration", dto.getDuration());
        obj.put("caloriesBurned", dto.getCaloriesBurned());
        obj.put("workoutDate", dto.getWorkoutDate().toString());
        obj.put("notes", dto.getNotes());

        return obj.toString();
    }
}