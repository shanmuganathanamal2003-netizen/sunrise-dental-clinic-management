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
            System.out.println("Error generating Doctor Workload Report: " + e.getMessage());
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
            System.out.println("Error generating Treatment Summary Report: " + e.getMessage());
        }
        return report;
    }

    /**
     * Retrieves overall revenue and operations summary report.
     */
    public Map<String, Object> getRevenueSummaryReport() {
        Map<String, Object> summary = new HashMap<>();
        String sql = "SELECT " +
                     "  COUNT(*) AS total_appointments, " +
                     "  COALESCE(SUM(CASE WHEN status = 'Scheduled' THEN 1 ELSE 0 END), 0) AS scheduled_appointments, " +
                     "  COALESCE(SUM(CASE WHEN status = 'Billed' THEN 1 ELSE 0 END), 0) AS completed_appointments, " +
                     "  COALESCE(SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END), 0) AS cancelled_appointments, " +
                     "  COALESCE(SUM(CASE WHEN status = 'Billed' THEN total_bill ELSE 0 END), 0.00) AS total_revenue, " +
                     "  COALESCE(SUM(CASE WHEN status = 'Billed' THEN consultation_fee ELSE 0 END), 0.00) AS total_consult_fee, " +
                     "  COALESCE(SUM(CASE WHEN status = 'Billed' THEN treatment_cost ELSE 0 END), 0.00) AS total_treatment_cost " +
                     "FROM appointments";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                summary.put("totalAppointments", rs.getInt("total_appointments"));
                summary.put("scheduledAppointments", rs.getInt("scheduled_appointments"));
                summary.put("completedAppointments", rs.getInt("completed_appointments"));
                summary.put("cancelledAppointments", rs.getInt("cancelled_appointments"));
                summary.put("totalRevenue", rs.getDouble("total_revenue"));
                summary.put("totalConsultFee", rs.getDouble("total_consult_fee"));
                summary.put("totalTreatmentCost", rs.getDouble("total_treatment_cost"));
            }
        } catch (SQLException e) {
            System.out.println("Error generating Revenue Summary Report: " + e.getMessage());
        }
        return summary;
    }
}

