package org.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.example.db.DBConnection;
import org.example.model.User;

/**
 * UserDAO (Data Access Object)
 * Handles database operations related to user authentication.
 */
public class UserDAO {

    /**
     * Authenticates a user by username and password.
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
}
