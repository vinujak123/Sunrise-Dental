package com.sunrise.dental.servlet;

import com.sunrise.dental.util.JsonUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * SessionFilter - Enforces session security by blocking unauthenticated 
 * or expired sessions from accessing protected /api/* routes.
 */
@WebFilter("/api/*")
public class SessionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
        String path = request.getRequestURI();
        
        // Exclude login and register from session checks
        if (path.endsWith("/api/login") || path.endsWith("/api/register")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("user") != null);

        if (loggedIn) {
            chain.doFilter(request, response);
        } else {
            // Session expired or unauthenticated
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print(JsonUtil.createErrorResponse("Session expired or unauthenticated. Please log in again."));
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
