package servlet.qnaboard;

import java.io.IOException;
import java.util.List; // 💡 댓글 목록 저장을 위해 추가

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie; 
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.dao.QnaBoardDAO;
import model.dao.UsersDAO; 
// 💡 Q&A 댓글 DAO 임포트 추가
import model.dao.QnaBoardCommentDAO; 
import model.dto.QnaBoardDTO;
import model.dto.UsersDTO;
// 💡 Q&A 댓글 DTO 임포트 추가
import model.dto.QnaBoardCommentDTO; 

@WebServlet("/qnaboard/view.do")
public class QnaBoardViewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ... [1. 파라미터 받기 및 유효성 검사 (기존 코드 유지)] ...
        String idxStr = req.getParameter("idx");
        if (idxStr == null || idxStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/qnaboard/list.do");
            return;
        }
        int idx = 0;
        try {
            idx = Integer.parseInt(idxStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/qnaboard/list.do");
            return;
        }

        // 2. DAO 인스턴스 준비
        QnaBoardDAO qnaDao = QnaBoardDAO.getInstance();
        UsersDAO userDao = UsersDAO.getInstance();
        // 💡 Q&A 댓글 DAO 인스턴스 추가
        QnaBoardCommentDAO commentDAO = QnaBoardCommentDAO.getInstance(); 

        // ... [3. 조회수 증가 로직 (쿠키 확인 기반으로 수정) - 기존 코드 유지] ...
        String cookieName = "view_qnaboard_" + idx;
        Cookie[] cookies = req.getCookies();
        boolean cookieFound = false;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(cookieName)) {
                    cookieFound = true;
                    break;
                }
            }
        }

        if (!cookieFound) {
            qnaDao.updateViews(idx); 
            Cookie newCookie = new Cookie(cookieName, "viewed");
            newCookie.setMaxAge(60 * 60 * 24); 
            newCookie.setPath(req.getContextPath() + "/qnaboard"); 
            resp.addCookie(newCookie);
        }

        // 4. 게시글 정보 조회
        QnaBoardDTO board = qnaDao.selectBoard(idx);

        if (board == null) {
            resp.sendRedirect(req.getContextPath() + "/qnaboard/list.do");
            return;
        }

        // 5. 작성자 이름 조회 및 DTO에 설정 (기존 코드 유지)
        UsersDTO writer = userDao.selectUserByIdx(board.getUser_idx());
        if (writer != null) {
            board.setWriterName(writer.getName());
        } else {
            board.setWriterName("탈퇴한 사용자");
        }
        
        // =========================================================
        // 💡 6. 댓글 목록 조회 및 Request에 설정 (추가된 핵심 로직)
        // =========================================================
        List<QnaBoardCommentDTO> commentList = commentDAO.selectList(idx);
        
        req.setAttribute("commentList", commentList);
        // =========================================================

        // 7. View에 전달할 데이터 설정 (기존 board 정보 설정)
        req.setAttribute("board", board);

        // 8. View로 포워딩
        RequestDispatcher rd = req.getRequestDispatcher("/qnaboard/view.jsp");
        rd.forward(req, resp);
    }
}