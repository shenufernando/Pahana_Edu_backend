package com.mycompany.servlets;

import com.mycompany.dao.CustomerDAO;
import com.mycompany.models.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/UpdateCustomerServlet")
public class UpdateCustomerServlet extends HttpServlet {
    
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
        
        try {
            // Get form parameters
            String accountNoStr = request.getParameter("accountNo");
            String name = request.getParameter("name");
            String address = request.getParameter("address");
            String phone = request.getParameter("phone");
            String unitsConsumedStr = request.getParameter("unitsConsumed");
            
            // Validate required fields
            if (accountNoStr == null || accountNoStr.trim().isEmpty() ||
                name == null || name.trim().isEmpty() ||
                address == null || address.trim().isEmpty() ||
                phone == null || phone.trim().isEmpty()) {
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "All required fields must be filled");
                out.print(jsonResponse.toString());
                return;
            }
            
            int accountNo = Integer.parseInt(accountNoStr);
            
            // Validate phone format (10 digits)
            if (!phone.matches("\\d{10}")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Phone number must be 10 digits");
                out.print(jsonResponse.toString());
                return;
            }
            
            // Parse units consumed (optional)
            Integer unitsConsumed = null;
            if (unitsConsumedStr != null && !unitsConsumedStr.trim().isEmpty()) {
                try {
                    unitsConsumed = Integer.parseInt(unitsConsumedStr);
                    if (unitsConsumed < 0) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        jsonResponse.put("status", "error");
                        jsonResponse.put("message", "Units consumed cannot be negative");
                        out.print(jsonResponse.toString());
                        return;
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Invalid units consumed value");
                    out.print(jsonResponse.toString());
                    return;
                }
            }
            
            // Check if phone already exists (excluding current customer)
            CustomerDAO customerDAO = new CustomerDAO();
            if (customerDAO.phoneExists(phone, accountNo)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Phone number already exists for another customer");
                out.print(jsonResponse.toString());
                return;
            }
            
            // Create customer object
            Customer customer = new Customer();
            customer.setAccountNo(accountNo);
            customer.setCustomerName(name.trim());
            customer.setAddress(address.trim());
            customer.setPhone(phone.trim());
            customer.setUnitsConsumed(unitsConsumed);
            
            // Update customer in database
            boolean success = customerDAO.updateCustomer(customer);
            
            if (success) {
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Customer updated successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Customer not found or could not be updated");
            }
            
            out.print(jsonResponse.toString());
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Invalid account number format");
            out.print(jsonResponse.toString());
        } catch (Exception e) {
            System.out.println("=== ERROR in UpdateCustomerServlet ===");
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
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}