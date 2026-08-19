package com.mycompany.dental.clinic.dao;

import com.mycompany.dental.clinic.db.DBConnection;
import com.mycompany.dental.clinic.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public List<User> findAll() {
        String sql = "SELECT user_id, username, full_name, role FROM users ORDER BY full_name";

        List<User> users = new ArrayList<>();

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        null,
                        rs.getString("full_name"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list users", e);
        }

        return users;
    }

    public int insert(String username, String hashedPassword, String fullName, String role) {
        String sql = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.setString(3, fullName);
            statement.setString(4, role);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    public void update(int userId, String username, String fullName, String role) {
        String sql = "UPDATE users SET username = ?, full_name = ?, role = ? WHERE user_id = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, fullName);
            statement.setString(3, role);
            statement.setInt(4, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    public void updatePassword(int userId, String hashedPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, hashedPassword);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update password", e);
        }
    }

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
