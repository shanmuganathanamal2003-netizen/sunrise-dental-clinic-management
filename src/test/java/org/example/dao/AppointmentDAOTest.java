package org.example.dao;

import org.example.model.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentDAOTest {

    private AppointmentDAO appointmentDAO;

    @BeforeEach
    void setUp() {
        appointmentDAO = new AppointmentDAO();
    }

    /**
     * TEST 1 - Read-only test.
     * Validates that the system correctly detects a dentist is booked
     * at a date/time slot, using a fresh appointment created by the test
     * itself (so it doesn't depend on seed data that might have changed).
     */
    @Test
    @DisplayName("isDentistBooked() correctly detects a booked slot after creating one")
    void testIsDentistBooked_detectsExistingBooking() throws SQLException {
        Appointment testAppt = new Appointment();
        testAppt.setPatientName("Booking Check Patient");
        testAppt.setPatientAge("40 Years");
        testAppt.setAddress("Test Address");
        testAppt.setContactNumber("0770000001");
        testAppt.setDentistName("Dr. Booking Test Dentist");
        testAppt.setTreatmentType("General Dental Consultation");
        testAppt.setAppointmentDate("2099-03-15");
        testAppt.setAppointmentTime("10:00 AM");
        testAppt.setTreatmentCost(1000.00);
        testAppt.setConsultationFee(1500.00);
        testAppt.setTotalBill(2500.00);
        testAppt.setStatus("Scheduled");

        int generatedId = appointmentDAO.createAppointment(testAppt);

        boolean isBooked = appointmentDAO.isDentistBooked(
                "Dr. Booking Test Dentist",
                "2099-03-15",
                "10:00 AM",
                -1
        );

        assertTrue(isBooked, "Expected this dentist/date/time slot to be detected as booked");

        appointmentDAO.deleteAppointment(generatedId);
    }

    /**
     * TEST 2 - Read-only test.
     * Validates that the system correctly identifies a FREE slot
     * (a date/time with no existing appointment) as available.
     */
    @Test
    @DisplayName("isDentistBooked() correctly identifies a free slot as available")
    void testIsDentistBooked_freeSlotIsAvailable() throws SQLException {
        boolean isBooked = appointmentDAO.isDentistBooked(
                "Dr. Nobody Books This One",
                "2099-04-20",
                "07:00 AM",
                -1
        );

        assertFalse(isBooked, "Expected this date/time slot to be available (not booked)");
    }

    /**
     * TEST 3 - Write test.
     * Validates that creating a new appointment actually inserts a row
     * and returns a valid generated appointment number.
     */
    @Test
    @DisplayName("createAppointment() inserts a new appointment and returns a valid ID")
    void testCreateAppointment_insertsNewAppointment() throws SQLException {
        Appointment newAppt = new Appointment();
        newAppt.setPatientName("Test Automation Patient");
        newAppt.setPatientAge("30 Years");
        newAppt.setAddress("123 Test Lane, Colombo");
        newAppt.setContactNumber("0770000000");
        newAppt.setDentistName("Dr. Test Dentist");
        newAppt.setTreatmentType("General Dental Consultation");
        newAppt.setAppointmentDate("2099-02-02");
        newAppt.setAppointmentTime("08:00 AM");
        newAppt.setTreatmentCost(1000.00);
        newAppt.setConsultationFee(1500.00);
        newAppt.setTotalBill(2500.00);
        newAppt.setStatus("Scheduled");

        int generatedId = appointmentDAO.createAppointment(newAppt);
        assertTrue(generatedId > 0, "createAppointment() should return a positive generated appointment_number");

        Appointment fetched = appointmentDAO.getAppointmentByNumber(generatedId);
        assertNotNull(fetched, "The newly created appointment should be retrievable by ID");
        assertEquals("Test Automation Patient", fetched.getPatientName());

        appointmentDAO.deleteAppointment(generatedId);
    }
}