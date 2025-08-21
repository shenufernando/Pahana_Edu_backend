package com.mycompany.servlets;

import com.mycompany.dao.BillingDAO;
import com.mycompany.models.Bill;
import com.mycompany.models.BillItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/BillingServlet")
public class BillingServlet extends HttpServlet {
    
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
        
        System.out.println("=== DEBUG: BillingServlet called ===");
        
        try {
            // Get bill data from request
            String customerId = request.getParameter("customerId");
            String paymentMethod = request.getParameter("paymentMethod");
            String cardName = request.getParameter("cardName");
            String cardNumber = request.getParameter("cardNumber");
            String cvv = request.getParameter("cvv");
            String itemsJson = request.getParameter("items");
            
            System.out.println("Parameters received:");
            System.out.println("Customer ID: " + customerId);
            System.out.println("Payment Method: " + paymentMethod);
            System.out.println("Items: " + itemsJson);
            
            // Validate required fields
            if (customerId == null || customerId.trim().isEmpty() ||
                paymentMethod == null || paymentMethod.trim().isEmpty() ||
                itemsJson == null || itemsJson.trim().isEmpty()) {
                
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Customer, payment method, and items are required");
                out.print(jsonResponse.toString());
                return;
            }
            
            // Parse items
            JSONArray itemsArray = new JSONArray(itemsJson);
            if (itemsArray.length() == 0) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "At least one item is required");
                out.print(jsonResponse.toString());
                return;
            }
            
            // Validate card details if payment method is card
            if ("card".equalsIgnoreCase(paymentMethod)) {
                if (cardName == null || cardName.trim().isEmpty() ||
                    cardNumber == null || cardNumber.trim().isEmpty() ||
                    cvv == null || cvv.trim().isEmpty()) {
                    
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Card details are required for card payments");
                    out.print(jsonResponse.toString());
                    return;
                }
                
                // Basic card validation
                if (!cardNumber.matches("\\d{16,19}")) {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Invalid card number");
                    out.print(jsonResponse.toString());
                    return;
                }
                
                if (!cvv.matches("\\d{3,4}")) {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Invalid CVV");
                    out.print(jsonResponse.toString());
                    return;
                }
            }
            
            // Create bill object
            Bill bill = new Bill();
            bill.setCustomerId(Integer.parseInt(customerId));
            bill.setPaymentMethod(paymentMethod);
            bill.setCardName(cardName);
            bill.setCardNumber(cardNumber);
            bill.setCvv(cvv);
            
            // Add items to bill
            double totalAmount = 0;
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemJson = itemsArray.getJSONObject(i);
                BillItem item = new BillItem();
                item.setBookCode(itemJson.getString("bookCode"));
                item.setQuantity(itemJson.getInt("quantity"));
                item.setUnitPrice(itemJson.getDouble("unitPrice"));
                item.setTotalPrice(itemJson.getDouble("totalPrice"));
                bill.addItem(item);
                totalAmount += item.getTotalPrice();
            }
            
            bill.setTotalAmount(totalAmount);
            
            // Save bill to database
            BillingDAO billingDAO = new BillingDAO();
            boolean success = billingDAO.saveBill(bill);
            
            if (success) {
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Bill generated successfully!");
                jsonResponse.put("billId", bill.getBillId());
                jsonResponse.put("totalAmount", totalAmount);
            } else {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Failed to save bill");
            }
            
            out.print(jsonResponse.toString());
            
        } catch (Exception e) {
            System.out.println("=== ERROR in BillingServlet ===");
            e.printStackTrace();
            System.out.println("=== END ERROR ===");
            
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Server error: " + e.getMessage());
            out.print(jsonResponse.toString());
        } finally {
            out.flush();
            System.out.println("=== DEBUG: BillingServlet completed ===");
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