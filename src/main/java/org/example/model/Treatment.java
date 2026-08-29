package org.example.model;

/**
 * Treatment Model
 * Represents a dental treatment and its standard costs (consultation fee + procedure cost).
 */
public class Treatment {
    private int treatmentId;
    private String treatmentName;
    private double treatmentCost;
    private double consultationFee;

    // Default Constructor
    public Treatment() {
    }


    // Parameterized Constructor
    public Treatment(int treatmentId, String treatmentName, double treatmentCost, double consultationFee) {
        this.treatmentId = treatmentId;
        this.treatmentName = treatmentName;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
    }

    // Convenience Constructor for name and costs
    public Treatment(String treatmentName, double treatmentCost, double consultationFee) {
        this.treatmentName = treatmentName;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
    }

    // Getters and Setters
    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public double getTreatmentCost() {
        return treatmentCost;
    }

    public void setTreatmentCost(double treatmentCost) {
        this.treatmentCost = treatmentCost;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public double getTotalEstimate() {
        return treatmentCost + consultationFee;
    }

    /**
     * Prints full treatment details using System.out.println.
     */
    public void printDetails() {
        System.out.println("-------------------------------------------");
        System.out.println("Treatment ID     : " + treatmentId);
        System.out.println("Treatment Name   : " + treatmentName);
        System.out.println("Treatment Cost   : LKR " + treatmentCost);
        System.out.println("Consultation Fee : LKR " + consultationFee);
        System.out.println("Total Estimate   : LKR " + getTotalEstimate());
        System.out.println("-------------------------------------------");
    }

    @Override
    public String toString() {
        return treatmentName;
    }
}
