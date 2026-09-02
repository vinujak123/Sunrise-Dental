package com.sunrise.dental.model;

/**
 * Treatment – a dental service offered by the clinic with its fee.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class Treatment {

    private int     treatmentId;
    private String  treatmentCode;
    private String  treatmentName;
    private String  category;
    private String  description;
    private int     durationMin;
    private double  fee;
    private boolean active;

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------
    public Treatment() {}

    public Treatment(int treatmentId, String treatmentCode,
                     String treatmentName, double fee) {
        this.treatmentId   = treatmentId;
        this.treatmentCode = treatmentCode;
        this.treatmentName = treatmentName;
        this.fee           = fee;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    public int    getTreatmentId()          { return treatmentId; }
    public void   setTreatmentId(int v)     { this.treatmentId = v; }

    public String getTreatmentCode()             { return treatmentCode; }
    public void   setTreatmentCode(String v)     { this.treatmentCode = v; }

    public String getTreatmentName()             { return treatmentName; }
    public void   setTreatmentName(String v)     { this.treatmentName = v; }

    public String getCategory()              { return category; }
    public void   setCategory(String v)      { this.category = v; }

    public String getDescription()           { return description; }
    public void   setDescription(String v)   { this.description = v; }

    public int    getDurationMin()           { return durationMin; }
    public void   setDurationMin(int v)      { this.durationMin = v; }

    public double getFee()                   { return fee; }
    public void   setFee(double v)           { this.fee = v; }

    public boolean isActive()                { return active; }
    public void    setActive(boolean v)      { this.active = v; }

    @Override
    public String toString() {
        return "Treatment{id=" + treatmentId + ", code='" + treatmentCode +
               "', name='" + treatmentName + "', fee=" + fee + "}";
    }
}
