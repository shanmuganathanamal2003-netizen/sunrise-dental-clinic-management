package org.example.dao;

import org.example.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PatientDAOTest {

    private PatientDAO patientDAO;

    @BeforeEach
    void setUp() {
        patientDAO = new PatientDAO();
    }

    @Test
    @DisplayName("searchPatients() finds an existing patient by name")
    void testSearchPatients_findsExistingPatient() throws SQLException {
        List<Patient> results = patientDAO.searchPatients("Kamal");

        assertFalse(results.isEmpty(), "Expected at least one patient matching 'Kamal'");
        assertTrue(
                results.stream().anyMatch(p -> p.getPatientName().toLowerCase().contains("kamal")),
                "Search results should contain a patient named Kamal Perera"
        );
    }

    @Test
    @DisplayName("createPatient() inserts a new patient and returns a valid ID")
    void testCreatePatient_insertsNewPatient() throws SQLException {
        Patient newPatient = new Patient(
                "Test Automation Patient",
                "30 Years",
                "Male",
                "0770000000",
                "123 Test Lane, Colombo",
                "No known allergies (created by automated test)"
        );

        int generatedId = patientDAO.createPatient(newPatient);
        assertTrue(generatedId > 0, "createPatient() should return a positive generated patient_id");

        Patient fetched = patientDAO.getPatientById(generatedId);
        assertNotNull(fetched, "The newly created patient should be retrievable by ID");
        assertEquals("Test Automation Patient", fetched.getPatientName());

        patientDAO.deletePatient(generatedId);
    }
}