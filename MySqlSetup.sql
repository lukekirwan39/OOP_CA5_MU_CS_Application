-- Create Database
CREATE
DATABASE IF NOT EXISTS FitnessTrackerDB;
USE
FitnessTrackerDB;

-- Create Workout Table
CREATE TABLE IF NOT EXISTS Workout
(
    workoutID
    INT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    userID
    INT
    NOT
    NULL,
    workoutType
    VARCHAR
(
    50
) NOT NULL,
    duration INT NOT NULL, -- Duration in minutes
    caloriesBurned INT NOT NULL,
    workoutDate DATE NOT NULL,
    notes TEXT DEFAULT NULL
    );

-- Insert Sample Data
INSERT INTO Workout (userID, workoutType, duration, caloriesBurned, workoutDate, notes)
VALUES (1, 'Cardio', 45, 400, '2025-02-01', 'Morning run at the park'),
       (2, 'Strength Training', 60, 500, '2025-02-02', 'Full-body weight training'),
       (3, 'Yoga', 30, 150, '2025-02-03', 'Relaxation and stretching session'),
       (4, 'HIIT', 40, 600, '2025-02-04', 'Intense interval training session'),
       (5, 'Cycling', 50, 450, '2025-02-05', 'Outdoor cycling session'),
       (6, 'Swimming', 60, 550, '2025-02-06', 'Laps at the local pool'),
       (7, 'Boxing', 45, 500, '2025-02-07', 'Heavy bag training and sparring'),
       (8, 'Pilates', 30, 200, '2025-02-08', 'Core strengthening workout'),
       (9, 'Rowing', 35, 420, '2025-02-09', 'High-resistance rowing session'),
       (10, 'Jump Rope', 20, 300, '2025-02-10', 'Intense jump rope cardio workout');
