package com.mycompany.servlets;

import com.mycompany.dao.UserDAO;
import com.mycompany.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Add CORS headers
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            // Input validation
            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                
                out.print("{\"status\":\"error\",\"message\":\"Username and password are required\"}");
                return;
            }

            UserDAO userDAO = new UserDAO();
            User user = userDAO.authenticateUser(username.trim(), password.trim());

            if (user != null) {
                // Create session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole());
                
                // Set session timeout to 30 minutes
                session.setMaxInactiveInterval(30 * 60);
                
                // Determine redirect URL based on role
                String redirectUrl = user.getRole().equalsIgnoreCase("admin") 
                    ? "admin_dashboard.html" 
                    : "cashier_dashboard.html";
                
                out.print("{\"status\":\"success\",\"message\":\"Login successful!\",\"role\":\"" + 
                         user.getRole() + "\",\"redirect\":\"" + redirectUrl + "\"}");
            } else {
                out.print("{\"status\":\"error\",\"message\":\"Invalid username or password\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\",\"message\":\"Server error: " + e.getMessage().replace("\"", "\\\"") + "\"}");
        } finally {
            out.flush();
        }
    }
    
    // Handle preflight OPTIONS requests
    @Override
    public void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}