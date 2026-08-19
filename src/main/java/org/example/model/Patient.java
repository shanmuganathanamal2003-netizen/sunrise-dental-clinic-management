package org.example.model;

import java.sql.Timestamp;

/**
 * Patient Model
 * Represents a registered clinic patient with age, contact information,
 * and medical/dental history background.
 */
public class Patient {

    private int patientId;
    private String patientName;
    private String age;             // e.g. "1 Month", "6 Months", "28 Years"
    private String gender;          // e.g. "Male", "Female", "Other"
    private String contactNumber;
    private String address;
    private String medicalHistory;
    private Timestamp createdAt;

    // Constructors
    public Patient() {
        this.age = "1 Month";
        this.gender = "Not Specified";
    }

    public Patient(String patientName, String age, String gender, String contactNumber, String address, String medicalHistory) {
        this.patientName = patientName;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.medicalHistory = medicalHistory;
    }

    public Patient(int patientId, String patientName, String age, String gender, String contactNumber, String address, String medicalHistory) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.medicalHistory = medicalHistory;
    }

    public Patient(int patientId, String patientName, String age, String gender, String contactNumber, String address, String medicalHistory, Timestamp createdAt) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.medicalHistory = medicalHistory;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return patientName + " (" + contactNumber + ") - Age: " + age;
    }
}
