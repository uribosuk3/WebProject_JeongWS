package Servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.dao.UsersDAO;
import model.dto.UsersDTO;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {

    private UsersDAO dao = new UsersDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String action = req.getPathInfo(); 

        if ("/register".equals(action)) {

            String id = req.getParameter("id");
            String pw = req.getParameter("pw");
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");

            // DTO 만들기
            UsersDTO user = new UsersDTO();
            user.setId(id);
            user.setPw(pw);
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);

            // DAO에 전달
            dao.insertUser(user);

            // 회원가입 성공 → 로그인 페이지로 이동
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        }
    }
}
