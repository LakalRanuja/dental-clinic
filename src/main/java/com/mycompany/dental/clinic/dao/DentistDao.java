package com.mycompany.dental.clinic.dao;

import com.mycompany.dental.clinic.db.DBConnection;
import com.mycompany.dental.clinic.model.Dentist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DentistDao {

    /**
     * Returns dentists whose name contains the given text (case-insensitive),
     * limited to 10 matches. An empty query returns the first 10 dentists.
     */
    public List<Dentist> searchByName(String query) {
        String sql = "SELECT dentist_id, name, specialization, contact_no "
                + "FROM dentists WHERE name LIKE ? ORDER BY name LIMIT 10";

        List<Dentist> dentists = new ArrayList<>();

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "%" + query + "%");

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
