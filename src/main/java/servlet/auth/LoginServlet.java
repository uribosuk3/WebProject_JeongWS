package servlet.auth;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.UsersDAO;
import model.dto.UsersDTO;

@WebServlet("/member/login.do")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // GET 요청: 로그인 페이지로 리다이렉트
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
    			throws ServletException, IOException {
        // JSP 파일의 실제 위치인 /member/login.jsp로 리다이렉트
        resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
    }
    
    // POST 요청: 로그인 처리
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        // 인코딩 설정
        req.setCharacterEncoding("UTF-8");
        
        String id = req.getParameter("id");
        String pw = req.getParameter("pw");
        String saveIdChecked = req.getParameter("save_id");
        
        UsersDAO dao = UsersDAO.getInstance();
        UsersDTO loginUser = dao.login(id, pw);

        if (loginUser != null) {
            // 1. 로그인 성공 처리
            req.getSession().setAttribute("loginUser", loginUser);

            // 2. 아이디 저장 (쿠키) 처리
            Cookie cookie = new Cookie("savedId", id);
            
            if (saveIdChecked != null) { 
                // 아이디 저장 체크: 7일간 쿠키 유지
                cookie.setMaxAge(60 * 60 * 24 * 7); 
            } else {
                // 아이디 저장 해제: 쿠키 즉시 삭제
                cookie.setMaxAge(0); 
            }
            
            // 쿠키 경로 설정 (전체 애플리케이션에서 사용 가능하도록)
            cookie.setPath("/"); 
            resp.addCookie(cookie);

            // 3. 메인 페이지로 리다이렉트 (Context Path 사용)
            resp.sendRedirect(req.getContextPath() + "/index.jsp");

        } else {
            // 1. 로그인 실패 처리
            String errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";
            
            // 2. 실패 메시지와 기존 입력 ID를 request에 담아 포워드
            req.setAttribute("errorMessage", errorMessage);
            req.setAttribute("inputId", id);
            
            // 💡 login.jsp의 실제 위치인 /member/login.jsp로 포워드
            req.getRequestDispatcher("/member/login.jsp").forward(req, resp);
        }
    }
}