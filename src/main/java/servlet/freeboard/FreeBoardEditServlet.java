package servlet.freeboard;

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

@WebServlet("/freeboard/edit.do")
public class FreeBoardEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 1. GET 요청: 수정 폼을 보여줍니다. (기존 내용을 채워서)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)    
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        UsersDTO loginUser = (session != null) ? (UsersDTO) session.getAttribute("loginUser") : null;
        
        // 로그인 인증
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // 파라미터 추출
        String idxStr = req.getParameter("idx");
        int idx = 0;
        try {
            idx = Integer.parseInt(idxStr);
        } catch (NumberFormatException e) {
            // 💡 경로 통일: /board/list.do -> /freeboard/list.do
            resp.sendRedirect(req.getContextPath() + "/freeboard/list.do");
            return;
        }

        FreeBoardDAO dao = FreeBoardDAO.getInstance();
        FreeBoardDTO board = dao.selectBoard(idx); // 게시글 정보 조회

        // 게시글 존재 및 작성자 인증
        if (board == null || board.getUser_idx() != loginUser.getIdx()) {
            // 게시글이 없거나 작성자가 아님
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "수정 권한이 없습니다.");
            return;
        }
        
        // DTO를 View로 전달 (수정 폼에 기존 내용을 채우기 위해)
        req.setAttribute("board", board);
        
        // View로 포워드
        // 💡 경로 통일: /board/edit.jsp -> /freeboard/edit.jsp
        req.getRequestDispatcher("/freeboard/edit.jsp").forward(req, resp);
    }

    // 2. POST 요청: 수정된 내용을 DB에 반영합니다.
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)    
            throws ServletException, IOException {
        
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        UsersDTO loginUser = (session != null) ? (UsersDTO) session.getAttribute("loginUser") : null;

        // 로그인 인증
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        
        // 폼 데이터 추출
        String idxStr = req.getParameter("idx");
        String pageNum = req.getParameter("pageNum"); // 목록 복귀용
        String title = req.getParameter("title");
        String content = req.getParameter("content");

        int idx = 0;
        try {
            idx = Integer.parseInt(idxStr);
        } catch (NumberFormatException e) {
            // 💡 경로 통일: /board/list.do -> /freeboard/list.do
            resp.sendRedirect(req.getContextPath() + "/freeboard/list.do");
            return;
        }
        
        FreeBoardDAO dao = FreeBoardDAO.getInstance();
        FreeBoardDTO oldBoard = dao.selectBoard(idx); // 기존 게시글 정보를 다시 조회

        // 작성자 인증 (재확인) 및 게시글 존재 확인
        if (oldBoard == null || oldBoard.getUser_idx() != loginUser.getIdx()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "수정 권한이 없습니다.");
            return;
        }

        // DTO 설정 (수정할 내용)
        FreeBoardDTO dto = new FreeBoardDTO();
        dto.setIdx(idx);
        dto.setTitle(title);
        dto.setContent(content);

        // DB 업데이트
        boolean isSuccess = dao.updateBoard(dto);

        if (isSuccess) {
            // 성공 시, 상세 보기 페이지로 리다이렉트 (pageNum 유지)
            // 💡 경로 통일: /board/view.do -> /freeboard/view.do
            resp.sendRedirect(req.getContextPath() + "/freeboard/view.do?idx=" + idx + "&pageNum=" + pageNum);
        } else {
            // 실패 시, 에러 메시지를 담아 폼으로 포워드
            req.setAttribute("errorMsg", "게시글 수정에 실패했습니다. DB 오류를 확인하세요.");
            // 실패하더라도 수정 폼을 보여주기 위해 기존 DTO와 pageNum을 다시 설정
            req.setAttribute("board", oldBoard); 
            // 💡 경로 통일 (JSP 포워드 경로는 이미 통일되어 있었음)
            req.getRequestDispatcher("/freeboard/edit.jsp").forward(req, resp);
        }
    }
}