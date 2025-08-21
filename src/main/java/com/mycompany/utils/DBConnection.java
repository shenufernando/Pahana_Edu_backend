package com.mycompany.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // ADD "?useSSL=false" to disable SSL encryption + your password
    private static final String URL = "jdbc:mysql://localhost:3306/pahana_edu?" +
                                  "useSSL=false&" +
                                  "allowPublicKeyRetrieval=true&" +
                                  "useUnicode=true&" +
                                  "characterEncoding=UTF-8&" +
                                  "serverTimezone=UTC&" +
                                  "autoReconnect=true";
    private static final String USER = "root"; // or your MySQL username
    private static final String PASSWORD = "1234"; // ← YOUR PASSWORD HERE
    
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database connection failed: " + e.getMessage());
        }
    }
    
    // Even better connection string with additional parameters:

}