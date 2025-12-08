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

@WebServlet("/comment/delete.do")
public class FreeBoardCommentDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        UsersDTO loginUser = (session != null) ? (UsersDTO) session.getAttribute("loginUser") : null;
        
        // 1. 로그인 인증
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // 2. 파라미터 추출
        String commentIdxStr = req.getParameter("comment_idx"); // 삭제할 댓글 번호
        String boardIdxStr = req.getParameter("board_idx"); // 리다이렉트할 게시글 번호
        String pageNum = req.getParameter("pageNum"); // 목록 복귀용

        int comment_idx = 0;
        int board_idx = 0;
        try {
            comment_idx = Integer.parseInt(commentIdxStr);
            board_idx = Integer.parseInt(boardIdxStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/board/list.do");
            return;
        }

        FreeBoardCommentDAO dao = FreeBoardCommentDAO.getInstance();
        FreeBoardCommentDTO comment = dao.selectComment(comment_idx); // 댓글 정보 조회
        
        // 3. 댓글 존재 및 작성자 인증
        if (comment == null || comment.getUser_idx() != loginUser.getIdx()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "삭제 권한이 없거나 존재하지 않는 댓글입니다.");
            return;
        }
        
        // 4. DB 삭제
        boolean isSuccess = dao.deleteComment(comment_idx);

        // 5. 결과 처리
        if (isSuccess) {
            // 성공 시, 상세 보기 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/board/view.do?idx=" + board_idx + "&pageNum=" + pageNum);
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "댓글 삭제에 실패했습니다.");
        }
    }
}