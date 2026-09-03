package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.IUserDAO;
import com.sunrise.dental.dao.impl.UserDAOImpl;
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
import java.util.List;

/**
 * AdminServlet – Admin-only endpoint for user management.
 * Handles listing pending users, approving and rejecting accounts.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
@WebServlet("/api/admin/users")
public class AdminServlet extends HttpServlet {

    private IUserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
    }

    /**
     * GET /api/admin/users — returns list of pending users awaiting approval.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (!isAdmin(request)) {
            out.print(JsonUtil.createErrorResponse("Unauthorized. Admin access required."));
            return;
        }

        List<User> pending = userDAO.findPendingUsers();

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < pending.size(); i++) {
            User u = pending.get(i);
            sb.append(String.format(
                "{\"id\":%d,\"username\":\"%s\",\"fullName\":\"%s\",\"role\":\"%s\",\"email\":\"%s\",\"contact\":\"%s\",\"createdAt\":\"%s\"}",
                u.getUserId(),
                escapeJson(u.getUsername()),
                escapeJson(u.getFullName()),
                u.getRole().name(),
                u.getEmail() != null ? escapeJson(u.getEmail()) : "",
                u.getContact() != null ? escapeJson(u.getContact()) : "",
                u.getCreatedAt() != null ? u.getCreatedAt().toString() : ""
            ));
            if (i < pending.size() - 1) sb.append(",");
        }
        sb.append("]");
        out.print(sb.toString());
    }

    /**
     * POST /api/admin/users — approve or reject a pending user.
     * Params: action=approve|reject, userId=<id>
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (!isAdmin(request)) {
            out.print(JsonUtil.createErrorResponse("Unauthorized. Admin access required."));
            return;
        }

        String action = request.getParameter("action");
        String userIdStr = request.getParameter("userId");

        if (action == null || userIdStr == null) {
            out.print(JsonUtil.createErrorResponse("Missing action or userId parameter."));
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);

            if ("approve".equals(action)) {
                boolean ok = userDAO.approve(userId);
                if (ok) {
                    out.print(JsonUtil.createSuccessResponse("User account approved successfully."));
                } else {
                    out.print(JsonUtil.createErrorResponse("Failed to approve user."));
                }
            } else if ("reject".equals(action)) {
                boolean ok = userDAO.reject(userId);
                if (ok) {
                    out.print(JsonUtil.createSuccessResponse("User account rejected and removed."));
                } else {
                    out.print(JsonUtil.createErrorResponse("Failed to reject user (may already be active)."));
                }
            } else {
                out.print(JsonUtil.createErrorResponse("Unknown action: " + action));
            }
        } catch (NumberFormatException e) {
            out.print(JsonUtil.createErrorResponse("Invalid userId."));
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        User user = (User) session.getAttribute("user");
        return user != null && user.getRole() == User.Role.ADMIN;
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
