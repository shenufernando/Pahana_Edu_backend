package com.mycompany.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.mycompany.utils.DBConnection;
import org.json.JSONObject;

@WebServlet("/DeleteUserServlet")
public class DeleteUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        
        JSONObject jsonResponse = new JSONObject();
        
        try {
            String email = request.getParameter("email");
            
            if (email == null || email.trim().isEmpty()) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Email parameter is required");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(jsonResponse.toString());
                return;
            }
            
            Connection conn = null;
            PreparedStatement stmt = null;
            
            try {
                conn = DBConnection.getConnection();
                String sql = "DELETE FROM users WHERE email = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, email.trim());
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "User deleted successfully");
                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "User not found with email: " + email);
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Database error: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Server error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        // Send JSON response
        response.getWriter().write(jsonResponse.toString());
    }

    // Add CORS support for OPTIONS requests
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}