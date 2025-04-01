package org.example.shared.DTOs;

import java.util.Date;

public class WorkoutDTO {
    private int workoutID;
    private int userID;
    private String workoutType;
    private int duration;
    private int caloriesBurned;
    private Date workoutDate;
    private String notes;

    public WorkoutDTO(int workoutID, int userID, String workoutType, int duration, int caloriesBurned, Date workoutDate, String notes) {
        this.workoutID = workoutID;
        this.userID = userID;
        this.workoutType = workoutType;
        this.duration = duration;
        this.caloriesBurned = caloriesBurned;
        this.workoutDate = workoutDate;
        this.notes = notes;
    }

    public int getWorkoutID() {
        return workoutID;
    }

    public void setWorkoutID(int workoutID) {
        this.workoutID = workoutID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public Date getWorkoutDate() {
        return workoutDate;
    }

    public void setWorkoutDate(Date workoutDate) {
        this.workoutDate = workoutDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "WorkoutDTO{" +
                "workoutID=" + workoutID +
                ", userID=" + userID +
                ", workoutType='" + workoutType + '\'' +
                ", duration=" + duration +
                ", caloriesBurned=" + caloriesBurned +
                ", workoutDate=" + workoutDate +
                ", notes='" + notes + '\'' +
                '}';
    }
}
