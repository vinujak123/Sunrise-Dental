package com.sunrise.dental.dao;

import com.sunrise.dental.model.Patient;
import java.util.List;
import java.util.Optional;

/**
 * IPatientDAO – Data Access Object interface for Patient model.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public interface IPatientDAO {
    
    /**
     * Registers a new patient using the stored procedure to auto-generate the patient number.
     * @param patient the patient to register
     * @return the generated patient number, or null if registration failed
     */
    String registerPatient(Patient patient);
    
    /**
     * Finds a patient by their internal ID.
     * @param id patient ID
     * @return an Optional containing the Patient if found, empty otherwise
     */
    Optional<Patient> findById(int id);
    
    /**
     * Finds a patient by their patient number (e.g., P-0001).
     * @param patientNumber the patient number
     * @return an Optional containing the Patient if found, empty otherwise
     */
    Optional<Patient> findByPatientNumber(String patientNumber);
    
    /**
     * Searches for patients by name or contact number.
     * @param keyword search keyword
     * @return a List of matching Patient objects
     */
    List<Patient> searchPatients(String keyword);
    
    /**
     * Retrieves all active patients.
     * @return a List of Patient objects
     */
    List<Patient> findAllActive();
    
    /**
     * Updates an existing patient's details.
     * @param patient the patient to update
     * @return true if successful, false otherwise
     */
    boolean update(Patient patient);
}
