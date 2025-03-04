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
            List<WorkoutDTO> workouts = wDAOInterface.getAllWorkouts();
            for (WorkoutDTO w: workouts){
                System.out.println(w);
            }

            System.out.println();

            WorkoutDTO workout = wDAOInterface.getWorkoutById(2);
            System.out.println(workout);

            System.out.println();

            wDAOInterface.deleteWorkout(10);

            System.out.println();

            System.out.println("Enter your User ID: ");
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

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}