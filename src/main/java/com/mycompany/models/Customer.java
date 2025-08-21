package com.mycompany.models;

public class Customer {
    private int customerId; // This will store account_no from database
    private String name;
    private String address;
    private String phone;
    private int unitsConsumed;
    
    // Constructors
    public Customer() {}
    
    public Customer(String name, String address, String phone, int unitsConsumed) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.unitsConsumed = unitsConsumed;
    }
    
    // Getters and Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public int getUnitsConsumed() { return unitsConsumed; }
    public void setUnitsConsumed(int unitsConsumed) { this.unitsConsumed = unitsConsumed; }
    
    // Helper method to get account number (alias for customerId)
    public int getAccountNo() { return customerId; }
    public void setAccountNo(int accountNo) { this.customerId = accountNo; }
}