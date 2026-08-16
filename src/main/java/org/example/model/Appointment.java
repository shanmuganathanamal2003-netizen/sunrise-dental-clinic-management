package org.example.model;

import java.sql.Timestamp;

/**
 * Appointment Model
 * Represents a dental patient appointment booking and related billing status.
 */
public class Appointment {
    private int appointmentNumber;
    private String patientName;
    private String address;
    private String contactNumber;
    private String dentistName;
    private String treatmentType;
    private String appointmentDate; // Format: YYYY-MM-DD
    private String appointmentTime; // e.g. 09:00 AM
    private double treatmentCost;
    private double consultationFee;
    private double totalBill;
    private String status;          // e.g. "Scheduled", "Completed", "Billed"
    private Timestamp createdAt;

    // Default Constructor
    public Appointment() {
        this.status = "Scheduled";
        this.consultationFee = 1500.00;
        this.treatmentCost = 0.00;
        this.totalBill = 1500.00;
    }

    // Parameterized Constructor for creating new appointments
    public Appointment(String patientName, String address, String contactNumber,
                       String dentistName, String treatmentType, String appointmentDate,
                       String appointmentTime, double treatmentCost, double consultationFee) {
        this.patientName = patientName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.dentistName = dentistName;
        this.treatmentType = treatmentType;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
        this.totalBill = treatmentCost + consultationFee;
        this.status = "Scheduled";
    }

    // Full Parameterized Constructor (for database retrieval)
    public Appointment(int appointmentNumber, String patientName, String address,
                       String contactNumber, String dentistName, String treatmentType,
                       String appointmentDate, String appointmentTime, double treatmentCost,
                       double consultationFee, double totalBill, String status, Timestamp createdAt) {
        this.appointmentNumber = appointmentNumber;
        this.patientName = patientName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.dentistName = dentistName;
        this.treatmentType = treatmentType;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
        this.totalBill = totalBill;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(int appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public double getTreatmentCost() {
        return treatmentCost;
    }

    public void setTreatmentCost(double treatmentCost) {
        this.treatmentCost = treatmentCost;
        this.totalBill = this.treatmentCost + this.consultationFee;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
        this.totalBill = this.treatmentCost + this.consultationFee;
    }

    public double getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(double totalBill) {
        this.totalBill = totalBill;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
