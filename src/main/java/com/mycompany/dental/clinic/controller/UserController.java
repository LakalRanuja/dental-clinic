package com.mycompany.dental.clinic.controller;

import com.mycompany.dental.clinic.dao.UserDao;
import com.mycompany.dental.clinic.model.User;
import org.mindrot.jbcrypt.BCrypt;

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
}
