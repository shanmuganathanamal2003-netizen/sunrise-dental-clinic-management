package org.example.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;

/**
 * DBConnection - Resilient Database Connection Manager & Auto-Initializer
 * 
 * Handles connecting to MySQL database via JDBC.
 * Automatically tries standard WAMP / XAMPP passwords ("" or "root" or "admin").
 * Creates the database and applies all schema updates (tables, columns, indexes, initial data) seamlessly.
 */
public class DBConnection {

    private static final String HOST = "localhost:3306";
    private static final String DB_NAME = "sunrise_dental_db";
    private static final String PARAMS = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    
    private static final String BASE_URL = "jdbc:mysql://" + HOST + "/" + PARAMS;
    private static final String DB_URL = "jdbc:mysql://" + HOST + "/" + DB_NAME + PARAMS;
    
    private static final String USER = "root";
    private static final String[] CANDIDATE_PASSWORDS = {"", "root", "admin", "1234", "root123", "password"};
    private static String verifiedPassword = "";
    
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static boolean isInitialized = false;

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Ensure mysql-connector-j is included: " + e.getMessage());
        }
    }

    /**
     * Obtains a new database connection.
     * Automatically ensures database and tables exist before returning the connection.
     */
    public static Connection getConnection() throws SQLException {
        if (!isInitialized) {
            initializeDatabaseIfMissing();
        }
        
        // Try connecting with verified password first
        try {
            return DriverManager.getConnection(DB_URL, USER, verifiedPassword);
        } catch (SQLException e) {
            // If connection failed, probe candidate passwords
            for (String pwd : CANDIDATE_PASSWORDS) {
                try {
                    Connection conn = DriverManager.getConnection(DB_URL, USER, pwd);
                    verifiedPassword = pwd;
                    return conn;
                } catch (SQLException ignore) {}
            }
            throw e;
        }
    }

    /**
     * Obtains a raw connection to the MySQL server without selecting a specific database.
     */
    private static Connection getRootConnection() throws SQLException {
        for (String pwd : CANDIDATE_PASSWORDS) {
            try {
                Connection conn = DriverManager.getConnection(BASE_URL, USER, pwd);
                verifiedPassword = pwd;
                return conn;
            } catch (SQLException ignore) {}
        }
        return DriverManager.getConnection(BASE_URL, USER, "");
    }

    /**
     * Automatically initializes database and tables with sample data and schema updates.
     */
    public static synchronized void initializeDatabaseIfMissing() {
        if (isInitialized) return;

        // 1. Ensure database exists
        try (Connection rootConn = getRootConnection();
             Statement stmt = rootConn.createStatement()) {
            
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        } catch (SQLException e) {
            System.out.println("Notice on database creation check: " + e.getMessage());
        }

        // 2. Ensure tables and default records exist
        try (Connection dbConn = DriverManager.getConnection(DB_URL, USER, verifiedPassword);
             Statement stmt = dbConn.createStatement()) {

            // Create users table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `users` (" +
                "  `user_id` INT AUTO_INCREMENT PRIMARY KEY," +
                "  `username` VARCHAR(50) NOT NULL UNIQUE," +
                "  `password` VARCHAR(100) NOT NULL," +
                "  `full_name` VARCHAR(100) NOT NULL," +
                "  `role` VARCHAR(30) NOT NULL DEFAULT 'Receptionist'," +
                "  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
            );

            // Create patients table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `patients` (" +
                "  `patient_id` INT AUTO_INCREMENT PRIMARY KEY," +
                "  `patient_name` VARCHAR(100) NOT NULL," +
                "  `age` VARCHAR(30) NOT NULL DEFAULT '1 Month'," +
                "  `gender` VARCHAR(20) NOT NULL DEFAULT 'Not Specified'," +
                "  `contact_number` VARCHAR(20) NOT NULL," +
                "  `address` VARCHAR(255) NOT NULL," +
                "  `medical_history` TEXT," +
                "  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4;"
            );

            // Create treatments table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `treatments` (" +
                "  `treatment_id` INT AUTO_INCREMENT PRIMARY KEY," +
                "  `treatment_name` VARCHAR(100) NOT NULL UNIQUE," +
                "  `treatment_cost` DECIMAL(10, 2) NOT NULL DEFAULT 0.00," +
                "  `consultation_fee` DECIMAL(10, 2) NOT NULL DEFAULT 1500.00" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
            );

            // Create appointments table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `appointments` (" +
                "  `appointment_number` INT AUTO_INCREMENT PRIMARY KEY," +
                "  `patient_id` INT DEFAULT NULL," +
                "  `patient_name` VARCHAR(100) NOT NULL," +
                "  `patient_age` VARCHAR(30) NOT NULL DEFAULT '1 Month'," +
                "  `address` VARCHAR(255) NOT NULL," +
                "  `contact_number` VARCHAR(20) NOT NULL," +
                "  `dentist_name` VARCHAR(100) NOT NULL," +
                "  `assigned_doctor_username` VARCHAR(50) DEFAULT NULL," +
                "  `doctor_notes` TEXT DEFAULT NULL," +
                "  `treatment_type` VARCHAR(100) NOT NULL," +
                "  `appointment_date` DATE NOT NULL," +
                "  `appointment_time` VARCHAR(20) NOT NULL," +
                "  `treatment_cost` DECIMAL(10, 2) NOT NULL DEFAULT 0.00," +
                "  `consultation_fee` DECIMAL(10, 2) NOT NULL DEFAULT 1500.00," +
                "  `total_bill` DECIMAL(10, 2) NOT NULL DEFAULT 0.00," +
                "  `status` VARCHAR(20) NOT NULL DEFAULT 'Scheduled'," +
                "  `cancellation_reason` VARCHAR(255) DEFAULT NULL," +
                "  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8mb4;"
            );

            // Safely inspect and apply column migrations on appointments table
            ensureColumnsExist(dbConn);

            // Seed default users if empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `users`")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.executeUpdate(
                        "INSERT INTO `users` (`username`, `password`, `full_name`, `role`) VALUES " +
                        "('admin', 'admin123', 'Administrator - Dr. Samantha Perera', 'Admin')," +
                        "('staff', 'staff123', 'Receptionist - Kasuni Silva', 'Receptionist')," +
                        "('doctor1', 'doctor123', 'Dr. Kasun Fernando', 'Doctor')," +
                        "('doctor2', 'doctor123', 'Dr. Nihal Silva', 'Doctor');"
                    );
                }
            }

            // Seed default patients if empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `patients`")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.executeUpdate(
                        "INSERT INTO `patients` (`patient_id`, `patient_name`, `age`, `gender`, `contact_number`, `address`, `medical_history`) VALUES " +
                        "(101, 'Kamal Perera', '35 Years', 'Male', '0771234567', 'No. 45, Galle Road, Colombo 03', 'No known allergies. Regular dental checkup patient.')," +
                        "(102, 'Nimali Fernando', '28 Years', 'Female', '0719876543', 'No. 12, Kandy Road, Kelaniya', 'Mild gingivitis reported in 2025.')," +
                        "(103, 'Sunil Wickramasinghe', '52 Years', 'Male', '0765551234', 'No. 88, High Level Road, Nugegoda', 'Hypertension under medication.')," +
                        "(104, 'Baby Arya Senanayake', '6 Months', 'Female', '0774443322', 'No. 19, Havelock Town, Colombo 05', 'Pediatric infant dental assessment.');"
                    );
                }
            }

            // Seed default treatments if empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `treatments`")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.executeUpdate(
                        "INSERT INTO `treatments` (`treatment_name`, `treatment_cost`, `consultation_fee`) VALUES " +
                        "('General Dental Consultation', 1000.00, 1500.00)," +
                        "('Teeth Cleaning & Scaling', 3500.00, 1500.00)," +
                        "('Tooth Filling (Composite)', 4500.00, 1500.00)," +
                        "('Tooth Extraction', 4000.00, 1500.00)," +
                        "('Root Canal Treatment', 15000.00, 1500.00)," +
                        "('Teeth Whitening', 12000.00, 1500.00)," +
                        "('Orthodontic Consultation / Braces', 25000.00, 2000.00)," +
                        "('Dental Crown / Bridge', 18000.00, 2000.00);"
                    );
                }
            }

            // Seed sample appointments if empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `appointments`")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.executeUpdate(
                        "INSERT INTO `appointments` (`appointment_number`, `patient_id`, `patient_name`, `patient_age`, `address`, `contact_number`, `dentist_name`, `treatment_type`, `appointment_date`, `appointment_time`, `treatment_cost`, `consultation_fee`, `total_bill`, `status`, `cancellation_reason`) VALUES " +
                        "(1001, 101, 'Kamal Perera', '35 Years', 'No. 45, Galle Road, Colombo 03', '0771234567', 'Dr. Samantha Perera (Senior Dental Surgeon)', 'Teeth Cleaning & Scaling', '2026-08-20', '09:00 AM', 3500.00, 1500.00, 5000.00, 'Scheduled', NULL)," +
                        "(1002, 102, 'Nimali Fernando', '28 Years', 'No. 12, Kandy Road, Kelaniya', '0719876543', 'Dr. Nihal Silva (Orthodontist)', 'Tooth Filling (Composite)', '2026-08-21', '10:30 AM', 4500.00, 1500.00, 6000.00, 'Scheduled', NULL)," +
                        "(1003, 103, 'Sunil Wickramasinghe', '52 Years', 'No. 88, High Level Road, Nugegoda', '0765551234', 'Dr. Anoma Wickramasinghe (General Dental Practitioner)', 'Root Canal Treatment', '2026-08-22', '02:00 PM', 15000.00, 1500.00, 16500.00, 'Scheduled', NULL)," +
                        "(1004, 101, 'Kamal Perera', '35 Years', 'No. 45, Galle Road, Colombo 03', '0771234567', 'Dr. Kasun Fernando (Endodontist)', 'General Dental Consultation', '2026-08-28', '11:00 AM', 1000.00, 1500.00, 2500.00, 'Scheduled', NULL);"
                    );
                }
            }

            // Sync any existing appointment records that lack patient_id to registered patients
            try {
                stmt.executeUpdate(
                    "UPDATE appointments a " +
                    "JOIN patients p ON LOWER(a.patient_name) = LOWER(p.patient_name) " +
                    "SET a.patient_id = p.patient_id " +
                    "WHERE a.patient_id IS NULL;"
                );
            } catch (SQLException ignore) {}

            isInitialized = true;
            System.out.println("[Database] Database and tables verified/initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing database schema: " + e.getMessage());
        }
    }

    /**
     * Inspects existing columns in appointments table and adds any missing columns cleanly.
     */
    private static void ensureColumnsExist(Connection conn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            Set<String> existingColumns = new HashSet<>();
            try (ResultSet rs = meta.getColumns(null, null, "appointments", null)) {
                while (rs.next()) {
                    existingColumns.add(rs.getString("COLUMN_NAME").toLowerCase());
                }
            }

            try (Statement stmt = conn.createStatement()) {
                if (!existingColumns.contains("patient_id")) {
                    stmt.executeUpdate("ALTER TABLE `appointments` ADD COLUMN `patient_id` INT DEFAULT NULL");
                }
                if (!existingColumns.contains("patient_age")) {
                    stmt.executeUpdate("ALTER TABLE `appointments` ADD COLUMN `patient_age` VARCHAR(30) NOT NULL DEFAULT '1 Month'");
                }
                if (!existingColumns.contains("cancellation_reason")) {
                    stmt.executeUpdate("ALTER TABLE `appointments` ADD COLUMN `cancellation_reason` VARCHAR(255) DEFAULT NULL");
                }
                if (!existingColumns.contains("assigned_doctor_username")) {
                    stmt.executeUpdate("ALTER TABLE `appointments` ADD COLUMN `assigned_doctor_username` VARCHAR(50) DEFAULT NULL");
                }
                if (!existingColumns.contains("doctor_notes")) {
                    stmt.executeUpdate("ALTER TABLE `appointments` ADD COLUMN `doctor_notes` TEXT DEFAULT NULL");
                }
            }
        } catch (SQLException e) {
            System.out.println("Notice on columns verification: " + e.getMessage());
        }
    }

    /**
     * Helper method to test if database connection is active.
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
                    "2. Is MySQL service running on port 3306?\n\n" +
                    "Technical error: " + e.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
            return false;
        }
    }
}

