package com.mycompany.dental.clinic.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/dental_clinic";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static DBConnection instance;

    // Private constructor
    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found!", e);
        }
    }

    // Singleton instance
    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }

        return instance;
    }

    // Get database connection
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed!", e);
        }
    }

    // Test connection
    public static void main(String[] args) {
        try (Connection connection =
                     DBConnection.getInstance().getConnection()) {

            System.out.println("Database connection successful!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}