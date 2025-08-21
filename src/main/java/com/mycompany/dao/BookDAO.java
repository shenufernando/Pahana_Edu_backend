package com.mycompany.dao;

import com.mycompany.models.Book;
import com.mycompany.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    
    // Add new book
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (book_title, book_category, price, available_quantity, book_image) VALUES (?, ?, ?, ?, ?)";
        
        System.out.println("=== DEBUG: Starting addBook ===");
        System.out.println("Book Title: " + book.getBookTitle());
        System.out.println("Category: " + book.getBookCategory());
        System.out.println("Price: " + book.getPrice());
        System.out.println("Quantity: " + book.getAvailableQuantity());
        System.out.println("Has Image: " + (book.getBookImage() != null));
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("Database connection successful: " + (conn != null));
            
            stmt.setString(1, book.getBookTitle());
            stmt.setString(2, book.getBookCategory());
            stmt.setDouble(3, book.getPrice());
            stmt.setInt(4, book.getAvailableQuantity());
            
            if (book.getBookImage() != null) {
                System.out.println("Setting image blob, size: " + book.getBookImage().length() + " bytes");
                stmt.setBlob(5, book.getBookImage());
            } else {
                System.out.println("Setting NULL for image");
                stmt.setNull(5, Types.BLOB);
            }
            
            System.out.println("Executing SQL: " + sql);
            int rows = stmt.executeUpdate();
            System.out.println("Rows affected: " + rows);
            
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
    
    // Get all books - WITH DEBUG OUTPUT
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT book_code, book_title, book_category, price, available_quantity FROM books ORDER BY book_title";
        
        System.out.println("=== DEBUG: Starting getAllBooks ===");
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("Database connection successful: " + (conn != null));
            System.out.println("Executing SQL: " + sql);
            
            int count = 0;
            while (rs.next()) {
                Book book = new Book();
                book.setBookCode(rs.getInt("book_code"));
                book.setBookTitle(rs.getString("book_title"));
                book.setBookCategory(rs.getString("book_category"));
                book.setPrice(rs.getDouble("price"));
                book.setAvailableQuantity(rs.getInt("available_quantity"));
                books.add(book);
                count++;
            }
            
            System.out.println("Fetched " + count + " books from database");
            
        } catch (SQLException e) {
            System.out.println("=== SQL ERROR in getAllBooks ===");
            System.out.println("Error Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            System.out.println("=== END ERROR ===");
        }
        
        System.out.println("Returning " + books.size() + " books");
        System.out.println("=== DEBUG: Ending getAllBooks ===");
        return books;
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
    
    // Get book by code - FIXED RESOURCE LEAK
    public Book getBookByCode(int bookCode) {
        String sql = "SELECT * FROM books WHERE book_code = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Book book = new Book();
                    book.setBookCode(rs.getInt("book_code"));
                    book.setBookTitle(rs.getString("book_title"));
                    book.setBookCategory(rs.getString("book_category"));
                    book.setPrice(rs.getDouble("price"));
                    book.setAvailableQuantity(rs.getInt("available_quantity"));
                    book.setBookImage(rs.getBlob("book_image"));
                    return book;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error fetching book: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Update book
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET book_title = ?, book_category = ?, price = ?, available_quantity = ?, book_image = ? WHERE book_code = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getBookTitle());
            stmt.setString(2, book.getBookCategory());
            stmt.setDouble(3, book.getPrice());
            stmt.setInt(4, book.getAvailableQuantity());
            
            if (book.getBookImage() != null) {
                stmt.setBlob(5, book.getBookImage());
            } else {
                stmt.setNull(5, Types.BLOB);
            }
            
            stmt.setInt(6, book.getBookCode());
            
            int rows = stmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete book
    public boolean deleteBook(int bookCode) {
        String sql = "DELETE FROM books WHERE book_code = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookCode);
            int rows = stmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Optional: Get book image only (for displaying images)
    public Blob getBookImage(int bookCode) {
        String sql = "SELECT book_image FROM books WHERE book_code = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBlob("book_image");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error fetching book image: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}