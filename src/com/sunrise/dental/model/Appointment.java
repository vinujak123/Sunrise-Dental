package com.sunrise.dental.model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Appointment – links a Patient, Dentist, and Treatment for a scheduled visit.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class Appointment {

    public enum Status { PENDING, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW }

    private int       apptId;
    private String    apptNumber;
    private int       patientId;
    private int       dentistId;
    private int       treatmentId;
    private Date      apptDate;
    private Time      apptTime;
    private Time      endTime;
    private Status    status;
    private String    notes;
    private Integer   createdBy;
    private Integer   updatedBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Joined fields (not stored in appointments table)
    private String    patientName;
    private String    patientNumber;
    private String    dentistName;
    private String    treatmentName;
    private double    treatmentFee;

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------
    public Appointment() {}

    public Appointment(int patientId, int dentistId, int treatmentId,
                       Date apptDate, Time apptTime) {
        this.patientId   = patientId;
        this.dentistId   = dentistId;
        this.treatmentId = treatmentId;
        this.apptDate    = apptDate;
        this.apptTime    = apptTime;
        this.status      = Status.PENDING;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    public int     getApptId()            { return apptId; }
    public void    setApptId(int v)       { this.apptId = v; }

    public String  getApptNumber()             { return apptNumber; }
    public void    setApptNumber(String v)     { this.apptNumber = v; }

    public int     getPatientId()          { return patientId; }
    public void    setPatientId(int v)     { this.patientId = v; }

    public int     getDentistId()          { return dentistId; }
    public void    setDentistId(int v)     { this.dentistId = v; }

    public int     getTreatmentId()        { return treatmentId; }
    public void    setTreatmentId(int v)   { this.treatmentId = v; }

    public Date    getApptDate()           { return apptDate; }
    public void    setApptDate(Date v)     { this.apptDate = v; }

    public Time    getApptTime()           { return apptTime; }
    public void    setApptTime(Time v)     { this.apptTime = v; }

    public Time    getEndTime()            { return endTime; }
    public void    setEndTime(Time v)      { this.endTime = v; }

    public Status  getStatus()             { return status; }
    public void    setStatus(Status v)     { this.status = v; }

    public String  getNotes()              { return notes; }
    public void    setNotes(String v)      { this.notes = v; }

    public Integer getCreatedBy()          { return createdBy; }
    public void    setCreatedBy(Integer v) { this.createdBy = v; }

    public Integer getUpdatedBy()          { return updatedBy; }
    public void    setUpdatedBy(Integer v) { this.updatedBy = v; }

    public Timestamp getCreatedAt()           { return createdAt; }
    public void      setCreatedAt(Timestamp v){ this.createdAt = v; }

    public Timestamp getUpdatedAt()           { return updatedAt; }
    public void      setUpdatedAt(Timestamp v){ this.updatedAt = v; }

    // Joined field accessors
    public String  getPatientName()            { return patientName; }
    public void    setPatientName(String v)    { this.patientName = v; }

    public String  getPatientNumber()          { return patientNumber; }
    public void    setPatientNumber(String v)  { this.patientNumber = v; }

    public String  getDentistName()            { return dentistName; }
    public void    setDentistName(String v)    { this.dentistName = v; }

    public String  getTreatmentName()          { return treatmentName; }
    public void    setTreatmentName(String v)  { this.treatmentName = v; }

    public double  getTreatmentFee()           { return treatmentFee; }
    public void    setTreatmentFee(double v)   { this.treatmentFee = v; }

    @Override
    public String toString() {
        return "Appointment{id=" + apptId + ", number='" + apptNumber +
               "', date=" + apptDate + ", time=" + apptTime +
               ", status=" + status + "}";
    }
}
