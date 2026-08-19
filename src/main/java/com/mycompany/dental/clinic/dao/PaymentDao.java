package com.mycompany.dental.clinic.dao;

import com.mycompany.dental.clinic.db.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class PaymentDao {

    public int insert(int appointmentNo, BigDecimal consultationFee, BigDecimal treatmentCost,
            BigDecimal totalAmount, String paymentMethod, LocalDate billDate) {
        String sql = "INSERT INTO payments "
                + "(appointment_no, consultation_fee, treatment_cost, total_amount, payment_method, bill_date) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, appointmentNo);
            statement.setBigDecimal(2, consultationFee);
            statement.setBigDecimal(3, treatmentCost);
            statement.setBigDecimal(4, totalAmount);
            statement.setString(5, paymentMethod);
            statement.setDate(6, Date.valueOf(billDate));
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to record payment", e);
        }
    }
}
