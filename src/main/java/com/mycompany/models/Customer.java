package com.mycompany.models;

public class Customer {
    private int accountNo;
    private String customerName;
    private String address;
    private String phone;
    private Integer unitsConsumed;
    
    // Constructors
    public Customer() {}
    
    // Getters and Setters
    public int getAccountNo() { return accountNo; }
    public void setAccountNo(int accountNo) { this.accountNo = accountNo; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Integer getUnitsConsumed() { return unitsConsumed; }
    public void setUnitsConsumed(Integer unitsConsumed) { this.unitsConsumed = unitsConsumed; }
}