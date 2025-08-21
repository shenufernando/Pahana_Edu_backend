package com.mycompany.servlets;

import com.mycompany.dao.CustomerDAO;
import com.mycompany.models.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/GetCustomersServlet")
public class GetCustomersServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CORS headers
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            List<Customer> customers = customerDAO.getAllCustomers();
            
            JSONArray customersArray = new JSONArray();
            for (Customer customer : customers) {
                JSONObject customerJson = new JSONObject();
                customerJson.put("accountNo", customer.getCustomerId()); // This is account_no
                customerJson.put("name", customer.getName());
                customerJson.put("phone", customer.getPhone());
                customersArray.put(customerJson);
            }
            
            jsonResponse.put("status", "success");
            jsonResponse.put("customers", customersArray);
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.out.println("=== ERROR in GetCustomersServlet ===");
            e.printStackTrace();
            
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
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
