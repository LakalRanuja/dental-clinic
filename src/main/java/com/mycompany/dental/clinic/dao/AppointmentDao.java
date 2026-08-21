package com.mycompany.dental.clinic.dao;

import com.mycompany.dental.clinic.db.DBConnection;
import com.mycompany.dental.clinic.dto.AppointmentDetails;
import com.mycompany.dental.clinic.dto.AppointmentSummary;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDao {

    public int insert(int patientId, int dentistId, int treatmentId, int userId,
            LocalDate appointmentDate, LocalTime appointmentTime, String status) {
        String sql = "INSERT INTO appointments "
                + "(patient_id, dentist_id, treatment_id, user_id, appointment_date, appointment_time, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, patientId);
            statement.setInt(2, dentistId);
            statement.setInt(3, treatmentId);
            statement.setInt(4, userId);
            statement.setDate(5, Date.valueOf(appointmentDate));
            statement.setTime(6, Time.valueOf(appointmentTime));
            statement.setString(7, status);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create appointment", e);
        }
    }

    public void updatePaymentStatus(int appointmentNo, String paymentStatus) {
        String sql = "UPDATE appointments SET payment_status = ? WHERE appointment_no = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, paymentStatus);
            statement.setInt(2, appointmentNo);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update payment status", e);
        }
    }

    public AppointmentDetails findByAppointmentNo(int appointmentNo) {
        String sql = "SELECT a.appointment_no, p.name AS patient_name, p.address, p.contact_no, "
                + "d.name AS dentist_name, t.treatment_name, a.appointment_date, a.appointment_time, "
                + "a.status, a.payment_status, d.consultation_fee, t.base_cost "
                + "FROM appointments a "
                + "JOIN patients p ON a.patient_id = p.patient_id "
                + "JOIN dentists d ON a.dentist_id = d.dentist_id "
                + "JOIN treatment_types t ON a.treatment_id = t.treatment_id "
                + "WHERE a.appointment_no = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, appointmentNo);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return new AppointmentDetails(
                        rs.getInt("appointment_no"),
                        rs.getString("patient_name"),
                        rs.getString("address"),
                        rs.getString("contact_no"),
                        rs.getString("dentist_name"),
                        rs.getString("treatment_name"),
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime(),
                        rs.getString("status"),
                        rs.getString("payment_status"),
                        rs.getBigDecimal("consultation_fee"),
                        rs.getBigDecimal("base_cost")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to look up appointment", e);
        }
    }

    public List<AppointmentSummary> findAllSummaries() {
        String sql = "SELECT a.appointment_no, a.patient_id, d.name AS dentist_name, t.treatment_name, "
                + "a.appointment_date, a.appointment_time, u.full_name AS booked_by, a.status, a.payment_status "
                + "FROM appointments a "
                + "JOIN dentists d ON a.dentist_id = d.dentist_id "
                + "JOIN treatment_types t ON a.treatment_id = t.treatment_id "
                + "JOIN users u ON a.user_id = u.user_id "
                + "ORDER BY a.appointment_no ASC";

        List<AppointmentSummary> summaries = new ArrayList<>();

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                summaries.add(new AppointmentSummary(
                        rs.getInt("appointment_no"),
                        rs.getInt("patient_id"),
                        rs.getString("dentist_name"),
                        rs.getString("treatment_name"),
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime(),
                        rs.getString("booked_by"),
                        rs.getString("status"),
                        rs.getString("payment_status")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list appointments", e);
        }

        return summaries;
    }
}
