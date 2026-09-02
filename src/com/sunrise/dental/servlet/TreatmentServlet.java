package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.ITreatmentDAO;
import com.sunrise.dental.dao.impl.TreatmentDAOImpl;
import com.sunrise.dental.model.Treatment;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * TreatmentServlet – Returns the list of active treatments as JSON.
 * Used to populate dropdowns in the Appointment booking and Billing forms.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
@WebServlet("/api/treatments")
public class TreatmentServlet extends HttpServlet {

    private ITreatmentDAO treatmentDAO;

    @Override
    public void init() throws ServletException {
        treatmentDAO = new TreatmentDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        List<Treatment> treatments = treatmentDAO.findAllActive();

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < treatments.size(); i++) {
            Treatment t = treatments.get(i);
            sb.append(String.format(
                "{\"id\":%d,\"code\":\"%s\",\"name\":\"%s\",\"category\":\"%s\",\"durationMin\":%d,\"fee\":%.2f}",
                t.getTreatmentId(),
                escapeJson(t.getTreatmentCode()),
                escapeJson(t.getTreatmentName()),
                escapeJson(t.getCategory()),
                t.getDurationMin(),
                t.getFee()
            ));
            if (i < treatments.size() - 1) sb.append(",");
        }
        sb.append("]");

        out.print(sb.toString());
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\"", "\\\"");
    }
}
