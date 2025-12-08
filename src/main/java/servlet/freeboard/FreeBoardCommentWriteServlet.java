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

@WebServlet("/comment/write.do")
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
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // 2. 폼 데이터 추출
        String boardIdxStr = req.getParameter("board_idx");
        String pageNum = req.getParameter("pageNum");
        String content = req.getParameter("content");

        int board_idx = 0;
        try {
            board_idx = Integer.parseInt(boardIdxStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/board/list.do");
            return;
        }

        // 3. DTO 설정
        FreeBoardCommentDTO dto = new FreeBoardCommentDTO();
        dto.setBoard_idx(board_idx);
        dto.setUser_idx(loginUser.getIdx()); // 작성자는 로그인 사용자
        dto.setContent(content);

        // 4. DB 삽입
        FreeBoardCommentDAO dao = FreeBoardCommentDAO.getInstance();
        boolean isSuccess = dao.insertComment(dto);

        // 5. 결과 처리 및 리다이렉트
        if (isSuccess) {
            // 성공 시, 게시글 상세 보기 페이지로 리다이렉트 (pageNum 유지)
            resp.sendRedirect(req.getContextPath() + "/board/view.do?idx=" + board_idx + "&pageNum=" + pageNum);
        } else {
            // 실패 시, 에러 처리 (여기서는 간단히 리다이렉트)
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "댓글 등록에 실패했습니다.");
        }
    }
}