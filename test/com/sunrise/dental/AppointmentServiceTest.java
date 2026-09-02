package com.sunrise.dental;

import com.sunrise.dental.dao.impl.AppointmentDAOImpl;
import com.sunrise.dental.model.Appointment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Date;
import java.sql.Time;
import static org.junit.jupiter.api.Assertions.*;

/**
 * AppointmentServiceTest – JUnit integration tests for booking logic.
 * Tests double-booking prevention.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class AppointmentServiceTest {

    private AppointmentDAOImpl apptDAO;

    @BeforeEach
    public void setUp() {
        apptDAO = new AppointmentDAOImpl();
    }

    @Test
    public void testBookValidAppointment() {
        Appointment appt = new Appointment();
        appt.setPatientId(1);
        appt.setDentistId(1);
        appt.setTreatmentId(1);
        appt.setApptDate(Date.valueOf("2026-10-01"));
        appt.setApptTime(Time.valueOf("09:00:00"));
        appt.setNotes("Test booking");

        String result = apptDAO.bookAppointment(appt, 1);
        assertNotNull(result);
        assertFalse(result.startsWith("ERROR:"));
        assertTrue(result.startsWith("A-")); // Valid appointment number format
    }

    @Test
    public void testDoubleBookingPrevention() {
        // Assume an appointment already exists at 09:00:00 for dentist 1 on 2026-10-01
        
        Appointment conflictAppt = new Appointment();
        conflictAppt.setPatientId(2);
        conflictAppt.setDentistId(1); // Same dentist
        conflictAppt.setTreatmentId(1); // 30 min duration
        conflictAppt.setApptDate(Date.valueOf("2026-10-01")); // Same date
        conflictAppt.setApptTime(Time.valueOf("09:15:00")); // Overlaps with 09:00-09:30
        conflictAppt.setNotes("Conflicting booking");

        String result = apptDAO.bookAppointment(conflictAppt, 1);
        assertNotNull(result);
        assertTrue(result.startsWith("ERROR:Double booking"), "System should reject double booking");
    }
}
