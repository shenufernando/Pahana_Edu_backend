package com.mycompany.servlets;

import com.mycompany.dao.UserDAO;
import com.mycompany.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SignupServletTest {

    private SignupServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        servlet = new SignupServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void testDoPost_SuccessfulRegistration() throws Exception {
        // Mock request parameters
        when(request.getParameter("fullName")).thenReturn("Alice");
        when(request.getParameter("email")).thenReturn("alice@example.com");
        when(request.getParameter("username")).thenReturn("alice123");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("role")).thenReturn("user");

        // Capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        when(response.getWriter()).thenReturn(writer);

        // Mock UserDAO construction and registerUser behavior
        try (MockedConstruction<UserDAO> mockedDAO = mockConstruction(UserDAO.class,
                (mock, context) -> when(mock.registerUser(any(User.class))).thenReturn(true))) {

            servlet.doPost(request, response);

            String jsonOutput = outputStream.toString();
            assertTrue(jsonOutput.contains("\"status\":\"success\""));
            assertTrue(jsonOutput.contains("User registered successfully"));
        }
    }

    @Test
    void testDoPost_MissingFields() throws Exception {
        // Missing email parameter
        when(request.getParameter("fullName")).thenReturn("Alice");
        when(request.getParameter("email")).thenReturn(null);
        when(request.getParameter("username")).thenReturn("alice123");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("role")).thenReturn("user");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        String jsonOutput = outputStream.toString();
        assertTrue(jsonOutput.contains("\"status\":\"error\""));
        assertTrue(jsonOutput.contains("All fields are required"));
    }

    @Test
    void testDoPost_RegistrationFails() throws Exception {
        // Mock request parameters
        when(request.getParameter("fullName")).thenReturn("Alice");
        when(request.getParameter("email")).thenReturn("alice@example.com");
        when(request.getParameter("username")).thenReturn("alice123");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("role")).thenReturn("user");

        // Capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        when(response.getWriter()).thenReturn(writer);

        // Mock UserDAO construction to return false (registration fails)
        try (MockedConstruction<UserDAO> mockedDAO = mockConstruction(UserDAO.class,
                (mock, context) -> when(mock.registerUser(any(User.class))).thenReturn(false))) {

            servlet.doPost(request, response);

            String jsonOutput = outputStream.toString();
            assertTrue(jsonOutput.contains("\"status\":\"error\""));
            assertTrue(jsonOutput.contains("Failed to register user"));
        }
    }
}
