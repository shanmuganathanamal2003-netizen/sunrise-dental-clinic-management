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

    public Patient(String patientName, String contactNumber, String address) {
        this(patientName, "1 Month", "Not Specified", contactNumber, address, "");
    }

    public Patient(String patientName, String age, String contactNumber, String address) {
        this(patientName, age, "Not Specified", contactNumber, address, "");
    }

    public Patient(String patientName, String age, String gender, String contactNumber, String address, String medicalHistory) {
        this.patientName = patientName;
        this.age = (age != null && !age.trim().isEmpty()) ? age : "1 Month";
        this.gender = (gender != null && !gender.trim().isEmpty()) ? gender : "Not Specified";
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

    /**
     * Prints full patient details using System.out.println.
     */
    public void printDetails() {
        System.out.println("-------------------------------------------");
        System.out.println("Patient ID      : " + patientId);
        System.out.println("Patient Name    : " + patientName);
        System.out.println("Age             : " + age);
        System.out.println("Gender          : " + gender);
        System.out.println("Contact Number  : " + contactNumber);
        System.out.println("Address         : " + address);
        System.out.println("Medical History : " + medicalHistory);
        System.out.println("-------------------------------------------");
    }

    @Override
    public String toString() {
        return patientName + " (" + contactNumber + ") - Age: " + age;
    }
}
