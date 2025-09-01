package com.mycompany.servlets;

import com.mycompany.dao.BillingDAO;
import com.mycompany.models.Bill;
import com.mycompany.models.BillItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.mycompany.utils.DBConnection;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/GetBillsServlet")
public class GetBillsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        BillingDAO billingDAO = new BillingDAO();
        List<Bill> bills = billingDAO.getAllBills();
        JSONArray billsArray = new JSONArray();

        try (Connection conn = DBConnection.getConnection()) {
            for (Bill bill : bills) {
                JSONObject billJson = new JSONObject();
                billJson.put("billId", bill.getBillId());
                billJson.put("customerId", bill.getCustomerId());
                billJson.put("paymentMethod", bill.getPaymentMethod() != null ? bill.getPaymentMethod() : "N/A");
                // Ensure totalAmount is always included, even if 0.0
                double totalAmount = bill.getTotalAmount();
                billJson.put("totalAmount", totalAmount);
                
                // Log bill details for debugging
                System.out.println("Bill ID: " + bill.getBillId() + ", Customer ID: " + bill.getCustomerId() + 
                                   ", Total Amount: " + totalAmount + ", Payment Method: " + bill.getPaymentMethod());

                // Fetch customer name
                String customerSql = "SELECT customer_name FROM customers WHERE account_no = ?";
                try (PreparedStatement stmt = conn.prepareStatement(customerSql)) {
                    stmt.setInt(1, bill.getCustomerId());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            billJson.put("customerName", rs.getString("customer_name"));
                        } else {
                            billJson.put("customerName", "Unknown");
                            System.out.println("No customer found for customer_id: " + bill.getCustomerId());
                        }
                    }
                }

                // Fetch bill items
                String itemsSql = "SELECT bi.*, b.book_title FROM bill_items bi " +
                                "JOIN books b ON bi.book_code = b.book_code WHERE bi.bill_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(itemsSql)) {
                    stmt.setInt(1, bill.getBillId());
                    try (ResultSet rs = stmt.executeQuery()) {
                        JSONArray itemsArray = new JSONArray();
                        while (rs.next()) {
                            JSONObject itemJson = new JSONObject();
                            itemJson.put("bookTitle", rs.getString("book_title"));
                            itemJson.put("quantity", rs.getInt("quantity"));
                            itemsArray.put(itemJson);
                        }
                        billJson.put("items", itemsArray);
                    }
                }

                // Fetch bill creation date
                String dateSql = "SELECT created_at FROM bills WHERE bill_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(dateSql)) {
                    stmt.setInt(1, bill.getBillId());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            billJson.put("createdAt", rs.getTimestamp("created_at") != null 
                                ? rs.getTimestamp("created_at").toString() : "");
                        } else {
                            billJson.put("createdAt", "");
                            System.out.println("No created_at found for bill_id: " + bill.getBillId());
                        }
                    }
                }

                billsArray.put(billJson);
            }

            // Log final JSON response
            System.out.println("JSON Response: " + billsArray.toString());
            out.print(billsArray.toString());
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error fetching bills: " + e.getMessage() + "\"}");
            out.flush();
        }
    }
}