package com.mycompany.models;

public class User {
    private int userId;  // Changed from 'id' to 'userId' to match database column
    private String fullName;
    private String email;
    private String username;
    private String password;
    private String role;

    public User() {}

    public User(String fullName, String email, String username, String password, String role) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Changed from getId() to getUserId() to match what GetUserServlet expects
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    // Keep the old getId() method for backward compatibility if needed
    public int getId() { return userId; }
    public void setId(int id) { this.userId = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}