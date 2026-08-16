package org.example.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.example.db.DBConnection;
import org.example.model.Appointment;

/**
 * AppointmentDAO (Data Access Object)
 * Handles all database operations for patient appointments and billing records.
 */
public class AppointmentDAO {

    /**
     * Registers a new appointment in the database.
     * Automatically retrieves and sets the generated appointment_number.
     * 
     * @param appointment The appointment details to save
     * @return Generated appointment number (ID)
     * @throws SQLException on database error
     */
    public int createAppointment(Appointment appointment) throws SQLException {
        String sql = "INSERT INTO appointments " +
                     "(patient_name, address, contact_number, dentist_name, treatment_type, " +
                     " appointment_date, appointment_time, treatment_cost, consultation_fee, total_bill, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, appointment.getPatientName().trim());
            stmt.setString(2, appointment.getAddress().trim());
            stmt.setString(3, appointment.getContactNumber().trim());
            stmt.setString(4, appointment.getDentistName().trim());
            stmt.setString(5, appointment.getTreatmentType().trim());
            stmt.setString(6, appointment.getAppointmentDate().trim());
            stmt.setString(7, appointment.getAppointmentTime().trim());
            stmt.setDouble(8, appointment.getTreatmentCost());
            stmt.setDouble(9, appointment.getConsultationFee());
            stmt.setDouble(10, appointment.getTotalBill());
            stmt.setString(11, appointment.getStatus() != null ? appointment.getStatus() : "Scheduled");

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        appointment.setAppointmentNumber(generatedId);
                        return generatedId;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Searches and retrieves an appointment by its unique appointment number.
     * 
     * @param appointmentNumber ID of the appointment
     * @return Appointment object if found, null otherwise
     * @throws SQLException on database error
     */
    public Appointment getAppointmentByNumber(int appointmentNumber) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE appointment_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAppointment(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all appointment records, sorted by most recent first.
     * 
     * @return List of Appointment objects
     * @throws SQLException on database error
     */
    public List<Appointment> getAllAppointments() throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_number DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToAppointment(rs));
            }
        }
        return list;
    }

    /**
     * Updates billing information (treatment cost, consultation fee, total bill)
     * and marks appointment status as 'Billed'.
     * 
     * @param appointmentNumber ID of the appointment
     * @param treatmentCost Procedure cost
     * @param consultationFee Doctor consultation fee
     * @param totalBill Total calculated bill amount
     * @return true if update succeeded, false otherwise
     * @throws SQLException on database error
     */
    public boolean updateAppointmentBilling(int appointmentNumber, double treatmentCost, double consultationFee, double totalBill) throws SQLException {
        String sql = "UPDATE appointments SET treatment_cost = ?, consultation_fee = ?, total_bill = ?, status = 'Billed' WHERE appointment_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, treatmentCost);
            stmt.setDouble(2, consultationFee);
            stmt.setDouble(3, totalBill);
            stmt.setInt(4, appointmentNumber);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Returns a preview of what the next generated appointment number will be.
     * 
     * @return Next appointment number preview
     */
    public int getNextAppointmentNumberPreview() {
        String sql = "SELECT MAX(appointment_number) AS max_id FROM appointments";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                if (maxId > 0) {
                    return maxId + 1;
                }
            }
        } catch (SQLException e) {
            System.err.println("Could not calculate next appointment ID preview: " + e.getMessage());
        }
        // Default start number if database table is empty
        return 1001;
    }

    /**
     * Helper method to map a ResultSet row to an Appointment object.
     */
    private Appointment mapRowToAppointment(ResultSet rs) throws SQLException {
        return new Appointment(
            rs.getInt("appointment_number"),
            rs.getString("patient_name"),
            rs.getString("address"),
            rs.getString("contact_number"),
            rs.getString("dentist_name"),
            rs.getString("treatment_type"),
            rs.getString("appointment_date"),
            rs.getString("appointment_time"),
            rs.getDouble("treatment_cost"),
            rs.getDouble("consultation_fee"),
            rs.getDouble("total_bill"),
            rs.getString("status"),
            rs.getTimestamp("created_at")
        );
    }
}
