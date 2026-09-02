package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.IDentistDAO;
import com.sunrise.dental.dao.impl.DentistDAOImpl;
import com.sunrise.dental.model.Dentist;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * DentistServlet – Returns the list of active dentists as JSON.
 * Used to populate dropdowns in the Appointment booking form.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
@WebServlet("/api/dentists")
public class DentistServlet extends HttpServlet {

    private IDentistDAO dentistDAO;

    @Override
    public void init() throws ServletException {
        dentistDAO = new DentistDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        List<Dentist> dentists = dentistDAO.findAllActive();

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < dentists.size(); i++) {
            Dentist d = dentists.get(i);
            sb.append(String.format(
                "{\"id\":%d,\"name\":\"%s\",\"specialization\":\"%s\",\"availableDays\":\"%s\"}",
                d.getDentistId(),
                escapeJson(d.getName()),
                escapeJson(d.getSpecialization()),
                d.getAvailableDays() != null ? d.getAvailableDays() : ""
            ));
            if (i < dentists.size() - 1) sb.append(",");
        }
        sb.append("]");

        out.print(sb.toString());
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\"", "\\\"");
    }
}
