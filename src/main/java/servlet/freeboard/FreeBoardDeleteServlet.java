package servlet.freeboard; // 💡 변경된 패키지 구조

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.FreeBoardDAO;
import model.dto.FreeBoardDTO;
import model.dto.UsersDTO;

@WebServlet("/freeboard/delete.do")
public class FreeBoardDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FreeBoardDAO dao = FreeBoardDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)    
            throws ServletException, IOException {
        
        // 1. 로그인 및 권한 체크
        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");
        String idxStr = req.getParameter("idx");
        
        if (loginUser == null || idxStr == null || idxStr.trim().isEmpty()) {
            // 💡 경로 통일: /board/list.do -> /freeboard/list.do
            resp.sendRedirect(req.getContextPath() + "/freeboard/list.do");
            return;
        }
        
        int idx = Integer.parseInt(idxStr);

        // 2. 게시글 정보 조회 (권한 확인용)
        FreeBoardDTO board = dao.selectBoard(idx);

        // 3. 존재 여부 및 작성자 일치 확인
        if (board == null || board.getUser_idx() != loginUser.getIdx()) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('삭제 권한이 없거나 게시글이 존재하지 않습니다.'); history.back();</script>");
            return;
        }
        
        // 4. DB 삭제 처리
        // 💡 DAO에서 댓글, 추천 정보도 함께 삭제하도록 트랜잭션 처리가 되어 있어야 합니다. (DAO에서 이미 처리했다 가정)
        boolean dbResult = dao.deleteBoard(idx); 

        // 5. 결과 처리
        if (dbResult) {
            // 💡 경로 통일: /board/list.do -> /freeboard/list.do
            resp.sendRedirect(req.getContextPath() + "/freeboard/list.do");
        } else {
            // DB 삭제 실패
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('게시글 삭제에 실패했습니다. (DB 오류)'); history.back();</script>");
        }
    }
}