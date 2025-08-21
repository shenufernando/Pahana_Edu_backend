package com.mycompany.servlets;

import com.mycompany.dao.CustomerDAO;
import com.mycompany.models.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/AddCustomerServlet")
public class AddCustomerServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CORS headers
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        System.out.println("=== DEBUG: AddCustomerServlet called ===");
        
        try {
            // Get form parameters
            String name = request.getParameter("name");
            String address = request.getParameter("address");
            String phone = request.getParameter("phone");
            String unitsConsumedStr = request.getParameter("unitsConsumed");
            
            System.out.println("Parameters received:");
            System.out.println("Name: " + name);
            System.out.println("Address: " + address);
            System.out.println("Phone: " + phone);
            System.out.println("Units Consumed: " + unitsConsumedStr);
            
            // Validate required fields
            if (name == null || name.trim().isEmpty() ||
                address == null || address.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty()) {
                
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Name, address, and phone are required fields");
                out.print(jsonResponse.toString());
                return;
            }
            
            // Validate phone format (10 digits)
            if (!phone.matches("\\d{10}")) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Phone number must be 10 digits");
                out.print(jsonResponse.toString());
                return;
            }
            
            // Parse units consumed (optional)
            int unitsConsumed = 0;
            if (unitsConsumedStr != null && !unitsConsumedStr.trim().isEmpty()) {
                try {
                    unitsConsumed = Integer.parseInt(unitsConsumedStr);
                    if (unitsConsumed < 0) {
                        jsonResponse.put("status", "error");
                        jsonResponse.put("message", "Units consumed cannot be negative");
                        out.print(jsonResponse.toString());
                        return;
                    }
                } catch (NumberFormatException e) {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Invalid units consumed value");
                    out.print(jsonResponse.toString());
                    return;
                }
            }
            
            // Check if phone already exists
            CustomerDAO customerDAO = new CustomerDAO();
            if (customerDAO.phoneExists(phone)) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Phone number already exists in the system");
                out.print(jsonResponse.toString());
                return;
            }
            
            // Create customer object
            Customer customer = new Customer(name.trim(), address.trim(), phone.trim(), unitsConsumed);
            
            // Add customer to database
            boolean success = customerDAO.addCustomer(customer);
            
            if (success) {
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Customer added successfully! Account Number: " + customer.getCustomerId());
                jsonResponse.put("accountNo", customer.getCustomerId());
                jsonResponse.put("customerId", customer.getCustomerId());
            } else {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Failed to add customer to database");
            }
            
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.out.println("=== ERROR in AddCustomerServlet ===");
            e.printStackTrace();
            System.out.println("=== END ERROR ===");
            
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Server error: " + e.getMessage());
            out.print(jsonResponse.toString());
        } finally {
            out.flush();
            System.out.println("=== DEBUG: AddCustomerServlet completed ===");
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}