package com.sunrise.dental.model;

import java.sql.Timestamp;

/**
 * Bill – invoice generated for a completed appointment.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class Bill {

    public enum PaymentMethod { CASH, CARD, INSURANCE, ONLINE }
    public enum PaymentStatus { PENDING, PAID, REFUNDED, CANCELLED }

    private int           billId;
    private String        billNumber;
    private int           apptId;
    private double        treatmentFee;
    private double        consultationFee;
    private double        discountPercent;
    private double        discountAmount;
    private double        subtotal;
    private double        taxPercent;
    private double        taxAmount;
    private double        totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String        insuranceProvider;
    private double        insuranceAmount;
    private Integer       generatedBy;
    private Timestamp     generatedAt;
    private Timestamp     paidAt;
    private String        notes;

    // Joined fields for display
    private String apptNumber;
    private String patientName;
    private String patientNumber;
    private String dentistName;
    private String treatmentName;
    private String apptDate;
    private String apptTime;
    private String generatedByName;

    // ----------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------
    public Bill() {
        this.consultationFee = 500.00;
        this.discountPercent = 0.00;
        this.taxPercent      = 0.00;
        this.paymentMethod   = PaymentMethod.CASH;
        this.paymentStatus   = PaymentStatus.PENDING;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------
    public int    getBillId()              { return billId; }
    public void   setBillId(int v)         { this.billId = v; }

    public String getBillNumber()          { return billNumber; }
    public void   setBillNumber(String v)  { this.billNumber = v; }

    public int    getApptId()              { return apptId; }
    public void   setApptId(int v)         { this.apptId = v; }

    public double getTreatmentFee()        { return treatmentFee; }
    public void   setTreatmentFee(double v){ this.treatmentFee = v; }

    public double getConsultationFee()          { return consultationFee; }
    public void   setConsultationFee(double v)  { this.consultationFee = v; }

    public double getDiscountPercent()          { return discountPercent; }
    public void   setDiscountPercent(double v)  { this.discountPercent = v; }

    public double getDiscountAmount()           { return discountAmount; }
    public void   setDiscountAmount(double v)   { this.discountAmount = v; }

    public double getSubtotal()            { return subtotal; }
    public void   setSubtotal(double v)    { this.subtotal = v; }

    public double getTaxPercent()          { return taxPercent; }
    public void   setTaxPercent(double v)  { this.taxPercent = v; }

    public double getTaxAmount()           { return taxAmount; }
    public void   setTaxAmount(double v)   { this.taxAmount = v; }

    public double getTotalAmount()         { return totalAmount; }
    public void   setTotalAmount(double v) { this.totalAmount = v; }

    public PaymentMethod getPaymentMethod()          { return paymentMethod; }
    public void          setPaymentMethod(PaymentMethod v){ this.paymentMethod = v; }

    public PaymentStatus getPaymentStatus()          { return paymentStatus; }
    public void          setPaymentStatus(PaymentStatus v){ this.paymentStatus = v; }

    public String  getInsuranceProvider()          { return insuranceProvider; }
    public void    setInsuranceProvider(String v)  { this.insuranceProvider = v; }

    public double  getInsuranceAmount()            { return insuranceAmount; }
    public void    setInsuranceAmount(double v)    { this.insuranceAmount = v; }

    public Integer getGeneratedBy()           { return generatedBy; }
    public void    setGeneratedBy(Integer v)  { this.generatedBy = v; }

    public Timestamp getGeneratedAt()           { return generatedAt; }
    public void      setGeneratedAt(Timestamp v){ this.generatedAt = v; }

    public Timestamp getPaidAt()           { return paidAt; }
    public void      setPaidAt(Timestamp v){ this.paidAt = v; }

    public String  getNotes()              { return notes; }
    public void    setNotes(String v)      { this.notes = v; }

    // Joined field accessors
    public String getApptNumber()          { return apptNumber; }
    public void   setApptNumber(String v)  { this.apptNumber = v; }

    public String getPatientName()         { return patientName; }
    public void   setPatientName(String v) { this.patientName = v; }

    public String getPatientNumber()           { return patientNumber; }
    public void   setPatientNumber(String v)   { this.patientNumber = v; }

    public String getDentistName()         { return dentistName; }
    public void   setDentistName(String v) { this.dentistName = v; }

    public String getTreatmentName()           { return treatmentName; }
    public void   setTreatmentName(String v)   { this.treatmentName = v; }

    public String getApptDate()            { return apptDate; }
    public void   setApptDate(String v)    { this.apptDate = v; }

    public String getApptTime()            { return apptTime; }
    public void   setApptTime(String v)    { this.apptTime = v; }

    public String getGeneratedByName()          { return generatedByName; }
    public void   setGeneratedByName(String v)  { this.generatedByName = v; }

    @Override
    public String toString() {
        return "Bill{id=" + billId + ", number='" + billNumber +
               "', total=" + totalAmount + ", status=" + paymentStatus + "}";
    }
}
