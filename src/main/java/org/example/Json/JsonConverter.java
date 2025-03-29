package org.example.Json;

import org.example.DTOs.WorkoutDTO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

public class JsonConverter
{

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public static String convertEntityToJson(WorkoutDTO workout) {
        JSONObject json = new JSONObject();
        json.put("workoutID", workout.getWorkoutID());
        json.put("userID", workout.getUserID());
        json.put("workoutType", workout.getWorkoutType());
        json.put("duration", workout.getDuration());
        json.put("caloriesBurned", workout.getCaloriesBurned());
        json.put("workoutDate", dateFormat.format(workout.getWorkoutDate()));
        json.put("notes", workout.getNotes());

        return json.toString();
    }


    public static String convertListToJson(List<WorkoutDTO> workouts) {
        JSONArray jsonArray = new JSONArray();

        for (WorkoutDTO workout : workouts) {
            JSONObject json = new JSONObject();
            json.put("workoutID", workout.getWorkoutID());
            json.put("userID", workout.getUserID());
            json.put("workoutType", workout.getWorkoutType());
            json.put("duration", workout.getDuration());
            json.put("caloriesBurned", workout.getCaloriesBurned());
            json.put("workoutDate", dateFormat.format(workout.getWorkoutDate()));
            json.put("notes", workout.getNotes());

            jsonArray.put(json);
        }

        return jsonArray.toString();
    }
}
