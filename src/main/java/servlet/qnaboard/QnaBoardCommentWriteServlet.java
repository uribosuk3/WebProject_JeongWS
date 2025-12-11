package servlet.qnaboard; // 패키지 경로를 qnaboard로 변경합니다.

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.QnaBoardCommentDAO; // 💡 QnaBoardCommentDAO 사용
import model.dto.QnaBoardCommentDTO; // 💡 QnaBoardCommentDTO 사용
import model.dto.UsersDTO;

// 💡 URL 패턴을 /qnaboard/commentWrite.do로 변경
@WebServlet("/qnaboard/commentWrite.do") 
public class QnaBoardCommentWriteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        UsersDTO loginUser = (session != null) ? (UsersDTO) session.getAttribute("loginUser") : null;

        // 1. 로그인 인증
        if (loginUser == null) {
            // 로그인 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/member/login.do"); 
            return;
        }

        // 2. 폼 데이터 추출
        // 💡 JSP의 name="qnaboard_idx"와 일치하도록 수정 (또는 boardIdx)
        String boardIdxStr = req.getParameter("boardIdx"); // Q&A 게시글 번호
        String pageNum = req.getParameter("pageNum"); // 목록으로 돌아갈 페이지 번호
        String content = req.getParameter("content");

        int board_idx = 0;
        try {
            board_idx = Integer.parseInt(boardIdxStr);
        } catch (NumberFormatException e) {
            // board_idx가 잘못되면 Q&A 목록으로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/qnaboard/list.do");
            return;
        }
        
        // 2-1. Content 유효성 검사
        if (content == null || content.trim().isEmpty()) {
            // 댓글 내용이 없으면 Q&A 상세 페이지로 리다이렉트
            String redirectUrl = req.getContextPath() + "/qnaboard/view.do?idx=" + board_idx + "&pageNum=" + pageNum + "&msg=empty_content";
            resp.sendRedirect(redirectUrl);
            return; 
        }

        // 3. DTO 설정
        QnaBoardCommentDTO dto = new QnaBoardCommentDTO(); // 💡 Qna DTO 사용
        dto.setBoard_idx(board_idx);
        dto.setUser_idx(loginUser.getIdx());
        dto.setContent(content);

        // 4. DB 삽입
        // QnaBoardCommentDAO를 사용하거나, Service를 사용해야 합니다.
        // 현재는 DAO만 구현했으므로 DAO를 직접 호출합니다. (나중에 Service로 변경 예정)
        QnaBoardCommentDAO dao = QnaBoardCommentDAO.getInstance(); // 💡 Qna DAO 사용
        boolean isSuccess = dao.insertComment(dto);

        // 5. 결과 처리 및 리다이렉트
        if (isSuccess) {
            // 성공 시, Q&A 게시글 상세 보기 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/qnaboard/view.do?idx=" + board_idx + "&pageNum=" + pageNum);
        } else {
            // 실패 시, 에러 메시지와 함께 상세 페이지로 돌아가기
            String redirectUrl = req.getContextPath() + "/qnaboard/view.do?idx=" + board_idx + "&pageNum=" + pageNum + "&msg=db_error";
            resp.sendRedirect(redirectUrl);
        }
    }
}