package com.mycompany.dao;

import com.mycompany.models.Bill;
import com.mycompany.models.BillItem;
import com.mycompany.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillingDAO {
    
    // Save bill and bill items to database
    public boolean saveBill(Bill bill) {
        Connection conn = null;
        PreparedStatement billStmt = null;
        PreparedStatement itemStmt = null;
        
        System.out.println("=== DEBUG: Starting saveBill ===");
        System.out.println("Customer ID: " + bill.getCustomerId());
        System.out.println("Payment Method: " + bill.getPaymentMethod());
        System.out.println("Total Amount: " + bill.getTotalAmount());
        System.out.println("Items count: " + bill.getItems().size());
        
        for (BillItem item : bill.getItems()) {
            System.out.println("Item: " + item.getBookCode() + " x " + item.getQuantity() + 
                              " = Rs. " + item.getTotalPrice());
        }
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // 1. Insert into bills table
            String billSql = "INSERT INTO bills (customer_id, payment_method, card_name, card_number, cvv, total_amount, created_at) " +
                           "VALUES (?, ?, ?, ?, ?, ?, NOW())";
            
            billStmt = conn.prepareStatement(billSql, Statement.RETURN_GENERATED_KEYS);
            billStmt.setInt(1, bill.getCustomerId());
            billStmt.setString(2, bill.getPaymentMethod());
            billStmt.setString(3, bill.getCardName());
            billStmt.setString(4, bill.getCardNumber());
            billStmt.setString(5, bill.getCvv());
            billStmt.setDouble(6, bill.getTotalAmount());
            
            System.out.println("Executing bill SQL: " + billSql);
            int billRows = billStmt.executeUpdate();
            
            if (billRows == 0) {
                throw new SQLException("Creating bill failed, no rows affected.");
            }
            
            // Get generated bill ID
            int billId;
            try (ResultSet generatedKeys = billStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    billId = generatedKeys.getInt(1);
                    bill.setBillId(billId);
                    System.out.println("Generated Bill ID: " + billId);
                } else {
                    throw new SQLException("Creating bill failed, no ID obtained.");
                }
            }
            
            // 2. Insert into bill_items table
            String itemSql = "INSERT INTO bill_items (bill_id, book_code, quantity, unit_price, total_price) " +
                           "VALUES (?, ?, ?, ?, ?)";
            
            itemStmt = conn.prepareStatement(itemSql);
            
            for (BillItem item : bill.getItems()) {
                itemStmt.setInt(1, billId);
                // FIX: Convert bookCode from String to int for zerofill INT
                itemStmt.setInt(2, Integer.parseInt(item.getBookCode()));
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getUnitPrice());
                itemStmt.setDouble(5, item.getTotalPrice());
                itemStmt.addBatch();
                
                System.out.println("Adding item: " + item.getBookCode() + " x " + item.getQuantity());
            }
            
            System.out.println("Executing items batch");
            int[] itemRows = itemStmt.executeBatch();
            System.out.println("Items inserted: " + itemRows.length);
            
            // 3. Update book quantities (reduce stock)
            updateBookQuantities(conn, bill.getItems());
            
            conn.commit(); // Commit transaction
            System.out.println("Transaction committed successfully");
            return true;
            
        } catch (SQLException e) {
            System.out.println("=== SQL ERROR in saveBill ===");
            System.out.println("Error Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back");
                } catch (SQLException ex) {
                    System.out.println("Rollback failed: " + ex.getMessage());
                }
            }
            e.printStackTrace();
            return false;
            
        } catch (Exception e) {
            System.out.println("=== GENERAL ERROR in saveBill ===");
            e.printStackTrace();
            
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back");
                } catch (SQLException ex) {
                    System.out.println("Rollback failed: " + ex.getMessage());
                }
            }
            return false;
            
        } finally {
            // Close resources
            try {
                if (itemStmt != null) itemStmt.close();
                if (billStmt != null) billStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
            System.out.println("=== DEBUG: Ending saveBill ===");
        }
    }
    
    // Update book quantities after sale
    private void updateBookQuantities(Connection conn, List<BillItem> items) throws SQLException {
        String sql = "UPDATE books SET available_quantity = available_quantity - ? WHERE book_code = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (BillItem item : items) {
                stmt.setInt(1, item.getQuantity());
                // FIX: Convert bookCode from String to int for zerofill INT
                stmt.setInt(2, Integer.parseInt(item.getBookCode()));
                stmt.addBatch();
                System.out.println("Updating stock: " + item.getBookCode() + " -" + item.getQuantity());
            }
            
            int[] updateCounts = stmt.executeBatch();
            System.out.println("Stock updates completed: " + updateCounts.length);
        }
    }
    
    // Get all bills
    public List<Bill> getAllBills() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT b.*, c.customer_name, c.phone " +
                   "FROM bills b " +
                   "JOIN customers c ON b.customer_id = c.account_no " +
                   "ORDER BY b.created_at DESC";
        
        System.out.println("=== DEBUG: Starting getAllBills ===");
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("Executing SQL: " + sql);
            
            while (rs.next()) {
                Bill bill = new Bill();
                bill.setBillId(rs.getInt("bill_id"));
                bill.setCustomerId(rs.getInt("customer_id"));
                bill.setPaymentMethod(rs.getString("payment_method"));
                bill.setCardName(rs.getString("card_name"));
                bill.setCardNumber(rs.getString("card_number"));
                bill.setTotalAmount(rs.getDouble("total_amount"));
                
                bills.add(bill);
            }
            
            System.out.println("Fetched " + bills.size() + " bills");
            
        } catch (SQLException e) {
            System.out.println("=== SQL ERROR in getAllBills ===");
            System.out.println("Error Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
        
        System.out.println("=== DEBUG: Ending getAllBills ===");
        return bills;
    }
    
    // Get bill by ID with items
    public Bill getBillById(int billId) {
        Bill bill = null;
        String billSql = "SELECT b.*, c.customer_name, c.address, c.phone " +
                       "FROM bills b " +
                       "JOIN customers c ON b.customer_id = c.account_no " +
                       "WHERE b.bill_id = ?";
        
        String itemsSql = "SELECT bi.*, b.book_title " +
                        "FROM bill_items bi " +
                        "JOIN books b ON bi.book_code = b.book_code " +
                        "WHERE bi.bill_id = ?";
        
        System.out.println("=== DEBUG: Starting getBillById ===");
        System.out.println("Bill ID: " + billId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement billStmt = conn.prepareStatement(billSql);
             PreparedStatement itemsStmt = conn.prepareStatement(itemsSql)) {
            
            billStmt.setInt(1, billId);
            try (ResultSet billRs = billStmt.executeQuery()) {
                if (billRs.next()) {
                    bill = new Bill();
                    bill.setBillId(billRs.getInt("bill_id"));
                    bill.setCustomerId(billRs.getInt("customer_id"));
                    bill.setPaymentMethod(billRs.getString("payment_method"));
                    bill.setCardName(billRs.getString("card_name"));
                    bill.setCardNumber(billRs.getString("card_number"));
                    bill.setTotalAmount(billRs.getDouble("total_amount"));
                    bill.setCvv(billRs.getString("cvv"));
                    
                    // Get bill items
                    itemsStmt.setInt(1, billId);
                    try (ResultSet itemsRs = itemsStmt.executeQuery()) {
                        List<BillItem> items = new ArrayList<>();
                        while (itemsRs.next()) {
                            BillItem item = new BillItem();
                            // FIX: Convert int back to String with leading zeros
                            String bookCodeStr = String.format("%010d", itemsRs.getInt("book_code"));
                            item.setBookCode(bookCodeStr);
                            item.setQuantity(itemsRs.getInt("quantity"));
                            item.setUnitPrice(itemsRs.getDouble("unit_price"));
                            item.setTotalPrice(itemsRs.getDouble("total_price"));
                            items.add(item);
                        }
                        bill.setItems(items);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.out.println("=== SQL ERROR in getBillById ===");
            System.out.println("Error Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
        
        System.out.println("=== DEBUG: Ending getBillById ===");
        return bill;
    }
    
    // Get bills by customer
    public List<Bill> getBillsByCustomer(int customerId) {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE customer_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Bill bill = new Bill();
                    bill.setBillId(rs.getInt("bill_id"));
                    bill.setCustomerId(rs.getInt("customer_id"));
                    bill.setPaymentMethod(rs.getString("payment_method"));
                    bill.setTotalAmount(rs.getDouble("total_amount"));
                    bills.add(bill);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error fetching customer bills: " + e.getMessage());
            e.printStackTrace();
        }
        return bills;
    }
    
    // Delete bill (with transaction)
    public boolean deleteBill(int billId) {
        Connection conn = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // First delete bill items
            String deleteItemsSql = "DELETE FROM bill_items WHERE bill_id = ?";
            try (PreparedStatement itemsStmt = conn.prepareStatement(deleteItemsSql)) {
                itemsStmt.setInt(1, billId);
                itemsStmt.executeUpdate();
            }
            
            // Then delete bill
            String deleteBillSql = "DELETE FROM bills WHERE bill_id = ?";
            try (PreparedStatement billStmt = conn.prepareStatement(deleteBillSql)) {
                billStmt.setInt(1, billId);
                int rows = billStmt.executeUpdate();
                conn.commit();
                return rows > 0;
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Rollback failed: " + ex.getMessage());
                }
            }
            System.out.println("Error deleting bill: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    // Get all unique book categories
    public List<String> getBookCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT book_category FROM books ORDER BY book_category";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(rs.getString("book_category"));
            }
            
        } catch (SQLException e) {
            System.out.println("Error fetching categories: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }
}