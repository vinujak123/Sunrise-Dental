package com.sunrise.dental.factory;

import com.sunrise.dental.model.User;
import com.sunrise.dental.util.PasswordUtil;

/**
 * UserFactory – Factory Design Pattern.
 * Handles creation of User objects, ensuring passwords are hashed correctly.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class UserFactory {

    /**
     * Creates a new User instance, automatically hashing the plain password.
     * 
     * @param username the username
     * @param plainPassword plain text password (will be hashed)
     * @param fullName user's full name
     * @param role the user's role (ADMIN, RECEPTIONIST, DENTIST)
     * @param email email address
     * @param contact contact number
     * @return a configured User object with a hashed password
     */
    public static User createUser(String username, String plainPassword, String fullName, User.Role role, String email, String contact) {
        User user = new User();
        user.setUsername(username);
        // Hash the password at creation time using the utility
        user.setPasswordHash(PasswordUtil.hash(plainPassword));
        user.setFullName(fullName);
        user.setRole(role);
        user.setEmail(email);
        user.setContact(contact);
        user.setActive(true);
        return user;
    }
}
