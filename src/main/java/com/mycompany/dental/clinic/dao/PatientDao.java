package com.mycompany.dental.clinic.dao;

import com.mycompany.dental.clinic.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PatientDao {

    /**
     * Returns the id of an existing patient matching name + contact number,
     * or inserts a new one and returns its generated id.
     */
    public int findOrCreate(String name, String address, String contactNo) {
        String selectSql = "SELECT patient_id FROM patients WHERE name = ? AND contact_no = ?";
        String insertSql = "INSERT INTO patients (name, address, contact_no) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection()) {
            try (PreparedStatement select = connection.prepareStatement(selectSql)) {
                select.setString(1, name);
                select.setString(2, contactNo);
                try (ResultSet rs = select.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("patient_id");
                    }
                }
            }

            try (PreparedStatement insert = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insert.setString(1, name);
                insert.setString(2, address);
                insert.setString(3, contactNo);
                insert.executeUpdate();

                try (ResultSet keys = insert.getGeneratedKeys()) {
                    keys.next();
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find or create patient", e);
        }
    }
}
