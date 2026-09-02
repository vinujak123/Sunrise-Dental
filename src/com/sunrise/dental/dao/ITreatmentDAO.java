package com.sunrise.dental.dao;

import com.sunrise.dental.model.Treatment;
import java.util.List;
import java.util.Optional;

/**
 * ITreatmentDAO – Data Access Object interface for Treatment model.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public interface ITreatmentDAO {

    /**
     * Retrieves all active treatments.
     * @return a List of Treatment objects
     */
    List<Treatment> findAllActive();

    /**
     * Finds a treatment by its ID.
     * @param id treatment ID
     * @return an Optional containing the Treatment if found, empty otherwise
     */
    Optional<Treatment> findById(int id);
    
    /**
     * Adds a new treatment to the system.
     * @param treatment the Treatment to add
     * @return true if successful, false otherwise
     */
    boolean insert(Treatment treatment);
    
    /**
     * Updates an existing treatment.
     * @param treatment the Treatment to update
     * @return true if successful, false otherwise
     */
    boolean update(Treatment treatment);
}
