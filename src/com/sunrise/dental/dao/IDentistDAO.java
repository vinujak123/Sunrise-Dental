package com.sunrise.dental.dao;

import com.sunrise.dental.model.Dentist;
import java.util.List;
import java.util.Optional;

/**
 * IDentistDAO – Data Access Object interface for Dentist model.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public interface IDentistDAO {

    /**
     * Retrieves all active dentists.
     * @return a List of Dentist objects
     */
    List<Dentist> findAllActive();

    /**
     * Finds a dentist by their ID.
     * @param id dentist ID
     * @return an Optional containing the Dentist if found, empty otherwise
     */
    Optional<Dentist> findById(int id);
    
    /**
     * Finds a dentist associated with a specific user account.
     * @param userId the user ID
     * @return an Optional containing the Dentist if found, empty otherwise
     */
    Optional<Dentist> findByUserId(int userId);
}
