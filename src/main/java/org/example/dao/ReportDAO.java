package org.example.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.db.DBConnection;

/**
 * ReportDAO (Data Access Object)
 * Generates decision-making analytics reports for clinic management.
 */
public class ReportDAO {

    /**
     * Retrieves doctor workload summary (number of appointments and revenue per dentist).
     */
    public List<Map<String, Object>> getDoctorWorkloadReport() {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT dentist_name, " +
                     "       COUNT(*) AS total_appointments, " +
                     "       COALESCE(SUM(CASE WHEN status = 'Billed' THEN 1 ELSE 0 END), 0) AS completed_appointments, " +
                     "       COALESCE(SUM(CASE WHEN status = 'Billed' THEN total_bill ELSE 0 END), 0.00) AS total_revenue " +
                     "FROM appointments " +
                     "GROUP BY dentist_name " +
                     "ORDER BY total_appointments DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("dentistName", rs.getString("dentist_name") != null ? rs.getString("dentist_name") : "Unassigned");
                row.put("totalAppointments", rs.getInt("total_appointments"));
                row.put("completedAppointments", rs.getInt("completed_appointments"));
                row.put("totalRevenue", rs.getDouble("total_revenue"));
                report.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error generating Doctor Workload Report: " + e.getMessage());
        }
        return report;
    }

    /**
     * Retrieves treatment popularity and procedure revenue summary.
     */
    public List<Map<String, Object>> getTreatmentSummaryReport() {
        List<Map<String, Object>> report = new ArrayList<>();
        String sql = "SELECT treatment_type, " +
                     "       COUNT(*) AS appointment_count, " +
                     "       COALESCE(SUM(treatment_cost), 0.00) AS total_treatment_cost, " +
                     "       COALESCE(SUM(consultation_fee), 0.00) AS total_consult_fee, " +
                     "       COALESCE(SUM(total_bill), 0.00) AS gross_total " +
                     "FROM appointments " +
                     "GROUP BY treatment_type " +
                     "ORDER BY appointment_count DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("treatmentType", rs.getString("treatment_type") != null ? rs.getString("treatment_type") : "General");
                row.put("appointmentCount", rs.getInt("appointment_count"));
                row.put("totalTreatmentCost", rs.getDouble("total_treatment_cost"));
                row.put("totalConsultFee", rs.getDouble("total_consult_fee"));
                row.put("grossTotal", rs.getDouble("gross_total"));
                report.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error generating Treatment Summary Report: " + e.getMessage());
        }
        return report;
    }
}

