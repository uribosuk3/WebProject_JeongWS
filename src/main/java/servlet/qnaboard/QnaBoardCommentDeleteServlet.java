package servlet.qnaboard; // qnaboard 패키지 경로로 변경

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.QnaBoardCommentDAO;
import model.dto.QnaBoardCommentDTO;
import model.dto.UsersDTO;

// URL 패턴: /qnaboard/commentDelete.do
@WebServlet("/qnaboard/commentDelete.do")
public class QnaBoardCommentDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        // 1. 로그인 인증 및 작성자 ID 확인을 위한 세션 정보
        HttpSession session = req.getSession(false);
        UsersDTO loginUser = (session != null) ? (UsersDTO) session.getAttribute("loginUser") : null;

        if (loginUser == null) {
            // 로그인되어 있지 않으면 로그인 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/member/login.do"); 
            return;
        }

        // 2. 파라미터 추출
        String commentIdxStr = req.getParameter("comment_idx"); // 삭제할 댓글 번호
        String boardIdxStr = req.getParameter("board_idx");     // 💡 수정됨: board_idx를 사용해야 합니다.

        String pageNum = req.getParameter("pageNum"); 

        int comment_idx = 0;
        int board_idx = 0;
        try {
            comment_idx = Integer.parseInt(commentIdxStr);
            board_idx = Integer.parseInt(boardIdxStr);
        } catch (NumberFormatException e) {
            // 예외 발생 시 목록으로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/qnaboard/list.do");
            return;
        }
        
        QnaBoardCommentDAO dao = QnaBoardCommentDAO.getInstance();
        
        // 3. 권한 확인: 댓글 정보 조회 (삭제 권한 확인을 위해)
        // 💡 주의: 이 기능은 QnaBoardCommentDAO에 selectComment(int idx) 메소드가 추가되어야 작동합니다.
        QnaBoardCommentDTO comment = dao.selectComment(comment_idx); 

        if (comment == null) {
            // 댓글이 존재하지 않으면 상세 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/qnaboard/view.do?idx=" + board_idx + "&msg=not_found");
            return;
        }

        // 4. 권한 확인: 현재 로그인 사용자와 댓글 작성자가 일치하는지 확인
        if (comment.getUser_idx() != loginUser.getIdx()) {
            // 권한이 없으면 상세 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/qnaboard/view.do?idx=" + board_idx + "&msg=no_permission");
            return;
        }

        // 5. DB에서 댓글 삭제 처리
        boolean isSuccess = dao.deleteComment(comment_idx);

        // 6. 결과 처리 및 리다이렉트
        String redirectUrl = req.getContextPath() + "/qnaboard/view.do?idx=" + board_idx;
        if (pageNum != null) {
            redirectUrl += "&pageNum=" + pageNum;
        }

        if (isSuccess) {
            // 성공 시, 상세 페이지로 리다이렉트
            resp.sendRedirect(redirectUrl + "&msg=delete_success");
        } else {
            // 실패 시, 에러 메시지와 함께 상세 페이지로 돌아가기
            resp.sendRedirect(redirectUrl + "&msg=db_error");
        }
    }
}