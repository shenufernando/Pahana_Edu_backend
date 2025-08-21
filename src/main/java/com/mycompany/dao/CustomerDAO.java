package com.mycompany.dao;

import com.mycompany.models.Customer;
import com.mycompany.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    // Add new customer - UPDATED FOR YOUR SCHEMA
    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customer_name, address, phone, units_consumed) VALUES (?, ?, ?, ?)";
        
        System.out.println("=== DEBUG: Starting addCustomer ===");
        System.out.println("Name: " + customer.getName());
        System.out.println("Address: " + customer.getAddress());
        System.out.println("Phone: " + customer.getPhone());
        System.out.println("Units Consumed: " + customer.getUnitsConsumed());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            System.out.println("Database connection successful: " + (conn != null));
            
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPhone());
            
            if (customer.getUnitsConsumed() > 0) {
                stmt.setInt(4, customer.getUnitsConsumed());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            System.out.println("Executing SQL: " + sql);
            int rows = stmt.executeUpdate();
            System.out.println("Rows affected: " + rows);
            
            // Get generated account number
            if (rows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setCustomerId(generatedKeys.getInt(1)); // This will be account_no
                    }
                }
            }
            
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("=== SQL ERROR DETAILS ===");
            System.out.println("Error Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            System.out.println("=== END ERROR DETAILS ===");
            return false;
        } catch (Exception e) {
            System.out.println("=== GENERAL ERROR ===");
            e.printStackTrace();
            System.out.println("=== END ERROR ===");
            return false;
        }
    }
    
    // Get all customers - UPDATED FOR YOUR SCHEMA
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT account_no, customer_name, address, phone, units_consumed FROM customers ORDER BY customer_name";
        
        System.out.println("=== DEBUG: Starting getAllCustomers ===");
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("Database connection successful: " + (conn != null));
            System.out.println("Executing SQL: " + sql);
            
            int count = 0;
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("account_no")); // Using account_no as ID
                customer.setName(rs.getString("customer_name"));
                customer.setAddress(rs.getString("address"));
                customer.setPhone(rs.getString("phone"));
                customer.setUnitsConsumed(rs.getInt("units_consumed"));
                customers.add(customer);
                count++;
            }
            
            System.out.println("Fetched " + count + " customers from database");
            
        } catch (SQLException e) {
            System.out.println("=== SQL ERROR in getAllCustomers ===");
            System.out.println("Error Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            System.out.println("=== END ERROR ===");
        }
        
        System.out.println("Returning " + customers.size() + " customers");
        System.out.println("=== DEBUG: Ending getAllCustomers ===");
        return customers;
    }
    
    // Get customer by account number - UPDATED FOR YOUR SCHEMA
    public Customer getCustomerById(int accountNo) {
        String sql = "SELECT * FROM customers WHERE account_no = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, accountNo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("account_no"));
                    customer.setName(rs.getString("customer_name"));
                    customer.setAddress(rs.getString("address"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setUnitsConsumed(rs.getInt("units_consumed"));
                    return customer;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error fetching customer: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Update customer - UPDATED FOR YOUR SCHEMA
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET customer_name = ?, address = ?, phone = ?, units_consumed = ? WHERE account_no = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPhone());
            
            if (customer.getUnitsConsumed() > 0) {
                stmt.setInt(4, customer.getUnitsConsumed());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setInt(5, customer.getCustomerId()); // account_no
            
            int rows = stmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete customer - UPDATED FOR YOUR SCHEMA
    public boolean deleteCustomer(int accountNo) {
        String sql = "DELETE FROM customers WHERE account_no = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, accountNo);
            int rows = stmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error deleting customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Check if phone number already exists - UPDATED FOR YOUR SCHEMA
    public boolean phoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM customers WHERE phone = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error checking phone: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Get customer by phone number - NEW METHOD
    public Customer getCustomerByPhone(String phone) {
        String sql = "SELECT * FROM customers WHERE phone = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phone);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("account_no"));
                    customer.setName(rs.getString("customer_name"));
                    customer.setAddress(rs.getString("address"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setUnitsConsumed(rs.getInt("units_consumed"));
                    return customer;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error fetching customer by phone: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}