package com.mycompany.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.mycompany.utils.DBConnection;
import com.mycompany.models.User;

@WebServlet("/GetUserServlet")
public class GetUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT user_Id, full_name, email, role FROM users";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            // Build JSON manually
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("[");
            
            boolean first = true;
            while (rs.next()) {
                if (!first) {
                    jsonBuilder.append(",");
                }
                first = false;
                
                jsonBuilder.append("{")
                    .append("\"userId\":").append(rs.getInt("user_Id")).append(",")
                    .append("\"fullName\":\"").append(escapeJson(rs.getString("full_name"))).append("\",")
                    .append("\"email\":\"").append(escapeJson(rs.getString("email"))).append("\",")
                    .append("\"role\":\"").append(escapeJson(rs.getString("role"))).append("\"")
                    .append("}");
            }
            
            jsonBuilder.append("]");
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonBuilder.toString());
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error retrieving users: " + escapeJson(e.getMessage()) + "\"}");
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Helper method to escape JSON strings
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}