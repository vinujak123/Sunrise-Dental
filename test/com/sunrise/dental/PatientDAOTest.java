package com.sunrise.dental;

import com.sunrise.dental.dao.IPatientDAO;
import com.sunrise.dental.dao.impl.PatientDAOImpl;
import com.sunrise.dental.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PatientDAOTest – JUnit integration tests for Patient data access.
 * Tests Valid, Boundary, and Invalid data scenarios.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class PatientDAOTest {

    private IPatientDAO patientDAO;

    @BeforeEach
    public void setUp() {
        patientDAO = new PatientDAOImpl();
    }

    // ----------------------------------------------------------------
    // Test: Register a new patient (Valid data)
    // ----------------------------------------------------------------
    @Test
    public void testRegisterPatient_ValidData() {
        Patient patient = new Patient();
        patient.setFirstName("Test");
        patient.setLastName("Patient");
        patient.setContact("0777999888");
        patient.setEmail("test.patient@test.com");

        String patientNumber = patientDAO.registerPatient(patient);

        assertNotNull(patientNumber, "Patient number should not be null on success");
        assertTrue(patientNumber.startsWith("P-"), "Patient number must follow P-XXXX format");
        assertTrue(patient.getPatientId() > 0, "Patient ID should be populated after registration");
    }

    // ----------------------------------------------------------------
    // Test: Search for a known patient (Valid keyword)
    // ----------------------------------------------------------------
    @Test
    public void testSearchPatients_ValidKeyword() {
        List<Patient> results = patientDAO.searchPatients("Amal");

        assertNotNull(results, "Result list should not be null");
        assertFalse(results.isEmpty(), "Should find at least one patient matching 'Amal' (from seed data)");

        // Verify that the result contains the expected patient
        boolean found = results.stream()
                .anyMatch(p -> "Amal".equalsIgnoreCase(p.getFirstName()));
        assertTrue(found, "Amal Jayawardena from seed data should be found");
    }

    // ----------------------------------------------------------------
    // Test: Search with an empty keyword (Boundary)
    // ----------------------------------------------------------------
    @Test
    public void testSearchPatients_EmptyKeyword() {
        // Empty keyword should return all active patients (no filter)
        List<Patient> results = patientDAO.searchPatients("");
        // Should not throw an exception; result may or may not be empty based on implementation
        assertNotNull(results);
    }

    // ----------------------------------------------------------------
    // Test: Search with a keyword that returns no results (Invalid / Non-existent)
    // ----------------------------------------------------------------
    @Test
    public void testSearchPatients_NoMatch() {
        List<Patient> results = patientDAO.searchPatients("XXXXXX_DOES_NOT_EXIST");
        assertNotNull(results, "Result list should not be null");
        assertTrue(results.isEmpty(), "No patients should be found for a non-existent keyword");
    }

    // ----------------------------------------------------------------
    // Test: Find patient by a valid patient number
    // ----------------------------------------------------------------
    @Test
    public void testFindByPatientNumber_Valid() {
        Optional<Patient> result = patientDAO.findByPatientNumber("P-0001");
        assertTrue(result.isPresent(), "P-0001 should exist in the seed data");
        assertEquals("Amal", result.get().getFirstName());
    }

    // ----------------------------------------------------------------
    // Test: Find patient by an invalid / non-existent number (Boundary)
    // ----------------------------------------------------------------
    @Test
    public void testFindByPatientNumber_Invalid() {
        Optional<Patient> result = patientDAO.findByPatientNumber("P-9999");
        assertFalse(result.isPresent(), "P-9999 should not exist in the database");
    }

    // ----------------------------------------------------------------
    // Test: Register a patient with null contact (Invalid – should fail)
    // ----------------------------------------------------------------
    @Test
    public void testRegisterPatient_NullContact() {
        Patient patient = new Patient();
        patient.setFirstName("Invalid");
        patient.setLastName("User");
        patient.setContact(null); // This is NOT NULL in the DB, so SP should fail

        String result = patientDAO.registerPatient(patient);
        assertNull(result, "Registration should fail when contact is null (DB NOT NULL constraint)");
    }
}
