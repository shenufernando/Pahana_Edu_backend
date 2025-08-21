package com.mycompany.servlets;

import com.mycompany.dao.BookDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;

@WebServlet("/BookImageServlet")
public class BookImageServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            int bookCode = Integer.parseInt(request.getParameter("code"));
            BookDAO bookDAO = new BookDAO();
            Blob imageBlob = bookDAO.getBookImage(bookCode);
            
            if (imageBlob != null) {
                response.setContentType("image/jpeg");
                InputStream inputStream = imageBlob.getBinaryStream();
                OutputStream outputStream = response.getOutputStream();
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                inputStream.close();
                outputStream.flush();
            } else {
                // Serve default image if no image in database
                response.sendRedirect("../images/default-book.jpg");
            }
        } catch (Exception e) {
            response.sendRedirect("../images/default-book.jpg");
        }
    }
}