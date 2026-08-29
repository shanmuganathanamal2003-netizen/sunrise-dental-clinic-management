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
        String patientAge = (appt.getPatientAge() != null) ? appt.getPatientAge() : "1 Month";

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================\n");
        sb.append("                SUNRISE DENTAL CLINIC                    \n");
        sb.append("         No. 45, Galle Road, Colombo 03, Sri Lanka        \n");
        sb.append("                Tel: +94 11 234 5678                     \n");
        sb.append("=========================================================\n");
        sb.append("               OFFICIAL PATIENT RECEIPT                  \n");
        sb.append("---------------------------------------------------------\n");
        sb.append(" Receipt Number  : REC-" + appt.getAppointmentNumber() + "\n");
        sb.append(" Appointment No  : " + appt.getAppointmentNumber() + "\n");
        sb.append(" Issued Date     : " + printTimestamp + "\n");
        sb.append("---------------------------------------------------------\n");
        sb.append(" Patient Name    : " + appt.getPatientName() + " (Age: " + patientAge + ")\n");
        sb.append(" Contact Number  : " + appt.getContactNumber() + "\n");
        sb.append(" Address         : " + appt.getAddress() + "\n");
        sb.append(" Assigned Doctor : " + appt.getDentistName() + "\n");
        sb.append(" Appt Date/Time  : " + appt.getAppointmentDate() + " (" + appt.getAppointmentTime() + ")\n");
        sb.append("---------------------------------------------------------\n");
        sb.append(" ITEM / PROCEDURE DESCRIPTION                    AMOUNT (LKR)\n");
        sb.append("---------------------------------------------------------\n");
        sb.append(" 1. Consultation Fee                             LKR " + consultationFee + "\n");
        sb.append(" 2. " + truncate(appt.getTreatmentType(), 30) + "                  LKR " + treatmentCost + "\n");
        sb.append("---------------------------------------------------------\n");
        sb.append(" GRAND TOTAL BILL (LKR)                          LKR " + total + "\n");
        sb.append("=========================================================\n");
        sb.append(" Payment Status  : PAID IN FULL\n");
        sb.append(" Billed By       : " + issuer + "\n\n");
        sb.append("         Thank you for choosing Sunrise Dental!          \n");
        sb.append("     For emergency inquiries, call +94 11 234 5678       \n");
        sb.append("=========================================================\n");

        return sb.toString();
    }

    /**
     * Calculates total bill for a given Appointment object.
     */
    public double calculateTotalBill(Appointment appt) {
        if (appt == null) return 0.0;
        return calculateTotal(appt.getConsultationFee(), appt.getTreatmentCost());
    }

    /**
     * Prints receipt details directly using System.out.println.
     */
    public void printReceiptToConsole(Appointment appt, double consultationFee, double treatmentCost, User issuedBy) {
        String receipt = generateReceipt(appt, consultationFee, treatmentCost, issuedBy);
        System.out.println(receipt);
    }

    /**
     * Displays summary of billing computation using System.out.println.
     */
    public void displayBillDetails(Appointment appt) {
        if (appt == null) return;
        System.out.println("====== BILL DETAILS ======");
        System.out.println("Appointment Number: " + appt.getAppointmentNumber());
        System.out.println("Patient Name      : " + appt.getPatientName());
        System.out.println("Consultation Fee  : LKR " + appt.getConsultationFee());
        System.out.println("Treatment Cost    : LKR " + appt.getTreatmentCost());
        System.out.println("Total Amount Due  : LKR " + calculateTotalBill(appt));
        System.out.println("Billing Status    : " + appt.getStatus());
        System.out.println("==========================");
    }

    private String truncate(String val, int maxLen) {
        if (val == null) return "";
        if (val.length() <= maxLen) return val;
        return val.substring(0, maxLen - 3) + "...";
    }
}
