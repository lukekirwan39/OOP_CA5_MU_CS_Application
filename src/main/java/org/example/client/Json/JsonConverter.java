package org.example.client.Json;

import org.example.client.DTOs.WorkoutDTO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class JsonConverter
{
    public static String workoutToJsonString(WorkoutDTO workout) {
        JSONObject json = new JSONObject();
        json.put("workoutID", workout.getWorkoutID());
        json.put("userID", workout.getUserID());
        json.put("workoutType", workout.getWorkoutType());
        json.put("duration", workout.getDuration());
        json.put("caloriesBurned", workout.getCaloriesBurned());
        json.put("workoutDate", workout.getWorkoutDate());
        json.put("notes", workout.getNotes());

        return json.toString(2);
    }


    public static JSONObject workoutToJson(WorkoutDTO workout) {
        JSONObject json = new JSONObject();
        json.put("workout Id", workout.getWorkoutID());
        json.put("user Id", workout.getUserID());
        json.put("workout type", workout.getWorkoutType());
        json.put("duration", workout.getDuration());
        json.put("calories burned", workout.getCaloriesBurned());
        json.put("workout date", workout.getWorkoutDate());
        json.put("notes", workout.getNotes());
        return json;
    }

    public static String workoutListToJsonString(List<WorkoutDTO> workouts){
        JSONArray jsonArray = new JSONArray();
        for (WorkoutDTO workout : workouts){
            jsonArray.put(workoutToJson(workout));
        }

        return jsonArray.toString(2);
    }
}
