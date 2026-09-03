package org.example.dao;

import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    /**
     * TEST 1 - Read-only test.
     * Validates that logging in with the correct username and password
     * (the seeded default admin account) succeeds.
     */
    @Test
    @DisplayName("authenticate() succeeds with correct username and password")
    void testAuthenticate_correctCredentials_succeeds() throws SQLException {
        User user = userDAO.authenticate("admin", "admin123");

        assertNotNull(user, "Expected login to succeed with correct admin credentials");
        assertEquals("admin", user.getUsername());
        assertEquals("Admin", user.getRole());
    }

    /**
     * TEST 2 - Read-only test.
     * Validates that logging in with a WRONG password is correctly rejected.
     */
    @Test
    @DisplayName("authenticate() fails with incorrect password")
    void testAuthenticate_wrongPassword_fails() throws SQLException {
        User user = userDAO.authenticate("admin", "wrongpassword123");

        assertNull(user, "Expected login to fail with an incorrect password");
    }

    /**
     * TEST 3 - Read-only test.
     * Validates that logging in with a username that doesn't exist is
     * correctly rejected.
     */
    @Test
    @DisplayName("authenticate() fails with a username that doesn't exist")
    void testAuthenticate_unknownUsername_fails() throws SQLException {
        User user = userDAO.authenticate("this_user_does_not_exist", "anything");

        assertNull(user, "Expected login to fail for a non-existent username");
    }
}