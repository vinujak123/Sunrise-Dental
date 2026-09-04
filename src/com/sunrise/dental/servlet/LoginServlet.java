package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.IUserDAO;
import com.sunrise.dental.dao.impl.UserDAOImpl;
import com.sunrise.dental.model.User;
import com.sunrise.dental.util.JsonUtil;
import com.sunrise.dental.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

/**
 * LoginServlet – Handles user authentication.
 */
@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private IUserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password"); // Plain text from form

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            out.print(JsonUtil.createErrorResponse("Username and password are required."));
            return;
        }

        // Hash the password before checking against DB
        String hashedPwd = PasswordUtil.hash(password);
        
        Optional<User> optUser = userDAO.authenticate(username, hashedPwd);

        if (optUser.isPresent()) {
            User user = optUser.get();
            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            
            // Security Hardening: Expire session after 30 minutes (1800 seconds)
            session.setMaxInactiveInterval(1800);
            
            // Security Hardening: Secure the session cookie
            String sessionCookie = "JSESSIONID=" + session.getId() + "; Path=" + request.getContextPath() + "; HttpOnly; SameSite=Strict; Max-Age=1800";
            // If running on HTTPS, uncomment the next line:
            // sessionCookie += "; Secure";
            response.addHeader("Set-Cookie", sessionCookie);
            
            // Build success JSON
            String json = String.format("{\"status\":\"success\",\"role\":\"%s\",\"name\":\"%s\"}", 
                                        user.getRole().name(), user.getFullName());
            out.print(json);
        } else {
            out.print(JsonUtil.createErrorResponse("Invalid username or password."));
        }
    }
}
