package servlet.freeboard;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.FreeBoardDAO;
import model.dto.FreeBoardDTO;
import model.dto.UsersDTO; // 사용자 정보를 얻기 위해 필요

@WebServlet("/board/write.do")
public class FreeBoardWriteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        req.setCharacterEncoding("UTF-8");
        
        // 1. 세션에서 로그인된 사용자 정보 (idx) 확인
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            // 로그인 상태가 아니면 접근 거부
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");
        int user_idx = loginUser.getIdx(); // 현재 로그인된 사용자의 고유 ID (작성자)
        
        // 2. 폼 데이터 추출
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        
        // 3. 유효성 검사
        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            req.setAttribute("errorMsg", "제목과 내용을 모두 입력해 주세요.");
            // 오류 발생 시 사용자가 입력했던 값 유지
            req.getRequestDispatcher("/board/write.jsp").forward(req, resp);
            return;
        }

        // 4. DTO 생성 및 값 설정
        FreeBoardDTO dto = new FreeBoardDTO();
        dto.setUser_idx(user_idx); // 작성자 ID 설정
        dto.setTitle(title);
        dto.setContent(content);
        
        FreeBoardDAO dao = FreeBoardDAO.getInstance();
        
        // 5. DB 저장
        boolean isSuccess = dao.insertBoard(dto);

        if (isSuccess) {
            // 성공 시, 목록 페이지의 첫 번째 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/board/list.do");
        } else {
            // DB 저장 실패 시
            req.setAttribute("errorMsg", "게시글 등록에 실패했습니다. 다시 시도해 주세요.");
            // 입력값 유지하며 폼으로 포워드
            req.getRequestDispatcher("/board/write.jsp").forward(req, resp);
        }
    }
}