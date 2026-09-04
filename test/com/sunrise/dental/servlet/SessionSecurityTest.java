package com.sunrise.dental.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SessionSecurityTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private SessionFilter sessionFilter;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    public void setUp() throws Exception {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    @Test
    public void testSessionFilter_ExpiredSession_ReturnsUnauthorized() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/appointments");
        when(request.getSession(false)).thenReturn(null);
        when(response.getWriter()).thenReturn(printWriter);

        sessionFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
        
        printWriter.flush();
        assertTrue(stringWriter.toString().contains("Session expired or unauthenticated"));
    }

    @Test
    public void testSessionFilter_ValidSession_AllowsAccess() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/appointments");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(new Object()); // Dummy user object

        sessionFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testSessionFilter_PublicEndpoints_AllowedWithoutSession() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/login");

        sessionFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(request, never()).getSession(false);
    }
}
