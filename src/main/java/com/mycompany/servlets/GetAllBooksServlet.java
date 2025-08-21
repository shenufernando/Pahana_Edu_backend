package com.mycompany.servlets;

import com.mycompany.dao.BookDAO;
import com.mycompany.models.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/GetAllBooksServlet")
public class GetAllBooksServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CORS headers
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        
        System.out.println("=== DEBUG: GetAllBooksServlet called ===");
        
        try {
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.getAllBooks();
            
            JSONArray booksArray = new JSONArray();
            for (Book book : books) {
                JSONObject bookJson = new JSONObject();
                bookJson.put("bookCode", book.getBookCode());
                bookJson.put("bookTitle", book.getBookTitle());
                bookJson.put("bookCategory", book.getBookCategory());
                bookJson.put("price", book.getPrice());
                bookJson.put("availableQuantity", book.getAvailableQuantity());
                booksArray.put(bookJson);
            }
            
            jsonResponse.put("status", "success");
            jsonResponse.put("books", booksArray);
            out.print(jsonResponse.toString());
            
            System.out.println("Successfully returned " + booksArray.length() + " books");
            
        } catch (Exception e) {
            System.out.println("=== ERROR in GetAllBooksServlet ===");
            e.printStackTrace();
            System.out.println("=== END ERROR ===");
            
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Server error: " + e.getMessage());
            out.print(jsonResponse.toString());
        } finally {
            out.flush();
            System.out.println("=== DEBUG: GetAllBooksServlet completed ===");
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}