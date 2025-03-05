package org.example;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/FitnessTrackerDB";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection CONNECTION;

    public static Connection getConnection(){
        try {
            Class.forName(DRIVER);
            CONNECTION = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e){
            System.out.println("Failed to find driver class " + e.getMessage());
            System.exit(1);
        } catch (SQLException e){
            System.out.println("Connection failed " + e.getMessage());
            System.exit(2);
        }
        return CONNECTION;
    }
}
