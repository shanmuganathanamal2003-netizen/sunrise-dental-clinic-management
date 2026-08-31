package org.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.db.DBConnection;
import org.example.model.Appointment;

/**
 * AppointmentDAO (Data Access Object)
 * Handles all database operations for patient appointments, searches, cancellations, statistics, and billing records.
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
                     "(patient_id, patient_name, patient_age, address, contact_number, dentist_name, assigned_doctor_username, doctor_notes, treatment_type, " +
                     " appointment_date, appointment_time, treatment_cost, consultation_fee, total_bill, status, cancellation_reason) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (appointment.getPatientId() != null && appointment.getPatientId() > 0) {
                stmt.setInt(1, appointment.getPatientId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, appointment.getPatientName().trim());
            stmt.setString(3, appointment.getPatientAge() != null ? appointment.getPatientAge().trim() : "1 Month");
            stmt.setString(4, appointment.getAddress().trim());
            stmt.setString(5, appointment.getContactNumber().trim());
            stmt.setString(6, appointment.getDentistName().trim());
            stmt.setString(7, appointment.getAssignedDoctorUsername());
            stmt.setString(8, appointment.getDoctorNotes());
            stmt.setString(9, appointment.getTreatmentType().trim());
            stmt.setString(10, appointment.getAppointmentDate().trim());
            stmt.setString(11, appointment.getAppointmentTime().trim());
            stmt.setDouble(12, appointment.getTreatmentCost());
            stmt.setDouble(13, appointment.getConsultationFee());
            stmt.setDouble(14, appointment.getTotalBill());
            stmt.setString(15, appointment.getStatus() != null ? appointment.getStatus() : "Scheduled");
            stmt.setString(16, appointment.getCancellationReason());

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
     * Cancels an existing appointment.
     * 
     * @param appointmentNumber ID of the appointment to cancel
     * @param reason Reason for cancellation
     * @return true if updated successfully, false otherwise
     * @throws SQLException on database error
     */
    public boolean cancelAppointment(int appointmentNumber, String reason) throws SQLException {
        String sql = "UPDATE appointments SET status = 'Cancelled', cancellation_reason = ? WHERE appointment_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, (reason != null && !reason.trim().isEmpty()) ? reason.trim() : "Cancelled by clinic staff / patient request");
            stmt.setInt(2, appointmentNumber);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Marks a "Scheduled" appointment as "Confirmed" by the attending doctor.
     * Only doctors should be able to trigger this (enforced in the UI layer).
     */
    public boolean confirmAppointment(int appointmentNumber) throws SQLException {
        String sql = "UPDATE appointments SET status = 'Confirmed' WHERE appointment_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentNumber);
            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    /**
     * Updates an existing appointment's details.
     */
    public boolean updateAppointment(Appointment appt) throws SQLException {
        String sql = "UPDATE appointments SET patient_name = ?, patient_age = ?, address = ?, contact_number = ?, " +
                     "dentist_name = ?, treatment_type = ?, appointment_date = ?, appointment_time = ?, " +
                     "treatment_cost = ?, consultation_fee = ?, total_bill = ?, status = ? WHERE appointment_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appt.getPatientName());
            stmt.setString(2, appt.getPatientAge());
            stmt.setString(3, appt.getAddress());
            stmt.setString(4, appt.getContactNumber());
            stmt.setString(5, appt.getDentistName());
            stmt.setString(6, appt.getTreatmentType());
            stmt.setString(7, appt.getAppointmentDate());
            stmt.setString(8, appt.getAppointmentTime());
            stmt.setDouble(9, appt.getTreatmentCost());
            stmt.setDouble(10, appt.getConsultationFee());
            stmt.setDouble(11, appt.getTotalBill());
            stmt.setString(12, appt.getStatus());
            stmt.setInt(13, appt.getAppointmentNumber());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes an appointment record by its appointment number.
     */
    public boolean deleteAppointment(int appointmentNumber) throws SQLException {
        String sql = "DELETE FROM appointments WHERE appointment_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentNumber);
            return stmt.executeUpdate() > 0;
        }
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
     * Searches appointments by Patient Name (case-insensitive substring match).
     * 
     * @param patientName Patient name or part of name
     * @return List of matching appointments
     * @throws SQLException on database error
     */
    public List<Appointment> searchByPatientName(String patientName) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE LOWER(patient_name) LIKE LOWER(?) ORDER BY appointment_number DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + patientName.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves all appointments for a specific patient (Patient History).
     * 
     * @param patientId Patient ID
     * @return List of appointments for this patient ordered by date/time
     * @throws SQLException on database error
     */
    public List<Appointment> getAppointmentsByPatientId(int patientId) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_date DESC, appointment_number DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves all appointments for a patient by Name and/or Contact.
     */
    public List<Appointment> getAppointmentsByPatientNameOrPhone(String name, String phone) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE LOWER(patient_name) = LOWER(?) OR contact_number = ? ORDER BY appointment_date DESC, appointment_number DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name.trim());
            stmt.setString(2, phone != null ? phone.trim() : "");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        }
        return list;
    }

    /**
     * Versatile multi-field search: Matches against Appointment Number, Patient Name,
     * Contact Number, or Dentist Name.
     * 
     * @param keyword search keyword
     * @return List of matching Appointment objects
     * @throws SQLException on database error
     */
    public List<Appointment> searchAppointments(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAppointments();
        }

        String trimmed = keyword.trim();
        // Support APT-1001 or PAT-101 format
        String cleanNumber = trimmed.replaceAll("(?i)^(APT-|PAT-|#)", "").trim();

        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE " +
                     "CAST(appointment_number AS CHAR) LIKE ? OR " +
                     "CAST(patient_id AS CHAR) LIKE ? OR " +
                     "LOWER(patient_name) LIKE LOWER(?) OR " +
                     "contact_number LIKE ? OR " +
                     "LOWER(dentist_name) LIKE LOWER(?) " +
                     "ORDER BY appointment_number DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + trimmed + "%";
            String numPattern = "%" + cleanNumber + "%";

            stmt.setString(1, numPattern);
            stmt.setString(2, numPattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            stmt.setString(5, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        }
        return list;
    }

    /**
     * Checks if a specific dentist is already booked for an appointment at a given date and time slot.
     * (Double-booking prevention feature - ignores Cancelled appointments)
     * 
     * @param dentistName Name of the doctor
     * @param appointmentDate Date in YYYY-MM-DD format
     * @param appointmentTime Time slot string (e.g. "09:00 AM")
     * @param excludeAppointmentNo Appointment ID to ignore (used when editing, pass -1 for new)
     * @return true if dentist is already booked, false if available
     * @throws SQLException on database error
     */
    public boolean isDentistBooked(String dentistName, String appointmentDate, String appointmentTime, int excludeAppointmentNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE " +
                     "LOWER(dentist_name) = LOWER(?) AND " +
                     "appointment_date = ? AND " +
                     "appointment_time = ? AND " +
                     "status != 'Cancelled' AND " +
                     "appointment_number != ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dentistName.trim());
            stmt.setString(2, appointmentDate.trim());
            stmt.setString(3, appointmentTime.trim());
            stmt.setInt(4, excludeAppointmentNo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
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
     * Retrieves appointments filtered by status (e.g. 'Scheduled', 'Billed', 'Cancelled').
     */
    public List<Appointment> getAppointmentsByStatus(String status) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE status = ? ORDER BY appointment_number DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
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
            System.out.println("Could not calculate next appointment ID preview: " + e.getMessage());
        }
        // Default start number if database table is empty
        return 1001;
    }

    /**
     * Computes live statistics for dashboard cards.
     * 
     * @return Map of metric keys to numeric/double values
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", 0);
        stats.put("todayCount", 0);
        stats.put("scheduledCount", 0);
        stats.put("billedCount", 0);
        stats.put("cancelledCount", 0);
        stats.put("totalRevenue", 0.0);

        String today = LocalDate.now().toString();

        String sql = "SELECT " +
                     "  COUNT(*) AS total_count, " +
                     "  COALESCE(SUM(CASE WHEN appointment_date = ? AND status != 'Cancelled' THEN 1 ELSE 0 END), 0) AS today_count, " +
                     "  COALESCE(SUM(CASE WHEN status = 'Scheduled' THEN 1 ELSE 0 END), 0) AS scheduled_count, " +
                     "  COALESCE(SUM(CASE WHEN status = 'Billed' THEN 1 ELSE 0 END), 0) AS billed_count, " +
                     "  COALESCE(SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END), 0) AS cancelled_count, " +
                     "  COALESCE(SUM(CASE WHEN status = 'Billed' THEN total_bill ELSE 0 END), 0.00) AS total_revenue " +
                     "FROM appointments";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, today);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("totalCount", rs.getInt("total_count"));
                    stats.put("todayCount", rs.getInt("today_count"));
                    stats.put("scheduledCount", rs.getInt("scheduled_count"));
                    stats.put("billedCount", rs.getInt("billed_count"));
                    stats.put("cancelledCount", rs.getInt("cancelled_count"));
                    stats.put("totalRevenue", rs.getDouble("total_revenue"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching dashboard statistics: " + e.getMessage());
        }

        return stats;
    }

    /**
     * Doctor's queue for ONE specific date (used for Today / Tomorrow / any picked date).
     */
    public List<Appointment> getAppointmentsByDoctorAndDate(String username, String date) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE assigned_doctor_username = ? " +
                     "AND appointment_date = ? AND status != 'Cancelled' " +
                     "ORDER BY appointment_time ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            stmt.setString(2, date.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        }
        return list;
    }

    /**
     * Doctor's queue across a date RANGE (e.g. today through next 7 days, or any custom range).
     */
    public List<Appointment> getAppointmentsByDoctorAndDateRange(String username, String fromDate, String toDate) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE assigned_doctor_username = ? " +
                     "AND appointment_date BETWEEN ? AND ? AND status != 'Cancelled' " +
                     "ORDER BY appointment_date ASC, appointment_time ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            stmt.setString(2, fromDate.trim());
            stmt.setString(3, toDate.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves all appointments assigned to a specific doctor.
     */
    public List<Appointment> getAppointmentsByDoctor(String username) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE assigned_doctor_username = ? ORDER BY appointment_date DESC, appointment_time ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        }
        return list;
    }

    /**
     * Updates clinical diagnosis and doctor treatment notes.
     */
    public boolean updateDoctorNotes(int appointmentNumber, String notes) throws SQLException {
        String sql = "UPDATE appointments SET doctor_notes = ? WHERE appointment_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, notes != null ? notes.trim() : null);
            stmt.setInt(2, appointmentNumber);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Helper method to map a ResultSet row to an Appointment object.
     */
    private Appointment mapRowToAppointment(ResultSet rs) throws SQLException {
        Integer pId = null;
        try {
            int val = rs.getInt("patient_id");
            if (!rs.wasNull()) {
                pId = val;
            }
        } catch (SQLException ignore) {}

        String pAge = "1 Month";
        try {
            pAge = rs.getString("patient_age");
            if (pAge == null || pAge.trim().isEmpty()) {
                pAge = "1 Month";
            }
        } catch (SQLException ignore) {}

        String cancReason = null;
        try {
            cancReason = rs.getString("cancellation_reason");
        } catch (SQLException ignore) {}

        String docUser = null;
        try {
            docUser = rs.getString("assigned_doctor_username");
        } catch (SQLException ignore) {}

        String docNotes = null;
        try {
            docNotes = rs.getString("doctor_notes");
        } catch (SQLException ignore) {}

        return new Appointment(
            rs.getInt("appointment_number"),
            pId,
            rs.getString("patient_name"),
            pAge,
            rs.getString("address"),
            rs.getString("contact_number"),
            rs.getString("dentist_name"),
            docUser,
            docNotes,
            rs.getString("treatment_type"),
            rs.getString("appointment_date"),
            rs.getString("appointment_time"),
            rs.getDouble("treatment_cost"),
            rs.getDouble("consultation_fee"),
            rs.getDouble("total_bill"),
            rs.getString("status"),
            cancReason,
            rs.getTimestamp("created_at")
        );
    }
}

