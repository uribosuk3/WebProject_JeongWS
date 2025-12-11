package servlet.auth;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/member/logout.do")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // POST 요청을 받지 않고 GET 요청으로 처리 (로그아웃은 상태 변경이므로 GET으로도 허용)
        doPost(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false); // 기존 세션이 없으면 새로 생성하지 않음

        if (session != null) {
            // 1. 세션의 loginUser 속성 삭제
            session.removeAttribute("loginUser");
            
            // 2. 세션 자체를 무효화 (가장 확실한 방법)
            session.invalidate(); 
        }

        // 3. 메인 페이지로 리다이렉트
        resp.sendRedirect(req.getContextPath() + "/index.jsp");
    }
}