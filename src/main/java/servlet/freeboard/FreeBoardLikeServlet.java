package servlet.freeboard;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.FreeBoardLikeDAO;
import model.dto.FreeBoardLikeDTO;
import model.dto.UsersDTO;

@WebServlet("/board/like.do")
public class FreeBoardLikeServlet extends HttpServlet {
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
        String boardIdxStr = req.getParameter("idx");
        String pageNum = req.getParameter("pageNum");

        int board_idx = 0;
        try {
            board_idx = Integer.parseInt(boardIdxStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/board/list.do");
            return;
        }

        int user_idx = loginUser.getIdx();
        FreeBoardLikeDAO dao = FreeBoardLikeDAO.getInstance();
        boolean isSuccess = false;

        // 3. 추천 상태 확인
        int existingLikeIdx = dao.checkLike(board_idx, user_idx);

        if (existingLikeIdx > 0) {
            // 이미 추천한 경우: 추천 취소 (Delete)
            isSuccess = dao.deleteLike(existingLikeIdx, board_idx);
            if (!isSuccess) {
                 System.err.println("추천 취소 실패");
            }
        } else {
            // 추천하지 않은 경우: 추천 등록 (Insert)
            FreeBoardLikeDTO dto = new FreeBoardLikeDTO();
            dto.setBoard_idx(board_idx);
            dto.setUser_idx(user_idx);
            isSuccess = dao.insertLike(dto);
            if (!isSuccess) {
                 System.err.println("추천 등록 실패");
            }
        }

        // 4. 상세 페이지로 리다이렉트하여 변경된 상태 확인
        resp.sendRedirect(req.getContextPath() + "/board/view.do?idx=" + board_idx + "&pageNum=" + pageNum);
    }
}