package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * DBConnection - Database Connection Manager
 * 
 * Handles connecting to MySQL database via JDBC.
 * Configured for standard WAMP / XAMPP local environment:
 * - Host: localhost:3306
 * - Database: sunrise_dental_db
 * - Username: root
 * - Password: (empty)
 */
public class DBConnection {

    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/sunrise_dental_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // Static initializer block to load the JDBC driver class once
    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please ensure mysql-connector-j is included in pom.xml: " + e.getMessage());
        }
    }

    /**
     * Obtains a new database connection.
     * 
     * @return Connection object if successful
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Helper method to test if database connection is active.
     * Displays a friendly dialog message if WAMP / MySQL is not running.
     * 
     * @param showDialog whether to show a popup dialog on error
     * @return true if connection succeeds, false otherwise
     */
    public static boolean testConnection(boolean showDialog) {
        try (Connection conn = getConnection()) {
            return (conn != null && !conn.isClosed());
        } catch (SQLException e) {
            if (showDialog) {
                JOptionPane.showMessageDialog(
                    null,
                    "Cannot connect to MySQL Database!\n\n" +
                    "Please check:\n" +
                    "1. Is WAMP Server started (Green icon)?\n" +
                    "2. Is MySQL service running on port 3306?\n" +
                    "3. Did you import 'database.sql' into phpMyAdmin (database: sunrise_dental_db)?\n\n" +
                    "Technical error: " + e.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
            return false;
        }
    }
}
