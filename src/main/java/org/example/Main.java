package org.example;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.example.db.DBConnection;
import org.example.view.LoginView;

/**
 * ====================================================================
 * SUNRISE DENTAL CLINIC - APPOINTMENT & PATIENT MANAGEMENT SYSTEM
 * ====================================================================
 */
public class Main {

    public static void main(String[] args) {
        // Step 1: Set native platform look and feel for modern Swing appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Could not apply system look and feel: " + e.getMessage());
        }

        // Step 2: Test database connection in background
        new Thread(() -> {
            boolean connected = DBConnection.testConnection(false);
            if (connected) {
                System.out.println("[Database] Successfully connected to MySQL database (sunrise_dental_db).");
            } else {
                System.out.println("[Database] Notice: MySQL database is currently unreachable. Please ensure WAMP Server is running on port 3306.");
            }
        }).start();

        // Step 3: Launch Login window safely on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });


    }
}
