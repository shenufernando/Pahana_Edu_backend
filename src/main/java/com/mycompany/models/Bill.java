package com.mycompany.models;

import java.util.ArrayList;
import java.util.List;

public class Bill {
    private int billId;
    private int customerId;
    private String paymentMethod;
    private String cardName;
    private String cardNumber;
    private String cvv;
    private double totalAmount;
    private List<BillItem> items = new ArrayList<>();
    
    // Getters and setters
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }
    
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }
    public void addItem(BillItem item) { this.items.add(item); }
}