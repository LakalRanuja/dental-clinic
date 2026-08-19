package com.mycompany.dental.clinic.controller;

import com.mycompany.dental.clinic.dao.UserDao;
import com.mycompany.dental.clinic.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UserController {

    private final UserDao userDao = new UserDao();

    /**
     * Returns the authenticated user with the password field cleared, or
     * null if the username doesn't exist or the password doesn't match.
     */
    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user == null || !passwordMatches(password, user.getPassword())) {
            return null;
        }

        user.setPassword(null);
        return user;
    }

    private boolean passwordMatches(String rawPassword, String storedHash) {
        try {
            return BCrypt.checkpw(rawPassword, storedHash);
        } catch (IllegalArgumentException e) {
            // storedHash isn't a valid BCrypt hash (e.g. legacy plaintext data)
            return false;
        }
    }

    /**
     * Creates a new staff/admin account. Throws IllegalStateException if the
     * username is already taken.
     */
    public User register(String username, String password, String fullName, String role) {
        if (userDao.findByUsername(username) != null) {
            throw new IllegalStateException("Username already exists");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        int userId = userDao.insert(username, hashedPassword, fullName, role);
        return new User(userId, username, null, fullName, role);
    }

    public List<User> listAll() {
        return userDao.findAll();
    }

    /**
     * Updates username/full name/role. Password is left unchanged if
     * {@code password} is null/blank. Throws IllegalStateException if the
     * new username is already taken by a different account.
     */
    public User update(int userId, String username, String password, String fullName, String role) {
        User existing = userDao.findByUsername(username);
        if (existing != null && existing.getUserId() != userId) {
            throw new IllegalStateException("Username already exists");
        }

        userDao.update(userId, username, fullName, role);
        if (password != null && !password.isBlank()) {
            userDao.updatePassword(userId, BCrypt.hashpw(password, BCrypt.gensalt()));
        }
        return new User(userId, username, null, fullName, role);
    }
}
