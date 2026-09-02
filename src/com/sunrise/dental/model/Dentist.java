package com.sunrise.dental.model;

import java.sql.Timestamp;

/**
 * Dentist – represents a dental practitioner at Sunrise Dental Clinic.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class Dentist {

    private int       dentistId;
    private Integer   userId;          // nullable FK to users table
    private String    name;
    private String    specialization;
    private String    qualification;
    private String    contact;
    private String    email;
    private String    availableDays;   // e.g. "MON,TUE,WED,THU,FRI"
    private boolean   active;
    private Timestamp createdAt;

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------
    public Dentist() {}

    public Dentist(int dentistId, String name, String specialization) {
        this.dentistId      = dentistId;
        this.name           = name;
        this.specialization = specialization;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    public int     getDentistId()          { return dentistId; }
    public void    setDentistId(int v)     { this.dentistId = v; }

    public Integer getUserId()             { return userId; }
    public void    setUserId(Integer v)    { this.userId = v; }

    public String  getName()               { return name; }
    public void    setName(String v)       { this.name = v; }

    public String  getSpecialization()          { return specialization; }
    public void    setSpecialization(String v)  { this.specialization = v; }

    public String  getQualification()           { return qualification; }
    public void    setQualification(String v)   { this.qualification = v; }

    public String  getContact()            { return contact; }
    public void    setContact(String v)    { this.contact = v; }

    public String  getEmail()              { return email; }
    public void    setEmail(String v)      { this.email = v; }

    public String  getAvailableDays()          { return availableDays; }
    public void    setAvailableDays(String v)  { this.availableDays = v; }

    public boolean isActive()              { return active; }
    public void    setActive(boolean v)    { this.active = v; }

    public Timestamp getCreatedAt()           { return createdAt; }
    public void      setCreatedAt(Timestamp v){ this.createdAt = v; }

    @Override
    public String toString() {
        return "Dentist{id=" + dentistId + ", name='" + name +
               "', specialization='" + specialization + "'}";
    }
}
