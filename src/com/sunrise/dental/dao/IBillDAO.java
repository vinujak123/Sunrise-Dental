package com.sunrise.dental.dao;

import com.sunrise.dental.model.Bill;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * IBillDAO – Data Access Object interface for Bill model.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public interface IBillDAO {

    /**
     * Generates a new bill using the stored procedure.
     * @param bill the Bill object with required details
     * @return the generated bill number, or null if generation failed
     */
    String generateBill(Bill bill);

    /**
     * Finds a bill by its ID.
     * @param id bill ID
     * @return an Optional containing the Bill if found, empty otherwise
     */
    Optional<Bill> findById(int id);
    
    /**
     * Finds a bill by its bill number.
     * @param billNumber bill number
     * @return an Optional containing the Bill if found, empty otherwise
     */
    Optional<Bill> findByBillNumber(String billNumber);

    /**
     * Retrieves the bill associated with a specific appointment.
     * @param apptId the appointment ID
     * @return an Optional containing the Bill if found, empty otherwise
     */
    Optional<Bill> findByAppointmentId(int apptId);

    /**
     * Updates the payment status of a bill (e.g., to PAID).
     * @param billId the bill ID
     * @param status the new payment status
     * @return true if successful, false otherwise
     */
    boolean updatePaymentStatus(int billId, String status);

    /**
     * Calculates the total daily revenue using the stored function.
     * @param date the date to calculate revenue for
     * @return the total revenue as a double
     */
    double getDailyRevenue(Date date);
    
    /**
     * Retrieves all bills generated on a specific date.
     * @param date the date to fetch bills for
     * @return a List of Bill objects
     */
    List<Bill> findBillsByDate(Date date);
}
