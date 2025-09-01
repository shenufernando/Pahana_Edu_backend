package com.mycompany.dao;

import com.mycompany.models.Customer;
import com.mycompany.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    // SQL queries
    private static final String INSERT_CUSTOMER_SQL = "INSERT INTO customers (customer_name, address, phone, units_consumed) VALUES (?, ?, ?, ?)";
    private static final String SELECT_CUSTOMER_BY_ID = "SELECT * FROM customers WHERE account_no = ?";
    private static final String SELECT_ALL_CUSTOMERS = "SELECT * FROM customers ORDER BY account_no";
    private static final String SELECT_CUSTOMERS_BY_NAME = "SELECT * FROM customers WHERE customer_name LIKE ? ORDER BY account_no";
    private static final String UPDATE_CUSTOMER_SQL = "UPDATE customers SET customer_name = ?, address = ?, phone = ?, units_consumed = ? WHERE account_no = ?";
    private static final String DELETE_CUSTOMER_SQL = "DELETE FROM customers WHERE account_no = ?";
    private static final String CHECK_PHONE_EXISTS = "SELECT COUNT(*) FROM customers WHERE phone = ?";
    private static final String CHECK_PHONE_EXISTS_EXCLUDE_ID = "SELECT COUNT(*) FROM customers WHERE phone = ? AND account_no != ?";
    private static final String COUNT_CUSTOMERS_SQL = "SELECT COUNT(*) FROM customers";

    // Use your existing DBConnection utility
    private Connection getConnection() {
        return DBConnection.getConnection();
    }

    // Add customer method
    public boolean addCustomer(Customer customer) {
        boolean success = false;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CUSTOMER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            setCustomerParameters(preparedStatement, customer);
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                setGeneratedAccountNo(preparedStatement, customer);
                success = true;
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return success;
    }

    // Get customer by ID
    public Customer getCustomerById(int accountNo) {
        Customer customer = null;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CUSTOMER_BY_ID)) {
            
            preparedStatement.setInt(1, accountNo);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    customer = extractCustomerFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return customer;
    }

    // Get all customers
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_CUSTOMERS);
             ResultSet rs = preparedStatement.executeQuery()) {
            
            while (rs.next()) {
                Customer customer = extractCustomerFromResultSet(rs);
                customers.add(customer);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return customers;
    }

    // Search customers by name
    public List<Customer> getCustomersByName(String namePattern) {
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CUSTOMERS_BY_NAME)) {
            
            preparedStatement.setString(1, "%" + namePattern + "%");
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Customer customer = extractCustomerFromResultSet(rs);
                    customers.add(customer);
                }
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return customers;
    }

    // Update customer
    public boolean updateCustomer(Customer customer) {
        boolean success = false;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CUSTOMER_SQL)) {
            
            setCustomerParameters(preparedStatement, customer);
            preparedStatement.setInt(5, customer.getAccountNo());
            
            int rowsAffected = preparedStatement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            printSQLException(e);
        }
        return success;
    }

    // Delete customer
    public boolean deleteCustomer(int accountNo) {
        boolean success = false;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CUSTOMER_SQL)) {
            
            preparedStatement.setInt(1, accountNo);
            int rowsAffected = preparedStatement.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            printSQLException(e);
        }
        return success;
    }

    // Phone exists check method
    public boolean phoneExists(String phone) {
        boolean exists = false;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CHECK_PHONE_EXISTS)) {
            
            preparedStatement.setString(1, phone);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return exists;
    }

    // Phone exists check excluding a specific customer (for updates)
    public boolean phoneExists(String phone, int excludeAccountNo) {
        boolean exists = false;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CHECK_PHONE_EXISTS_EXCLUDE_ID)) {
            
            preparedStatement.setString(1, phone);
            preparedStatement.setInt(2, excludeAccountNo);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return exists;
    }

    // Get total customer count
    public int getCustomerCount() {
        int count = 0;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_CUSTOMERS_SQL);
             ResultSet rs = preparedStatement.executeQuery()) {
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return count;
    }

    // Helper method to set customer parameters in PreparedStatement
    private void setCustomerParameters(PreparedStatement preparedStatement, Customer customer) 
            throws SQLException {
        preparedStatement.setString(1, customer.getCustomerName());
        preparedStatement.setString(2, customer.getAddress());
        preparedStatement.setString(3, customer.getPhone());
        
        if (customer.getUnitsConsumed() != null) {
            preparedStatement.setInt(4, customer.getUnitsConsumed());
        } else {
            preparedStatement.setNull(4, Types.INTEGER);
        }
    }

    // Helper method to set generated account number
    private void setGeneratedAccountNo(PreparedStatement preparedStatement, Customer customer) 
            throws SQLException {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                customer.setAccountNo(generatedKeys.getInt(1));
            }
        }
    }

    // Helper method to extract customer from ResultSet
    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setAccountNo(rs.getInt("account_no"));
        customer.setCustomerName(rs.getString("customer_name"));
        customer.setAddress(rs.getString("address"));
        customer.setPhone(rs.getString("phone"));
        
        int unitsConsumed = rs.getInt("units_consumed");
        if (!rs.wasNull()) {
            customer.setUnitsConsumed(unitsConsumed);
        } else {
            customer.setUnitsConsumed(null);
        }
        
        return customer;
    }

    // Exception handling method
    private void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                System.err.println("SQLException occurred:");
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                
                // Print stack trace for debugging
                e.printStackTrace();
                
                // Print cause chain
                Throwable cause = e.getCause();
                while (cause != null) {
                    System.err.println("Cause: " + cause);
                    cause = cause.getCause();
                }
            }
        }
    }
}