package com.mycompany.servlets;

import com.mycompany.dao.BookDAO;
import com.mycompany.models.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;

@WebServlet("/BookTitlesServlet")
public class BookTitlesServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CORS headers
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        try {
            System.out.println("=== BookTitlesServlet: Getting all books ===");
            
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.getAllBooks();
            
            System.out.println("Retrieved " + books.size() + " books from DAO");
            
            jsonResponse.put("status", "success");
            JSONArray booksArray = new JSONArray();
            
            for (Book book : books) {
                JSONObject bookObj = new JSONObject();
                bookObj.put("bookCode", book.getBookCode());
                bookObj.put("bookTitle", book.getBookTitle());
                bookObj.put("bookCategory", book.getBookCategory());
                bookObj.put("price", book.getPrice());
                bookObj.put("availableQuantity", book.getAvailableQuantity());
                booksArray.put(bookObj);
                
                System.out.println("Added book: " + book.getBookTitle() + " - " + book.getBookCategory());
            }
            
            jsonResponse.put("books", booksArray);
            System.out.println("Sending response with " + booksArray.length() + " books");
            
        } catch (Exception e) {
            System.out.println("=== ERROR in BookTitlesServlet ===");
            e.printStackTrace();
            System.out.println("=== END ERROR ===");
            
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Server error: " + e.getMessage());
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}