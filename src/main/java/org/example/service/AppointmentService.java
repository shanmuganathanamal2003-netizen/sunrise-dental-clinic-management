package org.example.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.example.dao.AppointmentDAO;
import org.example.dao.PatientDAO;
import org.example.model.Appointment;
import org.example.model.Patient;


/**
 * AppointmentService - Business Logic Layer
 * 
 * Handles appointment validations, double-booking prevention rules,
 * search operations, cancellation management, and statistics computation.
 */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO;
    private final PatientDAO patientDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
    }

    /**
     * Validates and registers a new appointment.
     * 
     * @param appointment The appointment details to create
     * @return Generated appointment number
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if the dentist is already booked
     * @throws SQLException on database error
     */
    public int registerAppointment(Appointment appointment) throws SQLException {
        // 1. Validation: Ensure no mandatory field is blank
        if (appointment.getPatientName() == null || appointment.getPatientName().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient Name cannot be blank.");
        }
        if (appointment.getAddress() == null || appointment.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Patient Address cannot be blank.");
        }
        if (appointment.getContactNumber() == null || appointment.getContactNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact Number cannot be blank.");
        }
        if (appointment.getDentistName() == null || appointment.getDentistName().trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a Dentist.");
        }
        if (appointment.getTreatmentType() == null || appointment.getTreatmentType().trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a Treatment Type.");
        }
        if (appointment.getAppointmentDate() == null || appointment.getAppointmentDate().trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment Date cannot be blank.");
        }
        if (appointment.getAppointmentTime() == null || appointment.getAppointmentTime().trim().isEmpty()) {
            throw new IllegalArgumentException("Appointment Time cannot be blank.");
        }

        // Contact Number format validation
        if (!appointment.getContactNumber().trim().matches("^[0-9+ -]{7,15}$")) {
            throw new IllegalArgumentException("Please enter a valid phone number (digits only, e.g. 0771234567).");
        }

        // 2. Ensure or link Patient record in patients table
        if (appointment.getPatientId() == null || appointment.getPatientId() <= 0) {
            int patientId = patientDAO.findOrCreatePatient(
                appointment.getPatientName(),
                appointment.getPatientAge(),
                "Not Specified",
                appointment.getContactNumber(),
                appointment.getAddress(),
                ""
            );
            appointment.setPatientId(patientId);
        }

        // 3. Double-Booking Prevention: Check if the doctor is already booked at that date/time
        boolean isBooked = appointmentDAO.isDentistBooked(
            appointment.getDentistName(),
            appointment.getAppointmentDate(),
            appointment.getAppointmentTime(),
            -1
        );

        if (isBooked) {
            throw new IllegalStateException(
                "DOUBLE-BOOKING DETECTED!\n" +
                appointment.getDentistName() + " already has an appointment booked on " +
                appointment.getAppointmentDate() + " at " + appointment.getAppointmentTime() + ".\n\n" +
                "Please choose a different time slot or select another dentist."
            );
        }

        // 4. Save to database
        return appointmentDAO.createAppointment(appointment);
    }

    /**
     * Cancels an appointment and frees the doctor's time slot.
     */
    public boolean cancelAppointment(int appointmentNumber, String reason) throws SQLException {
        if (appointmentNumber <= 0) {
            throw new IllegalArgumentException("Invalid appointment number.");
        }
        return appointmentDAO.cancelAppointment(appointmentNumber, reason);
    }

    public Patient getPatientDetails(int patientId) throws SQLException {
        return patientDAO.getPatientById(patientId);
    }

    public boolean confirmAppointment(int appointmentNumber) throws SQLException {
        return appointmentDAO.confirmAppointment(appointmentNumber);
    }

    /**
     * Searches appointments by Appointment Number.
     */
    public Appointment getAppointmentByNumber(int appointmentNumber) throws SQLException {
        return appointmentDAO.getAppointmentByNumber(appointmentNumber);
    }

    /**
     * Searches appointments by Patient Name or Keyword.
     */
    public List<Appointment> searchAppointments(String query) throws SQLException {
        return appointmentDAO.searchAppointments(query);
    }

    /**
     * Retrieves all appointments for a patient (history).
     */
    public List<Appointment> getAppointmentsByPatientId(int patientId) throws SQLException {
        return appointmentDAO.getAppointmentsByPatientId(patientId);
    }

    /**
     * Retrieves all appointments for a patient by Name or Phone.
     */
    public List<Appointment> getAppointmentsByPatientNameOrPhone(String name, String phone) throws SQLException {
        return appointmentDAO.getAppointmentsByPatientNameOrPhone(name, phone);
    }

    /**
     * Retrieves all appointments.
     */
    public List<Appointment> getAllAppointments() throws SQLException {
        return appointmentDAO.getAllAppointments();
    }

    /**
     * Searches appointments specifically by Patient Name.
     */
    public List<Appointment> searchByPatientName(String patientName) throws SQLException {
        return appointmentDAO.searchByPatientName(patientName);
    }

    /**
     * Retrieves appointments filtered by status (Scheduled, Billed, Cancelled).
     */
    public List<Appointment> getAppointmentsByStatus(String status) throws SQLException {
        return appointmentDAO.getAppointmentsByStatus(status);
    }

    /**
     * Checks if a dentist is booked at a given slot.
     */
    public boolean isDentistBooked(String dentistName, String appointmentDate, String appointmentTime, int excludeAppointmentNo) throws SQLException {
        return appointmentDAO.isDentistBooked(dentistName, appointmentDate, appointmentTime, excludeAppointmentNo);
    }

    /**
     * Updates appointment billing details.
     */
    public boolean updateAppointmentBilling(int appointmentNumber, double treatmentCost, double consultationFee, double totalBill) throws SQLException {
        return appointmentDAO.updateAppointmentBilling(appointmentNumber, treatmentCost, consultationFee, totalBill);
    }

    /**
     * Returns preview for next appointment number.
     */
    public int getNextAppointmentNumberPreview() {
        return appointmentDAO.getNextAppointmentNumberPreview();
    }

    /**
     * Retrieves dashboard statistics map.
     */
    public Map<String, Object> getDashboardStats() {
        return appointmentDAO.getDashboardStatistics();
    }

    /**
     * Retrieves appointments scheduled for a specific doctor on a single date.
     */
    public List<Appointment> getAppointmentsForDoctor(String username, String date) throws SQLException {
        return appointmentDAO.getAppointmentsByDoctorAndDate(username, date);
    }

    /**
     * Retrieves appointments scheduled for a specific doctor across a date range.
     */
    public List<Appointment> getAppointmentsForDoctor(String username, String fromDate, String toDate) throws SQLException {
        return appointmentDAO.getAppointmentsByDoctorAndDateRange(username, fromDate, toDate);
    }

    /**
     * Retrieves all appointments assigned to a specific doctor.
     */
    public List<Appointment> getAppointmentsForDoctor(String username) throws SQLException {
        return appointmentDAO.getAppointmentsByDoctor(username);
    }

    /**
     * Saves diagnosis and clinical treatment notes for an appointment.
     */
    public boolean saveDoctorNotes(int appointmentNumber, String notes) throws SQLException {
        if (appointmentNumber <= 0) {
            throw new IllegalArgumentException("Invalid appointment number.");
        }
        return appointmentDAO.updateDoctorNotes(appointmentNumber, notes);
    }

    /**
     * Displays appointment details to the console using System.out.println.
     */
    public void displayAppointmentDetails(int appointmentNumber) {
        try {
            Appointment appt = getAppointmentByNumber(appointmentNumber);
            if (appt != null) {
                appt.printDetails();
            } else {
                System.out.println("[Appointment] No appointment found with ID: " + appointmentNumber);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching appointment for display: " + e.getMessage());
        }
    }

    /**
     * Displays all appointments to the console using System.out.println.
     */
    public void displayAllAppointments() {
        try {
            List<Appointment> list = getAllAppointments();
            System.out.println("====== ALL APPOINTMENTS (" + list.size() + ") ======");
            for (Appointment appt : list) {
                appt.printDetails();
            }
        } catch (SQLException e) {
            System.out.println("Error listing appointments: " + e.getMessage());
        }
    }
}

