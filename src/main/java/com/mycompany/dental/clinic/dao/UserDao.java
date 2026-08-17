package com.mycompany.dental.clinic.dao;

import com.mycompany.dental.clinic.db.DBConnection;
import com.mycompany.dental.clinic.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public User findByUsername(String username) {
        String sql = "SELECT user_id, username, password, full_name, role FROM users WHERE username = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to look up user by username", e);
        }
    }
}
