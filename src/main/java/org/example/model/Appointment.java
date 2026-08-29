package org.example.model;

import java.sql.Timestamp;

/**
 * Appointment Model
 * Represents a dental patient appointment booking, doctor assignment, and billing status.
 */
public class Appointment {
    private int appointmentNumber;
    private Integer patientId;
    private String patientName;
    private String patientAge;      // e.g. "1 Month", "6 Months", "35 Years"
    private String address;
    private String contactNumber;
    private String dentistName;
    private String assignedDoctorUsername;
    private String doctorNotes;
    private String treatmentType;
    private String appointmentDate; // Format: YYYY-MM-DD
    private String appointmentTime; // e.g. 09:00 AM
    private double treatmentCost;
    private double consultationFee;
    private double totalBill;
    private String status;          // "Scheduled", "Billed", "Cancelled"
    private String cancellationReason;
    private Timestamp createdAt;

    // Default Constructor
    public Appointment() {
        this.status = "Scheduled";
        this.patientAge = "1 Month";
        this.consultationFee = 1500.00;
        this.treatmentCost = 0.00;
        this.totalBill = 1500.00;
    }

    // Parameterized Constructor without patientAge (defaults to 1 Month)
    public Appointment(String patientName, String address, String contactNumber,
                       String dentistName, String treatmentType, String appointmentDate,
                       String appointmentTime, double treatmentCost, double consultationFee) {
        this(null, patientName, "1 Month", address, contactNumber, dentistName, null, treatmentType, appointmentDate, appointmentTime, treatmentCost, consultationFee);
    }

    // Parameterized Constructor with patientAge
    public Appointment(String patientName, String patientAge, String address, String contactNumber,
                       String dentistName, String treatmentType, String appointmentDate,
                       String appointmentTime, double treatmentCost, double consultationFee) {
        this(null, patientName, patientAge, address, contactNumber, dentistName, null, treatmentType, appointmentDate, appointmentTime, treatmentCost, consultationFee);
    }

    // Constructor with patientId and patientAge
    public Appointment(Integer patientId, String patientName, String patientAge, String address, String contactNumber,
                       String dentistName, String treatmentType, String appointmentDate,
                       String appointmentTime, double treatmentCost, double consultationFee) {
        this(patientId, patientName, patientAge, address, contactNumber, dentistName, null, treatmentType, appointmentDate, appointmentTime, treatmentCost, consultationFee);
    }

    // Constructor with patientId, patientAge, and assignedDoctorUsername
    public Appointment(Integer patientId, String patientName, String patientAge, String address, String contactNumber,
                       String dentistName, String assignedDoctorUsername, String treatmentType, String appointmentDate,
                       String appointmentTime, double treatmentCost, double consultationFee) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientAge = (patientAge != null && !patientAge.trim().isEmpty()) ? patientAge : "1 Month";
        this.address = address;
        this.contactNumber = contactNumber;
        this.dentistName = dentistName;
        this.assignedDoctorUsername = assignedDoctorUsername;
        this.treatmentType = treatmentType;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
        this.totalBill = treatmentCost + consultationFee;
        this.status = "Scheduled";
    }

    // Full Parameterized Constructor (for database retrieval)
    public Appointment(int appointmentNumber, Integer patientId, String patientName, String patientAge, String address,
                       String contactNumber, String dentistName, String assignedDoctorUsername, String doctorNotes, String treatmentType,
                       String appointmentDate, String appointmentTime, double treatmentCost,
                       double consultationFee, double totalBill, String status, String cancellationReason, Timestamp createdAt) {
        this.appointmentNumber = appointmentNumber;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.address = address;
        this.contactNumber = contactNumber;
        this.dentistName = dentistName;
        this.assignedDoctorUsername = assignedDoctorUsername;
        this.doctorNotes = doctorNotes;
        this.treatmentType = treatmentType;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
        this.totalBill = totalBill;
        this.status = status;
        this.cancellationReason = cancellationReason;
        this.createdAt = createdAt;
    }

    // Backward-compatible constructor for database retrieval without doctor linkage
    public Appointment(int appointmentNumber, Integer patientId, String patientName, String patientAge, String address,
                       String contactNumber, String dentistName, String treatmentType,
                       String appointmentDate, String appointmentTime, double treatmentCost,
                       double consultationFee, double totalBill, String status, String cancellationReason, Timestamp createdAt) {
        this(appointmentNumber, patientId, patientName, patientAge, address, contactNumber, dentistName, null, null, treatmentType, appointmentDate, appointmentTime, treatmentCost, consultationFee, totalBill, status, cancellationReason, createdAt);
    }

    // Getters and Setters
    public int getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(int appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
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

    public String getAssignedDoctorUsername() {
        return assignedDoctorUsername;
    }

    public void setAssignedDoctorUsername(String assignedDoctorUsername) {
        this.assignedDoctorUsername = assignedDoctorUsername;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
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

    public double calculateTotal() {
        this.totalBill = this.treatmentCost + this.consultationFee;
        return this.totalBill;
    }

    public double getEstimatedTotal() {
        return calculateTotal();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Prints full appointment details using System.out.println.
     */
    public void printDetails() {
        System.out.println("-------------------------------------------");
        System.out.println("Appointment Number : " + appointmentNumber);
        System.out.println("Patient Name       : " + patientName);
        System.out.println("Patient Age        : " + patientAge);
        System.out.println("Address            : " + address);
        System.out.println("Contact Number     : " + contactNumber);
        System.out.println("Dentist Name       : " + dentistName);
        if (assignedDoctorUsername != null) {
            System.out.println("Doctor Username    : " + assignedDoctorUsername);
        }
        if (doctorNotes != null && !doctorNotes.trim().isEmpty()) {
            System.out.println("Doctor Notes       : " + doctorNotes);
        }
        System.out.println("Treatment Type     : " + treatmentType);
        System.out.println("Appointment Date   : " + appointmentDate);
        System.out.println("Appointment Time   : " + appointmentTime);
        System.out.println("Consultation Fee   : LKR " + consultationFee);
        System.out.println("Treatment Cost     : LKR " + treatmentCost);
        System.out.println("Total Bill         : LKR " + totalBill);
        System.out.println("Status             : " + status);
        if (cancellationReason != null) {
            System.out.println("Cancellation Reason: " + cancellationReason);
        }
        System.out.println("-------------------------------------------");
    }

    @Override
    public String toString() {
        return "Appointment #" + appointmentNumber + " - " + patientName + " (" + appointmentDate + " at " + appointmentTime + ")";
    }
}
