package servlet.freeboard;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie; // 쿠키 사용을 위해 추가
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.FreeBoardDAO;
import model.dao.FreeBoardCommentDAO;
import model.dao.FreeBoardLikeDAO;
import model.dto.FreeBoardDTO;
import model.dto.FreeBoardCommentDTO;
import model.dto.UsersDTO;

@WebServlet("/freeboard/view.do")
public class FreeBoardViewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)    
            throws ServletException, IOException {
        
        // 세션 및 사용자 정보 가져오기
        HttpSession session = req.getSession(false);
        UsersDTO loginUser = (session != null) ? (UsersDTO) session.getAttribute("loginUser") : null;
        
        String idxStr = req.getParameter("idx");
        String pageNum = req.getParameter("pageNum");

        int idx = 0;
        try {
            idx = Integer.parseInt(idxStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/freeboard/list.do");
            return;
        }

        FreeBoardDAO dao = FreeBoardDAO.getInstance();
        FreeBoardCommentDAO commentDao = FreeBoardCommentDAO.getInstance();
        
        // 1. 조회수 증가 로직 (쿠키 확인 기반으로 수정)
        
        // 1-1. 쿠키 이름 설정: 게시글 번호별로 고유하게 설정
        String cookieName = "view_freeboard_" + idx;
        
        // 1-2. 요청에서 기존 쿠키를 찾습니다.
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

        // 1-3. 쿠키가 없을 때만 조회수 증가 및 쿠키 생성
        if (!cookieFound) {
            dao.updateViews(idx); // FreeBoardDAO에 updateViews(idx) 메서드가 필요합니다.

            // 새로운 쿠키 생성 (유효 시간: 1일)
            Cookie newCookie = new Cookie(cookieName, "viewed");
            newCookie.setMaxAge(60 * 60 * 24); 
            
            // 쿠키 유효 경로: /freeboard 경로에서만 유효
            newCookie.setPath(req.getContextPath() + "/freeboard"); 
            
            resp.addCookie(newCookie);
        }
        
        // 2. 게시글 정보 조회
        FreeBoardDTO dto = dao.selectBoard(idx);

        if (dto != null) {
            // 3. 댓글 목록 조회 
            List<FreeBoardCommentDTO> commentList = commentDao.selectList(idx);
            
            // 4. 사용자 추천 상태 확인 및 전달
            boolean isLiked = false;
            if (loginUser != null) {
                FreeBoardLikeDAO likeDao = FreeBoardLikeDAO.getInstance();
                // 추천 기록이 있으면 likeDao.checkLike()는 0보다 큰 값을 반환
                int likeIdx = likeDao.checkLike(idx, loginUser.getIdx());  
                isLiked = likeIdx > 0;
            }

            // 5. 조회된 데이터 View에 전달
            req.setAttribute("board", dto);
            req.setAttribute("commentList", commentList);
            req.setAttribute("pageNum", pageNum);
            req.setAttribute("isLiked", isLiked);
            
            // 6. View(JSP)로 포워드
            req.getRequestDispatcher("/freeboard/view.jsp").forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "요청한 게시글을 찾을 수 없습니다.");
        }
    }
}