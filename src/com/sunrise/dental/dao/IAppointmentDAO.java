package com.sunrise.dental.dao;

import com.sunrise.dental.model.Appointment;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * IAppointmentDAO – Data Access Object interface for Appointment model.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public interface IAppointmentDAO {

    /**
     * Books a new appointment using the stored procedure.
     * @param appt the Appointment object containing details
     * @param userId the ID of the user creating the appointment
     * @return the generated appointment number, or an error message if double booking occurs
     */
    String bookAppointment(Appointment appt, int userId);

    /**
     * Finds an appointment by its ID.
     * @param id appointment ID
     * @return an Optional containing the Appointment if found, empty otherwise
     */
    Optional<Appointment> findById(int id);
    
    /**
     * Finds an appointment by its appointment number.
     * @param apptNumber appointment number
     * @return an Optional containing the Appointment if found, empty otherwise
     */
    Optional<Appointment> findByApptNumber(String apptNumber);

    /**
     * Retrieves all appointments for a specific date.
     * @param date the date to fetch appointments for
     * @return a List of Appointment objects
     */
    List<Appointment> findByDate(Date date);

    /**
     * Retrieves all appointments for a specific patient.
     * @param patientId the patient ID
     * @return a List of Appointment objects
     */
    List<Appointment> findByPatientId(int patientId);

    /**
     * Updates the status of an appointment using the stored procedure.
     * @param apptId the appointment ID
     * @param status the new status (e.g., CONFIRMED, COMPLETED, CANCELLED)
     * @param userId the ID of the user updating the status
     * @return true if successful, false otherwise
     */
    boolean updateStatus(int apptId, String status, int userId);
}
