package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.IUserDAO;
import com.sunrise.dental.dao.impl.UserDAOImpl;
import com.sunrise.dental.factory.UserFactory;
import com.sunrise.dental.model.User;
import com.sunrise.dental.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * RegisterServlet – Handles self-registration of Receptionist and Dentist accounts.
 * New registrations are created with is_active = 0 (pending Admin approval).
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {

    private IUserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String username     = request.getParameter("username");
        String password     = request.getParameter("password");
        String confirmPwd   = request.getParameter("confirmPassword");
        String fullName     = request.getParameter("fullName");
        String roleStr      = request.getParameter("role");
        String email        = request.getParameter("email");
        String contact      = request.getParameter("contact");

        // --- Validation ---
        if (isEmpty(username) || isEmpty(password) || isEmpty(confirmPwd)
                || isEmpty(fullName) || isEmpty(roleStr)) {
            out.print(JsonUtil.createErrorResponse("All required fields must be filled."));
            return;
        }

        if (!password.equals(confirmPwd)) {
            out.print(JsonUtil.createErrorResponse("Passwords do not match."));
            return;
        }

        if (password.length() < 6) {
            out.print(JsonUtil.createErrorResponse("Password must be at least 6 characters."));
            return;
        }

        // Only allow RECEPTIONIST or DENTIST self-registration
        User.Role role;
        try {
            role = User.Role.valueOf(roleStr.toUpperCase());
            if (role == User.Role.ADMIN) {
                out.print(JsonUtil.createErrorResponse("Cannot self-register as Admin."));
                return;
            }
        } catch (IllegalArgumentException e) {
            out.print(JsonUtil.createErrorResponse("Invalid role selected."));
            return;
        }

        if (userDAO.usernameExists(username.trim())) {
            out.print(JsonUtil.createErrorResponse("Username already taken. Please choose another."));
            return;
        }

        // Use UserFactory to create user with hashed password
        User user = UserFactory.createUser(username.trim(), password, fullName.trim(), role,
                                           email != null ? email.trim() : "",
                                           contact != null ? contact.trim() : "");
        // Set inactive — pending Admin approval
        user.setActive(false);

        boolean inserted = userDAO.insert(user);

        if (inserted) {
            out.print("{\"status\":\"success\",\"message\":\"Registration submitted! Your account is pending Admin approval. You will be able to login once approved.\"}");
        } else {
            out.print(JsonUtil.createErrorResponse("Registration failed. Please try again."));
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
