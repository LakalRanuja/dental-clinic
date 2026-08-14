/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.dental.clinic.db;

/**
 *
 * @author User
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/dental_clinic"
            + "?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Single instance of DBConnection
    private static DBConnection instance;

    // Private constructor prevents object creation from outside
    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "MySQL JDBC Driver not found. "
                    + "Make sure MySQL Connector/J is added.",
                    e
            );
        }
    }

    // Returns the single DBConnection instance
    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }

        return instance;
    }

    // Creates and returns a JDBC connection
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Database connection failed!",
                    e
            );
        }
    }

    // Test database connection
    public static void main(String[] args) {

        try (Connection connection =
                     DBConnection.getInstance().getConnection()) {

            System.out.println(
                    "Database connection successful!"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}