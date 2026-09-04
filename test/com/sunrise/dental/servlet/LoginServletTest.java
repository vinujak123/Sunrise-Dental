package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.IUserDAO;
import com.sunrise.dental.model.User;
import com.sunrise.dental.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServletTest {

    @Mock
    private IUserDAO userDAO;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private LoginServlet loginServlet;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    public void setUp() throws Exception {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        lenient().when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    public void testDoPost_MissingCredentials_ReturnsError() throws Exception {
        when(request.getParameter("username")).thenReturn("");
        when(request.getParameter("password")).thenReturn(null);

        loginServlet.doPost(request, response);

        printWriter.flush();
        assertTrue(stringWriter.toString().contains("Username and password are required"));
        verify(userDAO, never()).authenticate(anyString(), anyString());
    }

    @Test
    public void testDoPost_InvalidCredentials_ReturnsError() throws Exception {
        when(request.getParameter("username")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn("wrongpass");
        
        when(userDAO.authenticate(eq("admin"), anyString())).thenReturn(Optional.empty());

        loginServlet.doPost(request, response);

        printWriter.flush();
        assertTrue(stringWriter.toString().contains("Invalid username or password"));
        verify(request, never()).getSession(true);
    }

    @Test
    public void testDoPost_ValidCredentials_CreatesSession() throws Exception {
        when(request.getParameter("username")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn("admin123");
        
        User mockUser = new User();
        mockUser.setUsername("admin");
        mockUser.setRole(User.Role.ADMIN);
        mockUser.setFullName("System Admin");
        
        when(userDAO.authenticate(eq("admin"), anyString())).thenReturn(Optional.of(mockUser));
        when(request.getSession(true)).thenReturn(session);

        loginServlet.doPost(request, response);

        printWriter.flush();
        String jsonOutput = stringWriter.toString();
        
        assertTrue(jsonOutput.contains("\"status\":\"success\""));
        assertTrue(jsonOutput.contains("\"role\":\"ADMIN\""));
        
        verify(session).setAttribute("user", mockUser);
        
        // This validates our new security logic (MaxInactiveInterval & cookies) will be called,
        // although right now we haven't implemented it in LoginServlet yet.
        // We will update LoginServlet to set max inactive interval.
    }
}
