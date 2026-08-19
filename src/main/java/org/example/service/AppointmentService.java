package org.example.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.example.dao.AppointmentDAO;
import org.example.dao.PatientDAO;
import org.example.model.Appointment;

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
}

