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
 * Handles database operations for user authentication and staff registration.
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
     * Checks if a username is already registered in the system.
     * 
     * @param username the username to check
     * @return true if taken, false if available
     * @throws SQLException on database query failure
     */
    public boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(username) = LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Registers a new user/staff member into the database.
     * 
     * @param user User object containing full name, username, password, and role
     * @return generated user_id if successful, -1 otherwise
     * @throws SQLException on database error
     */
    public int registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername().trim());
            stmt.setString(2, user.getPassword().trim());
            stmt.setString(3, user.getFullName().trim());
            stmt.setString(4, user.getRole() != null ? user.getRole().trim() : "Receptionist");

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        user.setUserId(id);
                        return id;
                    }
                }
            }
        }
        return -1;
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
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = "SELECT user_id, username, password, full_name, role FROM users WHERE role = 'Doctor' ORDER BY full_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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
}
