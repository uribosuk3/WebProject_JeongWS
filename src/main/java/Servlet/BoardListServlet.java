package Servlet; 

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;           
import model.dao.FreeBoardDAO;  
import model.dto.FreeBoardDTO; 

@WebServlet("/board/list.do") 
public class BoardListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
    	FreeBoardDAO dao = FreeBoardDAO.getInstance(); 
        List<FreeBoardDTO> boardList = dao.selectList(); 
        
        req.setAttribute("pageTitle", "자유게시판 목록"); 
    
        req.getRequestDispatcher("/list.jsp").forward(req, resp);
    }
}