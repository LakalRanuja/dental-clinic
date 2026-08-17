package com.mycompany.dental.clinic.dao;

import com.mycompany.dental.clinic.db.DBConnection;
import com.mycompany.dental.clinic.dto.DashboardStats;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DashboardDao {

    public DashboardStats getStats() {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM appointments WHERE appointment_date = CURDATE()) AS todays_appointments, "
                + "(SELECT COUNT(*) FROM appointments) AS total_appointments, "
                + "(SELECT COUNT(*) FROM appointments WHERE appointment_date > CURDATE()) AS upcoming_appointments, "
                + "(SELECT COUNT(*) FROM patients) AS total_patients";

        try (Connection connection = DBConnection.getInstance().getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)) {

            rs.next();
            return new DashboardStats(
                    rs.getInt("todays_appointments"),
                    rs.getInt("total_appointments"),
                    rs.getInt("upcoming_appointments"),
                    rs.getInt("total_patients")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load dashboard stats", e);
        }
    }
}
