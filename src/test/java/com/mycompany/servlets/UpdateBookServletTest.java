package com.mycompany.servlets;

import com.mycompany.dao.BookDAO;
import com.mycompany.models.Book;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UpdateBookServletTest {

    private UpdateBookServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        servlet = new UpdateBookServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void testDoPost_SuccessfulUpdate_WithImage() throws Exception {
        // Mock request parameters
        when(request.getParameter("bookCode")).thenReturn("1");
        when(request.getParameter("bookTitle")).thenReturn("Java Basics");
        when(request.getParameter("bookCategory")).thenReturn("Programming");
        when(request.getParameter("price")).thenReturn("29.99");
        when(request.getParameter("quantity")).thenReturn("10");

        // Mock file upload
        byte[] fakeImage = new byte[]{1, 2, 3, 4};
        Part filePart = mock(Part.class);
        when(filePart.getSize()).thenReturn((long) fakeImage.length);
        when(filePart.getInputStream()).thenReturn(new ByteArrayInputStream(fakeImage));
        when(request.getPart("bookImage")).thenReturn(filePart);

        // Capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        when(response.getWriter()).thenReturn(writer);

        // Mock BookDAO construction
        try (MockedConstruction<BookDAO> mockedDAO = mockConstruction(BookDAO.class,
                (mock, context) -> when(mock.updateBook(any(Book.class))).thenReturn(true))) {

            servlet.doPost(request, response);

            String jsonOutput = outputStream.toString();
            assertTrue(jsonOutput.contains("\"status\":\"success\""));
            assertTrue(jsonOutput.contains("Book updated successfully"));
        }
    }

    @Test
    void testDoPost_FailedUpdate_NoImage() throws Exception {
        // Mock request parameters
        when(request.getParameter("bookCode")).thenReturn("2");
        when(request.getParameter("bookTitle")).thenReturn("Python Basics");
        when(request.getParameter("bookCategory")).thenReturn("Programming");
        when(request.getParameter("price")).thenReturn("39.99");
        when(request.getParameter("quantity")).thenReturn("5");

        // No file uploaded
        when(request.getPart("bookImage")).thenReturn(null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        when(response.getWriter()).thenReturn(writer);

        try (MockedConstruction<BookDAO> mockedDAO = mockConstruction(BookDAO.class,
                (mock, context) -> when(mock.updateBook(any(Book.class))).thenReturn(false))) {

            servlet.doPost(request, response);

            String jsonOutput = outputStream.toString();
            assertTrue(jsonOutput.contains("\"status\":\"error\""));
            assertTrue(jsonOutput.contains("Failed to update book"));
        }
    }

    @Test
    void testDoPost_ServerError_InvalidPrice() throws Exception {
        when(request.getParameter("bookCode")).thenReturn("3");
        when(request.getParameter("bookTitle")).thenReturn("C++ Basics");
        when(request.getParameter("bookCategory")).thenReturn("Programming");
        when(request.getParameter("price")).thenReturn("invalid_price");
        when(request.getParameter("quantity")).thenReturn("5");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        String jsonOutput = outputStream.toString();
        assertTrue(jsonOutput.contains("\"status\":\"error\""));
        assertTrue(jsonOutput.contains("Server error"));
    }
}
