package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.IBillDAO;
import com.sunrise.dental.dao.impl.BillDAOImpl;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.model.User;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * BillingServlet – Handles bill generation and payments.
 */
@WebServlet("/api/billing")
public class BillingServlet extends HttpServlet {

    private IBillDAO billDAO;

    @Override
    public void init() throws ServletException {
        billDAO = new BillDAOImpl();
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

        String action = request.getParameter("action");
        
        if ("generate".equals(action)) {
            try {
                Bill bill = new Bill();
                bill.setApptId(Integer.parseInt(request.getParameter("apptId")));
                
                String consultFee = request.getParameter("consultationFee");
                if (consultFee != null && !consultFee.isEmpty()) {
                    bill.setConsultationFee(Double.parseDouble(consultFee));
                }
                
                String discount = request.getParameter("discountPercent");
                if (discount != null && !discount.isEmpty()) {
                    bill.setDiscountPercent(Double.parseDouble(discount));
                }
                
                String method = request.getParameter("paymentMethod");
                if (method != null && !method.isEmpty()) {
                    bill.setPaymentMethod(Bill.PaymentMethod.valueOf(method));
                }
                
                bill.setGeneratedBy(user.getUserId());
                
                String result = billDAO.generateBill(bill);
                
                if (result != null) {
                    out.print(String.format("{\"status\":\"success\",\"message\":\"Bill Generated: %s\"}", result));
                } else {
                    out.print(JsonUtil.createErrorResponse("Failed to generate bill (might already exist)."));
                }
            } catch (Exception e) {
                out.print(JsonUtil.createErrorResponse("Invalid input."));
            }
        } else if ("pay".equals(action)) {
            try {
                int billId = Integer.parseInt(request.getParameter("billId"));
                boolean success = billDAO.updatePaymentStatus(billId, "PAID");
                if (success) {
                    out.print(JsonUtil.createSuccessResponse("Payment recorded successfully."));
                } else {
                    out.print(JsonUtil.createErrorResponse("Failed to update payment status."));
                }
            } catch (Exception e) {
                 out.print(JsonUtil.createErrorResponse("Invalid input."));
            }
        }
    }
}
