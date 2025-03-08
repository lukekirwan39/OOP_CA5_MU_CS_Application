package org.example;

import org.example.DAOs.WorkoutDAO;
import org.example.DTOs.WorkoutDTO;
import org.example.Interfaces.WorkoutDAOInterface;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {

            Scanner scanner = new Scanner(System.in);

            WorkoutDAOInterface wDAOInterface = new WorkoutDAO();
            System.out.println("All Workouts");
            List<WorkoutDTO> workouts = wDAOInterface.getAllWorkouts();
            for (WorkoutDTO w: workouts){
                System.out.println(w);
            }

            System.out.println();
            System.out.println("enter workout id to search");
            int workoutId = scanner.nextInt();
            WorkoutDTO workout = wDAOInterface.getWorkoutById(workoutId);
            System.out.println(workout);

            System.out.println();

            // Delete a workout by ID
            System.out.println("Enter Workout ID to delete: ");
            int deleteId = scanner.nextInt();
            wDAOInterface.deleteWorkout(deleteId);
            System.out.println("Workout deleted"+deleteId);

            System.out.println("\nAdd a Workout");

            System.out.println("Enter your Workout ID: ");
            int id = scanner.nextInt();
            System.out.print("Enter Workout Type: ");
            String type = scanner.next();
            System.out.print("Enter Duration: ");
            int duration = scanner.nextInt();
            System.out.print("Enter Calories Burned: ");
            int caloriesBurned = scanner.nextInt();
            System.out.print("Enter Workout Date (yyyy-mm-dd): ");
            String workoutDate = scanner.next();
            System.out.print("Enter Notes: ");
            String notes = scanner.next();
            wDAOInterface.insertWorkout(new WorkoutDTO(0, id, type, duration, caloriesBurned, java.sql.Date.valueOf(workoutDate), notes));

            System.out.println("\nPlease insert a duration(hours) to filter by: ");
            int durationFilter = scanner.nextInt();
            List<WorkoutDTO> filteredWorkoutsByDuration = wDAOInterface.filterWorkoutsByDuration(workouts, durationFilter);
            for (WorkoutDTO w: filteredWorkoutsByDuration){
                System.out.println(w);
            }

            System.out.println();
            System.out.println("Enter the workout ID to update: ");
            int updateId = scanner.nextInt();
            WorkoutDTO updatedWorkout = wDAOInterface.getWorkoutById(updateId);
            if (updatedWorkout != null){
                System.out.println("Enter new details for the workout: ");

                System.out.print("Enter new Workout Type: ");
                String newtype = scanner.next();
                System.out.print("Enter new Duration: ");
                int newduration = scanner.nextInt();
                System.out.print("Enter new Calories Burned: ");
                int newcaloriesBurned = scanner.nextInt();
                System.out.print("Enter new Workout Date (yyyy-mm-dd): ");
                String newworkoutDate = scanner.next();
                System.out.print("Enter new Notes: ");
                String newnotes = scanner.next();

                updatedWorkout.setWorkoutType(newtype);
                updatedWorkout.setDuration(newduration);
                updatedWorkout.setCaloriesBurned(newcaloriesBurned);
                updatedWorkout.setWorkoutDate(java.sql.Date.valueOf(newworkoutDate));
                updatedWorkout.setNotes(newnotes);

                wDAOInterface.updateWorkout(updatedWorkout);
                System.out.println("Workout updated");

                System.out.println("Updated workout: "+updatedWorkout);

            }

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}