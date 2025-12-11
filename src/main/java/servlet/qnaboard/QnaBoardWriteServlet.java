package servlet.qnaboard;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.QnaBoardDAO;
import model.dto.QnaBoardDTO;
import model.dto.UsersDTO;

@WebServlet("/qnaboard/write.do")
public class QnaBoardWriteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {

        // 1. 로그인 여부 및 사용자 정보 확인
        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            // 로그인되어 있지 않으면 로그인 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
            return;
        }

        // 2. 파라미터 받기 (제목, 내용)
        req.setCharacterEncoding("UTF-8");
        String title = req.getParameter("title");
        String content = req.getParameter("content");

        // 3. DTO에 데이터 설정
        QnaBoardDTO dto = new QnaBoardDTO();
        dto.setUser_idx(loginUser.getIdx()); // 작성자 IDX 설정
        dto.setTitle(title);
        dto.setContent(content);
        // *주의: 원본 질문이므로 gnum, onum, depth는 DAO에서 처리합니다.

        // 4. DAO를 통해 DB에 저장
        QnaBoardDAO dao = QnaBoardDAO.getInstance();
        boolean result = dao.insertQuestion(dto); // 원본 질문 등록 메서드

        // 5. 결과 처리 및 리다이렉트
        if (result) {
            // 성공: 목록 페이지로 이동
            resp.sendRedirect(req.getContextPath() + "/qnaboard/list.do");
        } else {
            // 실패: 에러 메시지 출력 후 등록 페이지 유지 (또는 에러 페이지로)
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('질문 등록에 실패했습니다. 다시 시도해 주세요.'); history.back();</script>");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. 로그인 여부 확인
        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            
            // 🚨 로그인되지 않은 경우: 경고창 출력 후 로그인 페이지로 리다이렉트
            
            // a. 응답 타입 설정 (HTML, UTF-8)
            resp.setContentType("text/html; charset=UTF-8");
            
            // b. JavaScript 출력
            String loginPagePath = req.getContextPath() + "/member/login.jsp"; // 💡 로그인 페이지 경로
            
            resp.getWriter().println("<script>");
            resp.getWriter().println("    alert('로그인 후 작성 가능합니다.');");
            resp.getWriter().println("    location.href='" + loginPagePath + "';");
            resp.getWriter().println("</script>");
            
            return; // 서블릿 실행 종료
        }

        // 2. 로그인된 경우: 글쓰기 폼 페이지로 포워드
        req.setAttribute("pageTitle", "자료실 글쓰기");
        req.getRequestDispatcher("/qnaboard/write.jsp").forward(req, resp);
    }
}