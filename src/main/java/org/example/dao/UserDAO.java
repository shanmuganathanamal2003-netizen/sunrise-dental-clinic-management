package org.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.example.db.DBConnection;
import org.example.model.User;

/**
 * UserDAO (Data Access Object)
 * Handles database operations for user authentication and retrieval.
 */
public class UserDAO {

    /**
     * Authenticates a user by username and password.
     * 
     * NOTE: For production deployments, passwords should be salted and hashed (e.g. BCrypt / Argon2).
     * Plaintext matching is maintained for sample data compatibility in academic environments.
     * 
     * @param username user login name
     * @param password user password
     * @return User object if credentials are correct, null otherwise
     * @throws SQLException on database query failure
     */
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password, full_name, role FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username.trim());
            stmt.setString(2, password.trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a user by their user ID.
     */
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT user_id, username, password, full_name, role FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a user by their username.
     */
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, password, full_name, role FROM users WHERE LOWER(username) = LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all registered users in the system.
     */
    public java.util.List<User> getAllUsers() throws SQLException {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT user_id, username, password, full_name, role FROM users ORDER BY user_id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("role")
                ));
            }
        }
        return list;
    }

    /**
     * Retrieves all users with the role 'Doctor'.
     * Used to populate doctor selection dropdowns dynamically.
     * 
     * @return List of Doctor User objects
     * @throws SQLException on database error
     */
    public java.util.List<User> getAllDoctors() throws SQLException {
        return getUsersByRole("Doctor");
    }

    /**
     * Retrieves all users filtered by role.
     * 
     * @param role User role ('Admin', 'Doctor', 'Receptionist')
     * @return List of User objects
     * @throws SQLException on database error
     */
    public java.util.List<User> getUsersByRole(String role) throws SQLException {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT user_id, username, password, full_name, role FROM users WHERE LOWER(role) = LOWER(?) ORDER BY full_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")
                    ));
                }
            }
        }
        return list;
    }

    /**
     * Searches users by keyword (username or full name) with optional role filter.
     */
    public java.util.List<User> searchUsers(String keyword, String roleFilter) throws SQLException {
        java.util.List<User> list = new java.util.ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT user_id, username, password, full_name, role FROM users WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(username) LIKE ? OR LOWER(full_name) LIKE ?) ");
        }
        if (roleFilter != null && !roleFilter.trim().isEmpty() && !"All".equalsIgnoreCase(roleFilter)) {
            sql.append("AND LOWER(role) = LOWER(?) ");
        }
        sql.append("ORDER BY role ASC, full_name ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim().toLowerCase() + "%";
                stmt.setString(paramIndex++, searchPattern);
                stmt.setString(paramIndex++, searchPattern);
            }
            if (roleFilter != null && !roleFilter.trim().isEmpty() && !"All".equalsIgnoreCase(roleFilter)) {
                stmt.setString(paramIndex++, roleFilter.trim());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")
                    ));
                }
            }
        }
        return list;
    }

    /**
     * Checks if a username is already taken by another user.
     */
    public boolean isUsernameTaken(String username, int excludeUserId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(username) = LOWER(?) AND user_id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            stmt.setInt(2, excludeUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Creates a new staff/doctor/admin user.
     * 
     * @param user The user to create
     * @return Generated user ID
     * @throws SQLException on database failure
     */
    public int createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername().trim());
            stmt.setString(2, user.getPassword().trim());
            stmt.setString(3, user.getFullName().trim());
            stmt.setString(4, user.getRole().trim());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Updates an existing user's details.
     * 
     * @param user The user to update
     * @return true if updated successfully
     * @throws SQLException on database failure
     */
    public boolean updateUser(User user) throws SQLException {
        boolean hasPassword = user.getPassword() != null && !user.getPassword().trim().isEmpty();
        String sql = hasPassword
            ? "UPDATE users SET username = ?, password = ?, full_name = ?, role = ? WHERE user_id = ?"
            : "UPDATE users SET username = ?, full_name = ?, role = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            stmt.setString(paramIndex++, user.getUsername().trim());
            if (hasPassword) {
                stmt.setString(paramIndex++, user.getPassword().trim());
            }
            stmt.setString(paramIndex++, user.getFullName().trim());
            stmt.setString(paramIndex++, user.getRole().trim());
            stmt.setInt(paramIndex, user.getUserId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a user by their user ID.
     * 
     * @param userId The ID of the user to delete
     * @return true if deleted successfully
     * @throws SQLException on database failure
     */
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}

