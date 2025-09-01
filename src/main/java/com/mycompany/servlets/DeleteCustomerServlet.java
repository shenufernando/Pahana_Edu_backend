package com.mycompany.servlets;

import com.mycompany.dao.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/DeleteCustomerServlet")
public class DeleteCustomerServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CORS headers
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        try {
            String accountNoStr = request.getParameter("accountNo");
            
            if (accountNoStr == null || accountNoStr.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Account number is required");
                out.print(jsonResponse.toString());
                return;
            }
            
            int accountNo = Integer.parseInt(accountNoStr);
            CustomerDAO customerDAO = new CustomerDAO();
            
            boolean success = customerDAO.deleteCustomer(accountNo);
            
            if (success) {
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Customer deleted successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Customer not found or could not be deleted");
            }
            
            out.print(jsonResponse.toString());
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Invalid account number format");
            out.print(jsonResponse.toString());
        } catch (Exception e) {
            System.out.println("=== ERROR in DeleteCustomerServlet ===");
            e.printStackTrace();
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Server error: " + e.getMessage());
            out.print(jsonResponse.toString());
        } finally {
            out.flush();
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}