package org.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.example.db.DBConnection;
import org.example.model.Patient;

/**
 * PatientDAO (Data Access Object)
 * Handles database operations for patient profile records and history lookups.
 */

public class PatientDAO {

    /**
     * Creates and registers a new patient in the database.
     * 
     * @param patient Patient object
     * @return Generated patient_id
     * @throws SQLException on database error
     */
    public int createPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO patients (patient_name, age, gender, contact_number, email, address, medical_history) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, patient.getPatientName().trim());
            stmt.setString(2, patient.getAge() != null ? patient.getAge().trim() : "1 Month");
            stmt.setString(3, patient.getGender() != null ? patient.getGender().trim() : "Not Specified");
            stmt.setString(4, patient.getContactNumber().trim());
            stmt.setString(5, patient.getEmail() != null ? patient.getEmail().trim() : "");
            stmt.setString(6, patient.getAddress().trim());
            stmt.setString(7, patient.getMedicalHistory() != null ? patient.getMedicalHistory().trim() : "");

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        patient.setPatientId(id);
                        return id;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Retrieves all registered patients sorted alphabetically by name.
     */
    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY patient_name ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToPatient(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching patients: " + e.getMessage());
        }
        return list;
    }

    /**
     * Retrieves a patient by their unique ID.
     */
    public Patient getPatientById(int patientId) throws SQLException {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPatient(rs);
                }
            }
        }
        return null;
    }

    /**
     * Searches patients by Name, Phone Number, or Patient ID.
     */
    public List<Patient> searchPatients(String keyword) throws SQLException {
        List<Patient> list = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPatients();
        }

        String trimmed = keyword.trim();
        String cleanNum = trimmed.replaceAll("(?i)^(PAT-|#)", "").trim();

        String pattern = "%" + trimmed + "%";
        String numPattern = "%" + cleanNum + "%";

        String sql = "SELECT * FROM patients WHERE " +
                     "CAST(patient_id AS CHAR) LIKE ? OR " +
                     "LOWER(patient_name) LIKE LOWER(?) OR " +
                     "contact_number LIKE ? " +
                     "ORDER BY patient_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numPattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToPatient(rs));
                }
            }
        }
        return list;
    }

    /**
     * Finds an existing patient by name and contact or creates a new one.
     */
    public int findOrCreatePatient(String name, String age, String gender, String contact, String address, String medicalHistory) throws SQLException {
        String findSql = "SELECT patient_id FROM patients WHERE LOWER(patient_name) = LOWER(?) AND contact_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(findSql)) {

            stmt.setString(1, name.trim());
            stmt.setString(2, contact.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("patient_id");
                }
            }
        }

        Patient newPatient = new Patient(name, age, gender, contact, address, medicalHistory);
        return createPatient(newPatient);
    }

    /**
     * Updates an existing patient profile.
     */
    public boolean updatePatient(Patient patient) throws SQLException {
        String sql = "UPDATE patients SET patient_name = ?, age = ?, gender = ?, contact_number = ?, email = ?, address = ?, medical_history = ? WHERE patient_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getPatientName().trim());
            stmt.setString(2, patient.getAge().trim());
            stmt.setString(3, patient.getGender().trim());
            stmt.setString(4, patient.getContactNumber().trim());
            stmt.setString(5, patient.getEmail() != null ? patient.getEmail().trim() : "");
            stmt.setString(6, patient.getAddress().trim());
            stmt.setString(7, patient.getMedicalHistory() != null ? patient.getMedicalHistory().trim() : "");
            stmt.setInt(8, patient.getPatientId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves a patient by exact or first matching name.
     */
    public Patient getPatientByName(String patientName) throws SQLException {
        String sql = "SELECT * FROM patients WHERE LOWER(patient_name) = LOWER(?) LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patientName.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPatient(rs);
                }
            }
        }
        return null;
    }

    /**
     * Deletes a patient profile by patient ID.
     */
    public boolean deletePatient(int patientId) throws SQLException {
        String sql = "DELETE FROM patients WHERE patient_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Patient mapRowToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient(
                rs.getInt("patient_id"),
                rs.getString("patient_name"),
                rs.getString("age"),
                rs.getString("gender"),
                rs.getString("contact_number"),
                rs.getString("address"),
                rs.getString("medical_history"),
                rs.getTimestamp("created_at")
        );
        patient.setEmail(rs.getString("email"));
        return patient;
    }
}
