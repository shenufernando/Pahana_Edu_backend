package com.mycompany.servlets;

import com.mycompany.dao.BookDAO;
import com.mycompany.models.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

@WebServlet("/AddBookServlet")
@MultipartConfig(maxFileSize = 16177215) // 16MB max file size
public class AddBookServlet extends HttpServlet {
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CORS headers
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        System.out.println("=== AddBookServlet Called ===");
        
        try {
            // Debug: Print all received parameters
            System.out.println("Received parameters:");
            java.util.Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                System.out.println(paramName + ": " + request.getParameter(paramName));
            }
            
            String bookTitle = request.getParameter("bookTitle");
            String bookCategory = request.getParameter("bookCategory");
            String priceStr = request.getParameter("price");
            String quantityStr = request.getParameter("quantity");
            
            System.out.println("Raw values - Title: '" + bookTitle + "', Category: '" + bookCategory + 
                             "', Price: '" + priceStr + "', Quantity: '" + quantityStr + "'");
            
            // Validate parameters
            if (bookTitle == null || bookTitle.trim().isEmpty() ||
                bookCategory == null || bookCategory.trim().isEmpty() ||
                priceStr == null || priceStr.trim().isEmpty() ||
                quantityStr == null || quantityStr.trim().isEmpty()) {
                
                System.out.println("Validation failed: Missing required fields");
                out.print("{\"status\":\"error\",\"message\":\"All fields are required\"}");
                return;
            }
            
            // Parse numeric values with error handling
            double price;
            int quantity;
            
            try {
                price = Double.parseDouble(priceStr.trim());
                quantity = Integer.parseInt(quantityStr.trim());
            } catch (NumberFormatException e) {
                System.out.println("Number format error: " + e.getMessage());
                out.print("{\"status\":\"error\",\"message\":\"Invalid number format\"}");
                return;
            }
            
            System.out.println("Parsed values - Title: '" + bookTitle + "', Category: '" + bookCategory + 
                             "', Price: " + price + ", Quantity: " + quantity);
            
            // Handle file upload
            Part filePart = request.getPart("bookImage");
            InputStream imageStream = null;
            
            if (filePart != null && filePart.getSize() > 0) {
                System.out.println("File received: " + filePart.getSubmittedFileName() + 
                                 ", Size: " + filePart.getSize() + " bytes");
                imageStream = filePart.getInputStream();
            } else {
                System.out.println("No file received or file is empty");
            }
            
            Book book = new Book(bookTitle.trim(), bookCategory.trim(), price, quantity);
            
            if (imageStream != null) {
                System.out.println("Setting book image...");
                book.setBookImage(new javax.sql.rowset.serial.SerialBlob(imageStream.readAllBytes()));
            }
            
            BookDAO bookDAO = new BookDAO();
            System.out.println("Calling bookDAO.addBook()...");
            
            if (bookDAO.addBook(book)) {
                System.out.println("Book added successfully!");
                out.print("{\"status\":\"success\",\"message\":\"Book added successfully!\"}");
            } else {
                System.out.println("BookDAO.addBook() returned false");
                out.print("{\"status\":\"error\",\"message\":\"Failed to add book\"}");
            }
            
        } catch (Exception e) {
            System.out.println("=== EXCEPTION IN AddBookServlet ===");
            e.printStackTrace();
            System.out.println("Exception message: " + e.getMessage());
            System.out.println("=== END EXCEPTION ===");
            
            out.print("{\"status\":\"error\",\"message\":\"Server error: " + e.getMessage().replace("\"", "\\\"") + "\"}");
        } finally {
            out.flush();
        }
    }
    
    @Override
    public void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}