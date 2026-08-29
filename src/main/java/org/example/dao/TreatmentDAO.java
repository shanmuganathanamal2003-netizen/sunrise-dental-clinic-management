package org.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.example.db.DBConnection;
import org.example.model.Treatment;

/**
 * TreatmentDAO (Data Access Object)
 * Handles database operations related to standard dental treatments and prices.
 */
public class TreatmentDAO {

    /**
     * Retrieves all available treatments from the database.
     * If database query encounters an issue or table is empty, returns standard default dental treatments.
     * 
     * @return List of Treatment objects
     */

    public List<Treatment> getAllTreatments() {
        List<Treatment> treatments = new ArrayList<>();
        String sql = "SELECT treatment_id, treatment_name, treatment_cost, consultation_fee FROM treatments ORDER BY treatment_id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                treatments.add(new Treatment(
                    rs.getInt("treatment_id"),
                    rs.getString("treatment_name"),
                    rs.getDouble("treatment_cost"),
                    rs.getDouble("consultation_fee")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Could not load treatments from DB, using defaults: " + e.getMessage());
        }

        // Fallback default list if DB had 0 records or wasn't populated yet
        if (treatments.isEmpty()) {
            treatments.add(new Treatment(1, "General Dental Consultation", 1000.00, 1500.00));
            treatments.add(new Treatment(2, "Teeth Cleaning & Scaling", 3500.00, 1500.00));
            treatments.add(new Treatment(3, "Tooth Filling (Composite)", 4500.00, 1500.00));
            treatments.add(new Treatment(4, "Tooth Extraction", 4000.00, 1500.00));
            treatments.add(new Treatment(5, "Root Canal Treatment", 15000.00, 1500.00));
            treatments.add(new Treatment(6, "Teeth Whitening", 12000.00, 1500.00));
            treatments.add(new Treatment(7, "Orthodontic Consultation / Braces", 25000.00, 2000.00));
        }

        return treatments;
    }

    /**
     * Finds a treatment by its name.
     * 
     * @param name Name of the treatment
     * @return Treatment object or null if not found
     */
    public Treatment getTreatmentByName(String name) {
        String sql = "SELECT treatment_id, treatment_name, treatment_cost, consultation_fee FROM treatments WHERE treatment_name = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Treatment(
                        rs.getInt("treatment_id"),
                        rs.getString("treatment_name"),
                        rs.getDouble("treatment_cost"),
                        rs.getDouble("consultation_fee")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching treatment by name: " + e.getMessage());
        }
        return null;
    }

    /**
     * Finds a treatment by its ID.
     */
    public Treatment getTreatmentById(int treatmentId) {
        String sql = "SELECT treatment_id, treatment_name, treatment_cost, consultation_fee FROM treatments WHERE treatment_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, treatmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Treatment(
                        rs.getInt("treatment_id"),
                        rs.getString("treatment_name"),
                        rs.getDouble("treatment_cost"),
                        rs.getDouble("consultation_fee")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching treatment by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Adds a new treatment procedure into the database.
     */
    public boolean addTreatment(Treatment treatment) throws SQLException {
        String sql = "INSERT INTO treatments (treatment_name, treatment_cost, consultation_fee) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, treatment.getTreatmentName());
            stmt.setDouble(2, treatment.getTreatmentCost());
            stmt.setDouble(3, treatment.getConsultationFee());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Updates an existing treatment procedure.
     */
    public boolean updateTreatment(Treatment treatment) throws SQLException {
        String sql = "UPDATE treatments SET treatment_name = ?, treatment_cost = ?, consultation_fee = ? WHERE treatment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, treatment.getTreatmentName());
            stmt.setDouble(2, treatment.getTreatmentCost());
            stmt.setDouble(3, treatment.getConsultationFee());
            stmt.setInt(4, treatment.getTreatmentId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a treatment procedure by ID.
     */
    public boolean deleteTreatment(int treatmentId) throws SQLException {
        String sql = "DELETE FROM treatments WHERE treatment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, treatmentId);
            return stmt.executeUpdate() > 0;
        }
    }
}
