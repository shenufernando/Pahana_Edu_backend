package com.mycompany.servlets;

import com.mycompany.dao.BookDAO;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/DeleteBookServlet")
public class DeleteBookServlet extends HttpServlet {
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CORS headers - FIXED: Use * for origin to match GetAllBooksServlet
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            System.out.println("=== DeleteBookServlet: Processing delete request ===");
            
            String codeParam = request.getParameter("code");
            System.out.println("Received book code parameter: " + codeParam);
            
            if (codeParam == null || codeParam.trim().isEmpty()) {
                System.out.println("ERROR: No book code provided");
                JsonObject errorResponse = new JsonObject();
                errorResponse.addProperty("status", "error");
                errorResponse.addProperty("message", "Book code is required");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(errorResponse.toString());
                return;
            }
            
            int bookCode = Integer.parseInt(codeParam.trim());
            System.out.println("Parsed book code: " + bookCode);
            
            BookDAO bookDAO = new BookDAO();
            boolean deleted = bookDAO.deleteBook(bookCode);
            
            System.out.println("Delete operation result: " + deleted);
            
            JsonObject responseObj = new JsonObject();
            if (deleted) {
                responseObj.addProperty("status", "success");
                responseObj.addProperty("message", "Book deleted successfully!");
                System.out.println("=== DeleteBookServlet: Book deleted successfully ===");
            } else {
                responseObj.addProperty("status", "error");
                responseObj.addProperty("message", "Failed to delete book - book may not exist");
                System.out.println("=== DeleteBookServlet: Failed to delete book ===");
            }
            
            out.print(responseObj.toString());
            
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid book code format: " + e.getMessage());
            e.printStackTrace();
            
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("status", "error");
            errorResponse.addProperty("message", "Invalid book code format");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print(errorResponse.toString());
            
        } catch (Exception e) {
            System.out.println("ERROR in DeleteBookServlet: " + e.getMessage());
            e.printStackTrace();
            
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("status", "error");
            errorResponse.addProperty("message", "Server error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(errorResponse.toString());
            
        } finally {
            out.close();
        }
    }
    
    // ADDED: Also handle GET requests for testing
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doDelete(request, response);
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}