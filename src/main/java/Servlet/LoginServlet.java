package Servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.UsersDAO;
import model.dto.UsersDTO;

@WebServlet("/login.do")
public class LoginServlet extends HttpServlet {

    private UsersDAO dao = new UsersDAO();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String id = req.getParameter("id");
        String pw = req.getParameter("pw");

        UsersDTO user = dao.login(id, pw);

        if (user != null) {
            req.getSession().setAttribute("loginUser", user);
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        } 
        else {
            req.setAttribute("error", "아이디 또는 비밀번호 오류");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
