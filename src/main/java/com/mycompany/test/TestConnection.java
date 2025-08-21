package com.mycompany.test;

import com.mycompany.utils.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection to pahana_edu...");
        
        try {
            // Get connection from your DBConnection class
            Connection conn = DBConnection.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ SUCCESS: Database connection established!");
                System.out.println("Database: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Version: " + conn.getMetaData().getDatabaseProductVersion());
                
                // Close the connection
                conn.close();
                System.out.println("Connection closed properly.");
            } else {
                System.out.println("❌ FAILED: Connection is null or closed");
            }
            
        } catch (SQLException e) {
            System.out.println("❌ SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ General Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}