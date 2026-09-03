package com.sunrise.dental.dao;

import com.sunrise.dental.model.User;
import java.util.List;
import java.util.Optional;

/**
 * IUserDAO – Data Access Object interface for User model.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public interface IUserDAO {
    
    /**
     * Authenticate a user by username and password hash.
     * @param username the username
     * @param passwordHash the SHA-256 hashed password
     * @return an Optional containing the User if auth succeeds, empty otherwise
     */
    Optional<User> authenticate(String username, String passwordHash);
    
    /**
     * Finds a user by their unique ID.
     * @param id user ID
     * @return an Optional containing the User if found, empty otherwise
     */
    Optional<User> findById(int id);
    
    /**
     * Retrieves all active users in the system.
     * @return a List of User objects
     */
    List<User> findAllActive();
    
    /**
     * Adds a new user to the database.
     * @param user the User object to add
     * @return true if successful, false otherwise
     */
    boolean insert(User user);
    
    /**
     * Updates an existing user's details.
     * @param user the User object with updated details
     * @return true if successful, false otherwise
     */
    boolean update(User user);
    
    /**
     * Deactivates (soft deletes) a user.
     * @param id the ID of the user to deactivate
     * @return true if successful, false otherwise
     */
    boolean deactivate(int id);

    /**
     * Checks if a username already exists.
     * @param username the username to check
     * @return true if the username already exists
     */
    boolean usernameExists(String username);

    /**
     * Returns all users with is_active = 0 (pending Admin approval).
     * @return a List of pending User objects
     */
    List<User> findPendingUsers();

    /**
     * Activates a pending user account (Admin approval).
     * @param id the user ID to approve
     * @return true if successful
     */
    boolean approve(int id);

    /**
     * Permanently deletes a pending user account (Admin rejection).
     * @param id the user ID to reject
     * @return true if successful
     */
    boolean reject(int id);
}
