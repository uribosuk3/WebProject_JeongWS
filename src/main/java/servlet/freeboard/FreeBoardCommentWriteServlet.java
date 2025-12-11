package servlet.freeboard;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.FreeBoardCommentDAO;
import model.dto.FreeBoardCommentDTO;
import model.dto.UsersDTO;

@WebServlet("/freeboard/commentWrite.do")
public class FreeBoardCommentWriteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)     
            throws ServletException, IOException {
        
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        UsersDTO loginUser = (session != null) ? (UsersDTO) session.getAttribute("loginUser") : null;

        // 1. 로그인 인증
        if (loginUser == null) {
            // 🚨 경로 수정: /member/login.do
            resp.sendRedirect(req.getContextPath() + "/member/login.do"); 
            return;
        }

        // 2. 폼 데이터 추출
        // 🚨 JSP의 name="freeboard_idx"와 일치하도록 수정
        String boardIdxStr = req.getParameter("freeboard_idx"); 
        String pageNum = req.getParameter("pageNum");
        String content = req.getParameter("content");

        int board_idx = 0;
        try {
            board_idx = Integer.parseInt(boardIdxStr);
        } catch (NumberFormatException e) {
            // board_idx가 잘못되면 목록으로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/freeboard/list.do");
            return;
        }
        
        // 🚨 2-1. Content 유효성 검사 추가
        if (content == null || content.trim().isEmpty()) {
            // 댓글 내용이 없으면 상세 페이지로 다시 리다이렉트 (오류 메시지는 view.jsp에서 처리)
            resp.sendRedirect(req.getContextPath() + "/freeboard/view.do?idx=" + board_idx + "&pageNum=" + pageNum + "&msg=empty_content");
            return; 
        }

        // 3. DTO 설정
        FreeBoardCommentDTO dto = new FreeBoardCommentDTO();
        dto.setBoard_idx(board_idx);
        dto.setUser_idx(loginUser.getIdx());
        dto.setContent(content);

        // 4. DB 삽입
        FreeBoardCommentDAO dao = FreeBoardCommentDAO.getInstance();
        boolean isSuccess = dao.insertComment(dto);

        // 5. 결과 처리 및 리다이렉트
        if (isSuccess) {
            // 성공 시, 게시글 상세 보기 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/freeboard/view.do?idx=" + board_idx + "&pageNum=" + pageNum);
        } else {
            // 실패 시, 에러 응답 대신 상세 페이지로 돌아가 알림 메시지 표시 (선택적)
            // resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "댓글 등록에 실패했습니다.");
            resp.sendRedirect(req.getContextPath() + "/freeboard/view.do?idx=" + board_idx + "&pageNum=" + pageNum + "&msg=db_error");
        }
    }
}