package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.IBillDAO;
import com.sunrise.dental.dao.impl.BillDAOImpl;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.util.DateUtil;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

/**
 * ReportServlet – Generates daily revenue reports.
 */
@WebServlet("/api/reports")
public class ReportServlet extends HttpServlet {

    private IBillDAO billDAO;

    @Override
    public void init() throws ServletException {
        billDAO = new BillDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String dateStr = request.getParameter("date");
        Date date = (dateStr != null && !dateStr.isEmpty()) ? DateUtil.parseSqlDate(dateStr) : new Date(System.currentTimeMillis());
        
        double dailyRevenue = billDAO.getDailyRevenue(date);
        List<Bill> bills = billDAO.findBillsByDate(date);
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(String.format("\"date\":\"%s\",", date.toString()));
        sb.append(String.format("\"totalRevenue\":%.2f,", dailyRevenue));
        sb.append("\"bills\":[");
        
        for (int i = 0; i < bills.size(); i++) {
            Bill b = bills.get(i);
            sb.append(String.format("{\"number\":\"%s\",\"patient\":\"%s\",\"dentist\":\"%s\",\"total\":%.2f,\"status\":\"%s\"}",
                      b.getBillNumber(), b.getPatientName(), b.getDentistName(), b.getTotalAmount(), b.getPaymentStatus().name()));
            if (i < bills.size() - 1) sb.append(",");
        }
        
        sb.append("]}");
        out.print(sb.toString());
    }
}
