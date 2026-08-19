package com.mycompany.dental.clinic.dao;

import com.mycompany.dental.clinic.db.DBConnection;
import com.mycompany.dental.clinic.model.Dentist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DentistDao {

    public int insert(String name, String specialization, String contactNumber) {
        String sql = "INSERT INTO dentists (name, specialization, contact_no) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            statement.setString(2, specialization);
            statement.setString(3, contactNumber);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create dentist", e);
        }
    }

    public List<Dentist> findAll() {
        String sql = "SELECT dentist_id, name, specialization, contact_no FROM dentists ORDER BY name";

        List<Dentist> dentists = new ArrayList<>();

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                dentists.add(new Dentist(
                        rs.getInt("dentist_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getString("contact_no")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list dentists", e);
        }

        return dentists;
    }

    public void update(int dentistId, String name, String specialization, String contactNumber) {
        String sql = "UPDATE dentists SET name = ?, specialization = ?, contact_no = ? WHERE dentist_id = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, specialization);
            statement.setString(3, contactNumber);
            statement.setInt(4, dentistId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update dentist", e);
        }
    }

    public List<Integer> findTreatmentIdsForDentist(int dentistId) {
        String sql = "SELECT treatment_id FROM dentist_treatment_types WHERE dentist_id = ?";

        List<Integer> treatmentIds = new ArrayList<>();

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, dentistId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    treatmentIds.add(rs.getInt("treatment_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load dentist's treatments", e);
        }

        return treatmentIds;
    }

    /** Replaces this dentist's treatment links wholesale (delete then insert) — safe for both create and update. */
    public void linkTreatments(int dentistId, List<Integer> treatmentIds) {
        String deleteSql = "DELETE FROM dentist_treatment_types WHERE dentist_id = ?";
        String insertSql = "INSERT INTO dentist_treatment_types (dentist_id, treatment_id) VALUES (?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection()) {
            try (PreparedStatement delete = connection.prepareStatement(deleteSql)) {
                delete.setInt(1, dentistId);
                delete.executeUpdate();
            }

            if (!treatmentIds.isEmpty()) {
                try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                    for (Integer treatmentId : treatmentIds) {
                        insert.setInt(1, dentistId);
                        insert.setInt(2, treatmentId);
                        insert.addBatch();
                    }
                    insert.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to link dentist to treatment types", e);
        }
    }

    /**
     * Returns dentists linked to the given treatment type whose name contains
     * the given text (case-insensitive), limited to 10 matches. An empty
     * query returns the first 10 matching dentists for that treatment.
     */
    public List<Dentist> searchByNameAndTreatment(String query, int treatmentId) {
        String sql = "SELECT d.dentist_id, d.name, d.specialization, d.contact_no "
                + "FROM dentists d "
                + "JOIN dentist_treatment_types dtt ON d.dentist_id = dtt.dentist_id "
                + "WHERE dtt.treatment_id = ? AND d.name LIKE ? "
                + "ORDER BY d.name LIMIT 10";

        List<Dentist> dentists = new ArrayList<>();

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, treatmentId);
            statement.setString(2, "%" + query + "%");

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    dentists.add(new Dentist(
                            rs.getInt("dentist_id"),
                            rs.getString("name"),
                            rs.getString("specialization"),
                            rs.getString("contact_no")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search dentists", e);
        }

        return dentists;
    }
}
