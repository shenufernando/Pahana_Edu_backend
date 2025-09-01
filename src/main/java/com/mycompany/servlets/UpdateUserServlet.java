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

@WebServlet("/UpdateUserServlet")
public class UpdateUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int userId = Integer.parseInt(request.getParameter("userId"));
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE users SET full_name = ?, email = ? WHERE user_Id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setInt(3, userId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                response.getWriter().write("User updated successfully");
            } else {
                response.getWriter().write("Failed to update user");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error updating user: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}