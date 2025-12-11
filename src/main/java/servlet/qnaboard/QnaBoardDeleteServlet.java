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

@WebServlet("/qnaboard/delete.do")
public class QnaBoardDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private QnaBoardDAO dao = QnaBoardDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. 로그인 및 권한 체크
        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");
        String idxStr = req.getParameter("idx");
        
        if (loginUser == null || idxStr == null || idxStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/qnaboard/list.do");
            return;
        }
        
        int idx = Integer.parseInt(idxStr);

        // 2. 게시글 정보 조회 (권한 확인용)
        QnaBoardDTO board = dao.selectBoard(idx);

        // 3. 존재 여부 및 작성자 일치 확인
        if (board == null || board.getUser_idx() != loginUser.getIdx()) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('삭제 권한이 없거나 게시글이 존재하지 않습니다.'); history.back();</script>");
            return;
        }
        
        // 4. DB 삭제 처리 (해당 게시글과 관련된 답글, 댓글 등도 DAO에서 처리해야 함)
        // 💡 Q&A는 계층형이므로, 원글 삭제 시 답글 처리에 유의해야 합니다.
        boolean dbResult = dao.deleteBoard(idx); 

        // 5. 결과 처리
        if (dbResult) {
            resp.sendRedirect(req.getContextPath() + "/qnaboard/list.do");
        } else {
            // DB 삭제 실패
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('게시글 삭제에 실패했습니다. (DB 오류)'); history.back();</script>");
        }
    }
}