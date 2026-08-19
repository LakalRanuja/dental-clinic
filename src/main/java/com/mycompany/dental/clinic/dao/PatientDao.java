package com.mycompany.dental.clinic.dao;

import com.mycompany.dental.clinic.db.DBConnection;
import com.mycompany.dental.clinic.model.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PatientDao {

    public List<Patient> findAll() {
        String sql = "SELECT patient_id, name, address, contact_no FROM patients ORDER BY name";

        List<Patient> patients = new ArrayList<>();

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                patients.add(new Patient(
                        rs.getInt("patient_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("contact_no")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list patients", e);
        }

        return patients;
    }

    public void update(int patientId, String name, String address, String contactNumber) {
        String sql = "UPDATE patients SET name = ?, address = ?, contact_no = ? WHERE patient_id = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, address);
            statement.setString(3, contactNumber);
            statement.setInt(4, patientId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update patient", e);
        }
    }

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
