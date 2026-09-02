package com.sunrise.dental.model;

import java.sql.Timestamp;

/**
 * User – represents a system user (Admin / Receptionist / Dentist).
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class User {

    public enum Role { ADMIN, RECEPTIONIST, DENTIST }

    private int       userId;
    private String    username;
    private String    passwordHash;
    private String    fullName;
    private Role      role;
    private String    email;
    private String    contact;
    private boolean   active;
    private Timestamp createdAt;
    private Timestamp lastLogin;

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------
    public User() {}

    public User(int userId, String username, String fullName, Role role,
                String email, String contact, boolean active) {
        this.userId   = userId;
        this.username = username;
        this.fullName = fullName;
        this.role     = role;
        this.email    = email;
        this.contact  = contact;
        this.active   = active;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    public int       getUserId()        { return userId; }
    public void      setUserId(int v)   { this.userId = v; }

    public String    getUsername()         { return username; }
    public void      setUsername(String v) { this.username = v; }

    public String    getPasswordHash()         { return passwordHash; }
    public void      setPasswordHash(String v) { this.passwordHash = v; }

    public String    getFullName()         { return fullName; }
    public void      setFullName(String v) { this.fullName = v; }

    public Role      getRole()         { return role; }
    public void      setRole(Role v)   { this.role = v; }

    public String    getEmail()        { return email; }
    public void      setEmail(String v){ this.email = v; }

    public String    getContact()          { return contact; }
    public void      setContact(String v)  { this.contact = v; }

    public boolean   isActive()        { return active; }
    public void      setActive(boolean v){ this.active = v; }

    public Timestamp getCreatedAt()          { return createdAt; }
    public void      setCreatedAt(Timestamp v){ this.createdAt = v; }

    public Timestamp getLastLogin()           { return lastLogin; }
    public void      setLastLogin(Timestamp v){ this.lastLogin = v; }

    @Override
    public String toString() {
        return "User{id=" + userId + ", username='" + username +
               "', role=" + role + ", active=" + active + "}";
    }
}
