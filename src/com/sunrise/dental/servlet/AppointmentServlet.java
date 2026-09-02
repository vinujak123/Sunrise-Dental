package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.IAppointmentDAO;
import com.sunrise.dental.dao.impl.AppointmentDAOImpl;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.User;
import com.sunrise.dental.util.DateUtil;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * AppointmentServlet – Handles booking and listing appointments.
 */
@WebServlet("/api/appointments")
public class AppointmentServlet extends HttpServlet {

    private IAppointmentDAO apptDAO;

    @Override
    public void init() throws ServletException {
        apptDAO = new AppointmentDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String dateStr = request.getParameter("date");
        Date date = (dateStr != null && !dateStr.isEmpty()) ? DateUtil.parseSqlDate(dateStr) : new Date(System.currentTimeMillis());
        
        List<Appointment> list = apptDAO.findByDate(date);
        
        // Build JSON manually
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Appointment a = list.get(i);
            sb.append(String.format("{\"id\":%d,\"number\":\"%s\",\"time\":\"%s\",\"patientName\":\"%s\",\"dentistName\":\"%s\",\"treatment\":\"%s\",\"status\":\"%s\"}",
                      a.getApptId(), a.getApptNumber(), a.getApptTime().toString(), 
                      a.getPatientName(), a.getDentistName(), a.getTreatmentName(), a.getStatus().name()));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        out.print(sb.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            out.print(JsonUtil.createErrorResponse("Unauthorized."));
            return;
        }
        
        User user = (User) session.getAttribute("user");

        try {
            Appointment appt = new Appointment();
            appt.setPatientId(Integer.parseInt(request.getParameter("patientId")));
            appt.setDentistId(Integer.parseInt(request.getParameter("dentistId")));
            appt.setTreatmentId(Integer.parseInt(request.getParameter("treatmentId")));
            appt.setApptDate(DateUtil.parseSqlDate(request.getParameter("apptDate")));
            
            // Format time properly to HH:mm:00
            String timeStr = request.getParameter("apptTime");
            if (timeStr != null && timeStr.length() == 5) timeStr += ":00";
            appt.setApptTime(Time.valueOf(timeStr));
            
            appt.setNotes(request.getParameter("notes"));
            
            String result = apptDAO.bookAppointment(appt, user.getUserId());
            
            if (result != null && result.startsWith("ERROR:")) {
                out.print(JsonUtil.createErrorResponse(result.substring(6)));
            } else if (result != null) {
                out.print(String.format("{\"status\":\"success\",\"message\":\"Appointment Booked: %s\"}", result));
            } else {
                out.print(JsonUtil.createErrorResponse("Unknown error occurred while booking."));
            }
            
        } catch (Exception e) {
            out.print(JsonUtil.createErrorResponse("Invalid input data."));
        }
    }
}
