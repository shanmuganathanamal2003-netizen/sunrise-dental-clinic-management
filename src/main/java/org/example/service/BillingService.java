package org.example.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.example.dao.AppointmentDAO;
import org.example.model.Appointment;
import org.example.model.User;

/**
 * BillingService - Business Logic Layer for Billing & Invoice Generation
 */
public class BillingService {

    private final AppointmentDAO appointmentDAO;

    public BillingService() {
        this.appointmentDAO = new AppointmentDAO();
    }

    /**
     * Calculates the grand total bill.
     */
    public double calculateTotal(double consultationFee, double treatmentCost) {
        if (consultationFee < 0 || treatmentCost < 0) {
            throw new IllegalArgumentException("Fees cannot be negative values.");
        }
        return consultationFee + treatmentCost;
    }

    /**
     * Saves and updates the billing calculation to the database.
     */
    public boolean processBill(int appointmentNumber, double treatmentCost, double consultationFee) throws SQLException {
        double total = calculateTotal(consultationFee, treatmentCost);
        return appointmentDAO.updateAppointmentBilling(appointmentNumber, treatmentCost, consultationFee, total);
    }

    /**
     * Generates a beautifully formatted dental clinic printable invoice.
     */
    public String generateReceipt(Appointment appt, double consultationFee, double treatmentCost, User issuedBy) {
        double total = calculateTotal(consultationFee, treatmentCost);
        String issuer = (issuedBy != null) ? issuedBy.getFullName() : "Authorized Staff";
        String printTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================\n");
        sb.append("                SUNRISE DENTAL CLINIC                    \n");
        sb.append("         No. 45, Galle Road, Colombo 03, Sri Lanka        \n");
        sb.append("                Tel: +94 11 234 5678                     \n");
        sb.append("=========================================================\n");
        sb.append("               OFFICIAL PATIENT RECEIPT                  \n");
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format(" Receipt Number  : REC-%04d\n", appt.getAppointmentNumber()));
        sb.append(String.format(" Appointment No : %d\n", appt.getAppointmentNumber()));
        sb.append(String.format(" Issued Date     : %s\n", printTimestamp));
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format(" Patient Name    : %s (Age: %s)\n", appt.getPatientName(), (appt.getPatientAge() != null ? appt.getPatientAge() : "1 Month")));
        sb.append(String.format(" Contact Number  : %s\n", appt.getContactNumber()));
        sb.append(String.format(" Address         : %s\n", appt.getAddress()));
        sb.append(String.format(" Assigned Doctor : %s\n", appt.getDentistName()));
        sb.append(String.format(" Appt Date/Time  : %s (%s)\n", appt.getAppointmentDate(), appt.getAppointmentTime()));
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format(" %-35s %18s\n", "ITEM / PROCEDURE DESCRIPTION", "AMOUNT (LKR)"));
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format(" 1. Consultation Fee               %18.2f\n", consultationFee));
        sb.append(String.format(" 2. %-30s %18.2f\n", truncate(appt.getTreatmentType(), 30), treatmentCost));
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format(" GRAND TOTAL BILL (LKR)           %18.2f\n", total));
        sb.append("=========================================================\n");
        sb.append(" Payment Status : PAID IN FULL\n");
        sb.append(" Billed By      : " + issuer + "\n\n");
        sb.append("         Thank you for choosing Sunrise Dental!          \n");
        sb.append("     For emergency inquiries, call +94 11 234 5678       \n");
        sb.append("=========================================================\n");

        return sb.toString();
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        if (val.length() <= maxLen) return val;
        return val.substring(0, maxLen - 3) + "...";
    }
}
