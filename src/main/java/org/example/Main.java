package org.example;

import org.example.DAOs.WorkoutDAO;
import org.example.DTOs.WorkoutDTO;
import org.example.Interfaces.WorkoutDAOInterface;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {

            WorkoutDAOInterface wDAOInterface = new WorkoutDAO();
            List<WorkoutDTO> workouts = wDAOInterface.getAllWorkouts();
            for (WorkoutDTO w: workouts){
                System.out.println(w);
            }

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}