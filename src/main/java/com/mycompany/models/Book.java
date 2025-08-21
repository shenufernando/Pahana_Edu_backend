package com.mycompany.models;

import java.sql.Blob;

public class Book {
    private int bookCode;
    private String bookTitle;
    private String bookCategory;
    private double price;
    private int availableQuantity;
    private Blob bookImage;
    // REMOVED: private String imageName;
    
    // Constructors
    public Book() {}
    
    public Book(String bookTitle, String bookCategory, double price, int availableQuantity) {
        this.bookTitle = bookTitle;
        this.bookCategory = bookCategory;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }
    
    // Getters and Setters (REMOVE imageName getter/setter)
    public int getBookCode() { return bookCode; }
    public void setBookCode(int bookCode) { this.bookCode = bookCode; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getBookCategory() { return bookCategory; }
    public void setBookCategory(String bookCategory) { this.bookCategory = bookCategory; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
    
    public Blob getBookImage() { return bookImage; }
    public void setBookImage(Blob bookImage) { this.bookImage = bookImage; }
    
    // REMOVED: imageName getter and setter
}