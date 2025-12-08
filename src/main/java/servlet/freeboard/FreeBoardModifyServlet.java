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

@WebServlet("/board/modify.do")
public class FreeBoardModifyServlet extends HttpServlet {
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
            resp.sendRedirect(req.getContextPath() + "/board/list.do");
            return;
        }
        
        int idx = Integer.parseInt(idxStr);
        FreeBoardDTO board = dao.selectBoard(idx);

        // 2. 존재 여부 및 작성자 일치 확인
        if (board == null || board.getUser_idx() != loginUser.getIdx()) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('수정 권한이 없거나 게시글이 존재하지 않습니다.'); history.back();</script>");
            return;
        }

        // 3. View로 데이터 전달 및 포워딩
        req.setAttribute("pageTitle", "게시글 수정");
        req.setAttribute("board", board);
        // 💡 JSP 경로 수정 반영
        req.getRequestDispatcher("/WEB-INF/views/board/modify.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");
        if (loginUser == null) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }
        
        req.setCharacterEncoding("UTF-8");
        
        // 1. 파라미터 받기 (idx, title, content)
        int idx = Integer.parseInt(req.getParameter("idx"));
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        
        // 2. DTO에 데이터 설정
        FreeBoardDTO dto = new FreeBoardDTO();
        dto.setIdx(idx);
        dto.setUser_idx(loginUser.getIdx()); // 권한 확인을 위해 user_idx 설정
        dto.setTitle(title);
        dto.setContent(content);

        // 3. DAO를 통해 DB 업데이트
        boolean result = dao.updateBoard(dto);

        // 4. 결과 처리
        if (result) {
            resp.sendRedirect(req.getContextPath() + "/board/view.do?idx=" + idx);
        } else {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('게시글 수정에 실패했습니다.'); history.back();</script>");
        }
    }
}