package com.mycompany.servlets;

import com.mycompany.dao.BookDAO;
import com.mycompany.models.Book;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.sql.Blob;
import java.util.Base64;

@WebServlet("/ViewBooksServlet")
public class ViewBooksServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set response content type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Enable CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        BookDAO bookDAO = new BookDAO();
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        
        try {
            System.out.println("=== ViewBooksServlet: Starting to fetch books ===");
            
            // Get all books from database
            List<Book> books = bookDAO.getAllBooks();
            
            System.out.println("Fetched " + books.size() + " books from DAO");
            
            // Convert books to a format that includes images
            List<Map<String, Object>> bookList = new ArrayList<>();
            
            for (Book book : books) {
                Map<String, Object> bookMap = new HashMap<>();
                bookMap.put("bookCode", book.getBookCode());
                bookMap.put("bookTitle", book.getBookTitle());
                bookMap.put("bookCategory", book.getBookCategory());
                bookMap.put("price", book.getPrice());
                bookMap.put("availableQuantity", book.getAvailableQuantity());
                
                // Handle image - get the full book with image
                Book fullBook = bookDAO.getBookByCode(book.getBookCode());
                if (fullBook != null && fullBook.getBookImage() != null) {
                    try {
                        Blob imageBlob = fullBook.getBookImage();
                        byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                        bookMap.put("bookImage", "data:image/jpeg;base64," + base64Image);
                    } catch (Exception e) {
                        System.out.println("Error processing image for book " + book.getBookCode() + ": " + e.getMessage());
                        bookMap.put("bookImage", null);
                    }
                } else {
                    bookMap.put("bookImage", null);
                }
                
                bookList.add(bookMap);
            }
            
            // Create response object with exactly the format JavaScript expects
            Map<String, Object> responseObj = new HashMap<>();
            responseObj.put("status", "success");  // String "success"
            responseObj.put("message", "Books loaded successfully");
            responseObj.put("books", bookList);
            
            // Send JSON response
            String jsonResponse = gson.toJson(responseObj);
            out.print(jsonResponse);
            
            System.out.println("=== ViewBooksServlet: Successfully sent " + books.size() + " books ===");
            System.out.println("Response JSON: " + jsonResponse);
            
        } catch (Exception e) {
            System.out.println("=== ERROR in ViewBooksServlet ===");
            e.printStackTrace();
            
            // Send error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error fetching books: " + e.getMessage());
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(errorResponse));
            
        } finally {
            out.close();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle preflight requests
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
