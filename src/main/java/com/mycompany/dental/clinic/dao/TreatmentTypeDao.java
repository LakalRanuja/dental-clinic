package com.mycompany.dental.clinic.dao;

import com.mycompany.dental.clinic.db.DBConnection;
import com.mycompany.dental.clinic.model.TreatmentType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TreatmentTypeDao {

    public List<TreatmentType> findAll() {
        String sql = "SELECT treatment_id, treatment_name, description, base_cost "
                + "FROM treatment_types ORDER BY treatment_name";

        List<TreatmentType> types = new ArrayList<>();

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                types.add(new TreatmentType(
                        rs.getInt("treatment_id"),
                        rs.getString("treatment_name"),
                        rs.getString("description"),
                        rs.getBigDecimal("base_cost")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list treatment types", e);
        }

        return types;
    }
}
