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

    /**
     * TEST 4 - CRUD & Role Management test.
     * Validates creating, retrieving, updating, and deleting a doctor/staff user.
     */
    @Test
    @DisplayName("createUser(), updateUser(), and deleteUser() work correctly for staff management")
    void testUserCrudOperations() throws SQLException {
        String testUsername = "temp_doc_" + System.currentTimeMillis();
        User newDoctor = new User(0, testUsername, "pass123", "Dr. Test Specialist", "Doctor");

        // 1. Create
        int createdId = userDAO.createUser(newDoctor);
        assertTrue(createdId > 0, "Expected new user to be created with valid ID");

        // 2. Check username taken
        assertTrue(userDAO.isUsernameTaken(testUsername, -1), "Expected username to be recognized as taken");
        assertFalse(userDAO.isUsernameTaken(testUsername, createdId), "Username should not be taken when excluding its own user ID");

        // 3. Retrieve
        User fetched = userDAO.getUserById(createdId);
        assertNotNull(fetched, "Expected created user to be retrievable");
        assertEquals("Dr. Test Specialist", fetched.getFullName());
        assertEquals("Doctor", fetched.getRole());

        // 4. Update
        fetched.setFullName("Dr. Test Specialist Updated");
        fetched.setRole("Doctor");
        boolean updated = userDAO.updateUser(fetched);
        assertTrue(updated, "Expected user update to succeed");

        User updatedFetched = userDAO.getUserById(createdId);
        assertEquals("Dr. Test Specialist Updated", updatedFetched.getFullName());

        // 5. Delete (Clean up)
        boolean deleted = userDAO.deleteUser(createdId);
        assertTrue(deleted, "Expected user deletion to succeed");

        User afterDelete = userDAO.getUserById(createdId);
        assertNull(afterDelete, "Expected deleted user to no longer exist");
    }

    /**
     * TEST 5 - Role filtering & Search test.
     */
    @Test
    @DisplayName("getUsersByRole() and searchUsers() return filtered results")
    void testRoleFilteringAndSearch() throws SQLException {
        java.util.List<User> doctors = userDAO.getUsersByRole("Doctor");
        assertNotNull(doctors);
        for (User doc : doctors) {
            assertEquals("Doctor", doc.getRole());
        }

        java.util.List<User> searchResults = userDAO.searchUsers("admin", "Admin");
        assertNotNull(searchResults);
        assertFalse(searchResults.isEmpty(), "Expected to find at least one admin account");
    }
}