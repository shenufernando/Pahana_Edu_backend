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

@WebServlet("/UpdateBookServlet")
@MultipartConfig(maxFileSize = 16177215)
public class UpdateBookServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // CORS headers
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            int bookCode = Integer.parseInt(request.getParameter("bookCode"));
            String bookTitle = request.getParameter("bookTitle");
            String bookCategory = request.getParameter("bookCategory");
            double price = Double.parseDouble(request.getParameter("price"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            
            Book book = new Book(bookTitle, bookCategory, price, quantity);
            book.setBookCode(bookCode);
            
            // Handle file upload (optional)
            Part filePart = request.getPart("bookImage");
            if (filePart != null && filePart.getSize() > 0) {
                InputStream imageStream = filePart.getInputStream();
                book.setBookImage(new javax.sql.rowset.serial.SerialBlob(imageStream.readAllBytes()));
            }
            
            BookDAO bookDAO = new BookDAO();
            
            if (bookDAO.updateBook(book)) {
                out.print("{\"status\":\"success\",\"message\":\"Book updated successfully!\"}");
            } else {
                out.print("{\"status\":\"error\",\"message\":\"Failed to update book\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\",\"message\":\"Server error: " + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
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