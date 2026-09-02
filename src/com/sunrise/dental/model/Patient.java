package com.sunrise.dental.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Patient – represents a registered clinic patient.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class Patient {

    public enum Gender { MALE, FEMALE, OTHER }

    private int       patientId;
    private String    patientNumber;
    private String    firstName;
    private String    lastName;
    private Date      dateOfBirth;
    private Gender    gender;
    private String    bloodGroup;
    private String    address;
    private String    city;
    private String    contact;
    private String    email;
    private String    emergencyContact;
    private String    medicalNotes;
    private String    allergies;
    private boolean   active;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------
    public Patient() {}

    public Patient(String firstName, String lastName, String contact) {
        this.firstName = firstName;
        this.lastName  = lastName;
        this.contact   = contact;
    }

    // ----------------------------------------------------------------
    // Convenience helpers
    // ----------------------------------------------------------------
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    public int       getPatientId()          { return patientId; }
    public void      setPatientId(int v)     { this.patientId = v; }

    public String    getPatientNumber()          { return patientNumber; }
    public void      setPatientNumber(String v)  { this.patientNumber = v; }

    public String    getFirstName()          { return firstName; }
    public void      setFirstName(String v)  { this.firstName = v; }

    public String    getLastName()           { return lastName; }
    public void      setLastName(String v)   { this.lastName = v; }

    public Date      getDateOfBirth()        { return dateOfBirth; }
    public void      setDateOfBirth(Date v)  { this.dateOfBirth = v; }

    public Gender    getGender()             { return gender; }
    public void      setGender(Gender v)     { this.gender = v; }

    public String    getBloodGroup()         { return bloodGroup; }
    public void      setBloodGroup(String v) { this.bloodGroup = v; }

    public String    getAddress()            { return address; }
    public void      setAddress(String v)    { this.address = v; }

    public String    getCity()               { return city; }
    public void      setCity(String v)       { this.city = v; }

    public String    getContact()            { return contact; }
    public void      setContact(String v)    { this.contact = v; }

    public String    getEmail()              { return email; }
    public void      setEmail(String v)      { this.email = v; }

    public String    getEmergencyContact()         { return emergencyContact; }
    public void      setEmergencyContact(String v) { this.emergencyContact = v; }

    public String    getMedicalNotes()         { return medicalNotes; }
    public void      setMedicalNotes(String v) { this.medicalNotes = v; }

    public String    getAllergies()           { return allergies; }
    public void      setAllergies(String v)  { this.allergies = v; }

    public boolean   isActive()              { return active; }
    public void      setActive(boolean v)    { this.active = v; }

    public Timestamp getCreatedAt()           { return createdAt; }
    public void      setCreatedAt(Timestamp v){ this.createdAt = v; }

    public Timestamp getUpdatedAt()           { return updatedAt; }
    public void      setUpdatedAt(Timestamp v){ this.updatedAt = v; }

    @Override
    public String toString() {
        return "Patient{id=" + patientId + ", number='" + patientNumber +
               "', name='" + getFullName() + "', contact='" + contact + "'}";
    }
}
