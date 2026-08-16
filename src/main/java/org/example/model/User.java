package org.example.model;

/**
 * User Model
 * Represents a system user (Staff / Receptionist / Admin) who can log in.
 */
public class User {
    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String role;

    // Default Constructor
    public User() {
    }

    // Parameterized Constructor
    public User(int userId, String username, String password, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}
