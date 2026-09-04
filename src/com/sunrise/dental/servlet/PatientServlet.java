package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.IPatientDAO;
import com.sunrise.dental.dao.INotificationDAO;
import com.sunrise.dental.dao.impl.PatientDAOImpl;
import com.sunrise.dental.dao.impl.NotificationDAOImpl;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.util.DateUtil;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * PatientServlet – Handles patient registration and search.
 */
@WebServlet("/api/patients")
public class PatientServlet extends HttpServlet {

    private IPatientDAO patientDAO;
    private INotificationDAO notificationDAO;

    @Override
    public void init() throws ServletException {
        patientDAO = new PatientDAOImpl();
        notificationDAO = new NotificationDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Simple search API
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String query = request.getParameter("q");
        List<Patient> patients = (query != null && !query.isEmpty()) 
                                 ? patientDAO.searchPatients(query) 
                                 : patientDAO.findAllActive();
                                 
        // Build JSON array manually (no Gson allowed)
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            sb.append(String.format("{\"id\":%d,\"number\":\"%s\",\"name\":\"%s\",\"contact\":\"%s\"}",
                      p.getPatientId(), p.getPatientNumber(), p.getFullName(), p.getContact()));
            if (i < patients.size() - 1) sb.append(",");
        }
        sb.append("]");
        out.print(sb.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Check auth (simplified for basic endpoints)
        if (request.getSession(false) == null || request.getSession(false).getAttribute("user") == null) {
            out.print(JsonUtil.createErrorResponse("Unauthorized. Please log in."));
            return;
        }

        Patient p = new Patient();
        p.setFirstName(request.getParameter("firstName"));
        p.setLastName(request.getParameter("lastName"));
        p.setDateOfBirth(DateUtil.parseSqlDate(request.getParameter("dob")));
        
        String gender = request.getParameter("gender");
        if (gender != null && !gender.isEmpty()) p.setGender(Patient.Gender.valueOf(gender.toUpperCase()));
        
        p.setContact(request.getParameter("contact"));
        p.setEmail(request.getParameter("email"));
        p.setAddress(request.getParameter("address"));
        p.setBloodGroup(request.getParameter("bloodGroup"));
        p.setCity(request.getParameter("city"));
        p.setEmergencyContact(request.getParameter("emergencyContact"));
        p.setMedicalNotes(request.getParameter("medicalNotes"));
        p.setAllergies(request.getParameter("allergies"));

        String patientNumber = patientDAO.registerPatient(p);

        if (patientNumber != null) {
            notificationDAO.createPatientRegisteredNotification(p.getFullName(), patientNumber);
            out.print(String.format("{\"status\":\"success\",\"message\":\"Patient registered successfully.\",\"patientNumber\":\"%s\"}", patientNumber));
        } else {
            out.print(JsonUtil.createErrorResponse("Failed to register patient."));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if (!isAuthenticated(request)) {
            out.print(JsonUtil.createErrorResponse("Unauthorized. Please log in."));
            return;
        }

        try {
            Patient patient = new Patient();
            patient.setPatientId(Integer.parseInt(request.getParameter("patientId")));
            patient.setFirstName(request.getParameter("firstName"));
            patient.setLastName(request.getParameter("lastName"));
            patient.setDateOfBirth(DateUtil.parseSqlDate(request.getParameter("dob")));
            String gender = request.getParameter("gender");
            if (gender != null && !gender.isEmpty()) patient.setGender(Patient.Gender.valueOf(gender.toUpperCase()));
            patient.setBloodGroup(request.getParameter("bloodGroup"));
            patient.setAddress(request.getParameter("address"));
            patient.setCity(request.getParameter("city"));
            patient.setContact(request.getParameter("contact"));
            patient.setEmail(request.getParameter("email"));
            patient.setEmergencyContact(request.getParameter("emergencyContact"));
            patient.setMedicalNotes(request.getParameter("medicalNotes"));
            patient.setAllergies(request.getParameter("allergies"));
            patient.setActive(true);

            out.print(patientDAO.update(patient)
                    ? JsonUtil.createSuccessResponse("Patient updated successfully.")
                    : JsonUtil.createErrorResponse("Failed to update patient."));
        } catch (Exception e) {
            out.print(JsonUtil.createErrorResponse("Invalid patient data."));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if (!isAuthenticated(request)) {
            out.print(JsonUtil.createErrorResponse("Unauthorized. Please log in."));
            return;
        }

        try {
            int patientId = Integer.parseInt(request.getParameter("patientId"));
            java.util.Optional<Patient> patient = patientDAO.findById(patientId);
            if (!patient.isPresent()) {
                out.print(JsonUtil.createErrorResponse("Patient not found."));
                return;
            }
            Patient toDeactivate = patient.get();
            toDeactivate.setActive(false);
            out.print(patientDAO.update(toDeactivate)
                    ? JsonUtil.createSuccessResponse("Patient deactivated successfully.")
                    : JsonUtil.createErrorResponse("Failed to deactivate patient."));
        } catch (Exception e) {
            out.print(JsonUtil.createErrorResponse("Invalid patient id."));
        }
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        return request.getSession(false) != null && request.getSession(false).getAttribute("user") != null;
    }
}
