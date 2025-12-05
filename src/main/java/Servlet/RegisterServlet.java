package Servlet; 

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register.do") 
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        String name = req.getParameter("name");
        String id = req.getParameter("id");
        String pw = req.getParameter("pw");
        String email = req.getParameter("email");
        resp.sendRedirect("login.jsp");
        
    }
}
