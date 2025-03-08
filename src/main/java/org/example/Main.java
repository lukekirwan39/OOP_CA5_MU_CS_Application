package org.example;

import org.example.DAOs.WorkoutDAO;
import org.example.DTOs.WorkoutDTO;
import org.example.Interfaces.WorkoutDAOInterface;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            WorkoutDAOInterface wDAOInterface = new WorkoutDAO();
            Scanner scanner = new Scanner(System.in);

            while (true) {
                try {
                    System.out.println("\n1. List all workouts");
                    System.out.println("2. Find and Display by workout ID");
                    System.out.println("3. Delete a workout");
                    System.out.println("4. Add a workout");
                    System.out.println("5. Update an existing workout");
                    System.out.println("6. Filter workouts by duration");
                    System.out.println("7. Exit");
                    System.out.print("Enter your choice: ");

                    if (!scanner.hasNextInt()) {
                        System.out.println("Invalid input, please try again.");
                        scanner.next();
                        continue;
                    }

                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:
                            // List all workouts
                            System.out.println("All Workouts");
                            List<WorkoutDTO> workouts = wDAOInterface.getAllWorkouts();
                            if (workouts.isEmpty()) {
                                System.out.println("No workouts found.");
                            } else {
                                for (WorkoutDTO w : workouts) {
                                    System.out.println(w);
                                }
                            }
                            break;

                        case 2:
                            // Find and display by workout ID
                            System.out.println("Enter workout ID to search: ");
                            if (!scanner.hasNextInt()) {
                                System.out.println("Invalid input, please try again.");
                                scanner.next();
                                break;
                            }
                            int workoutId = scanner.nextInt();
                            WorkoutDTO workout = wDAOInterface.getWorkoutById(workoutId);

                            if (workout == null) {
                                System.out.println("Workout not found.");
                            } else {
                                System.out.println(workout);
                            }
                            break;

                        case 3:
                            // Delete a workout by ID
                            System.out.println("Enter Workout ID to delete: ");
                            if (!scanner.hasNextInt()) {
                                System.out.println("Invalid input, please try again.");
                                scanner.next();
                                break;
                            }
                            int deleteId = scanner.nextInt();

                            if (wDAOInterface.getWorkoutById(deleteId) == null) {
                                System.out.println("Workout ID does not exist.");
                            } else {
                                wDAOInterface.deleteWorkout(deleteId);
                                System.out.println("Workout deleted: " + deleteId);
                            }
                            break;
                        case 4:
                            // Add a workout
                            System.out.println("\nAdd a Workout");

                            System.out.println("Enter your User ID: ");
                            if (!scanner.hasNextInt()) {
                                System.out.println("Invalid input! Workout ID must be a number.");
                                scanner.next();
                                break;
                            }
                            int id = scanner.nextInt();
                            System.out.print("Enter Workout Type: ");
                            String type = scanner.next();
                            System.out.print("Enter Duration: ");
                            if (!scanner.hasNextInt()) {
                                System.out.println("Invalid input! Duration must be a number.");
                                scanner.next();
                                break;
                            }
                            int duration = scanner.nextInt();
                            System.out.print("Enter Calories Burned: ");
                            if (!scanner.hasNextInt()) {
                                System.out.println("Invalid input! Calories burned must be a number.");
                                scanner.next();
                                break;
                            }
                            int caloriesBurned = scanner.nextInt();
                            System.out.print("Enter Workout Date (yyyy-mm-dd): ");
                            String workoutDate = scanner.next();
                            System.out.print("Enter Notes: ");
                            scanner.nextLine();
                            String notes = scanner.next();
                            wDAOInterface.insertWorkout(new WorkoutDTO(0, id, type, duration, caloriesBurned, java.sql.Date.valueOf(workoutDate), notes));
                            System.out.println("Workout added successfully.");
                            break;
                        case 5:
                            // Update an existing workout
                            System.out.println("Enter the workout ID to update: ");
                            if (!scanner.hasNextInt()) {
                                System.out.println("Invalid input! Please enter a valid workout ID.");
                                scanner.next();
                                break;
                            }
                            int updateId = scanner.nextInt();
                            WorkoutDTO updatedWorkout = wDAOInterface.getWorkoutById(updateId);
                            if (updatedWorkout == null) {
                                System.out.println("Workout ID does not exist.");
                                break;
                            }
                            System.out.println("Enter new details for the workout: ");

                            System.out.print("Enter new Workout Type: ");
                            String newType = scanner.next();

                            System.out.print("Enter new Duration: ");
                            if (!scanner.hasNextInt()) {
                                System.out.println("Invalid input! Duration must be a number.");
                                scanner.next();
                                break;
                            }
                            int newDuration = scanner.nextInt();

                            System.out.print("Enter new Calories Burned: ");
                            if (!scanner.hasNextInt()) {
                                System.out.println("Invalid input! Calories burned must be a number.");
                                scanner.next();
                                break;
                            }
                            int newCaloriesBurned = scanner.nextInt();

                            System.out.print("Enter new Workout Date (yyyy-mm-dd): ");
                            String newWorkoutDate = scanner.next();

                            System.out.print("Enter new Notes: ");
                            scanner.nextLine();
                            String newNotes = scanner.next();

                            updatedWorkout.setWorkoutType(newType);
                            updatedWorkout.setDuration(newDuration);
                            updatedWorkout.setCaloriesBurned(newCaloriesBurned);
                            updatedWorkout.setWorkoutDate(java.sql.Date.valueOf(newWorkoutDate));
                            updatedWorkout.setNotes(newNotes);

                            wDAOInterface.updateWorkout(updatedWorkout);
                            System.out.println("Workout updated");

                            System.out.println("Workout updated successfully: " + updatedWorkout);
                            break;
                        case 6:
                            // Filter workouts by duration
                            System.out.println("\nPlease insert a duration(hours) to filter by: ");
                            if (!scanner.hasNextInt()) {
                                System.out.println("Invalid input! Duration must be a number.");
                                scanner.next();
                                break;
                            }
                            int durationFilter = scanner.nextInt();

                            List<WorkoutDTO> allWorkouts = wDAOInterface.getAllWorkouts();
                            if (allWorkouts == null || allWorkouts.isEmpty()) {
                                System.out.println("No workouts available.");
                                break;
                            }

                            List<WorkoutDTO> filteredWorkoutsByDuration = wDAOInterface.filterWorkoutsByDuration(allWorkouts, durationFilter);
                            if (filteredWorkoutsByDuration == null || filteredWorkoutsByDuration.isEmpty()) {
                                System.out.println("No workouts found with duration " + durationFilter + " hours.");
                            } else {
                                for (WorkoutDTO w : filteredWorkoutsByDuration) {
                                    System.out.println(w);
                                }
                            }
                            break;
                        case 7:
                            System.out.println("Exiting program...");
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Invalid choice, please try again.");
                            break;

                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input, please try again.");
                    scanner.next();
                }
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}