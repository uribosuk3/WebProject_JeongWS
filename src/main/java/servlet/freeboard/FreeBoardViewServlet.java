package servlet.freeboard;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // 세션 import 추가

import model.dao.FreeBoardDAO;
import model.dao.UsersDAO;
import model.dao.FreeBoardCommentDAO;
import model.dao.FreeBoardLikeDAO; // 💡 추천 DAO 추가
import model.dto.FreeBoardDTO;
import model.dto.FreeBoardCommentDTO;
import model.dto.UsersDTO; // UsersDTO 추가

@WebServlet("/board/view.do")
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
            resp.sendRedirect(req.getContextPath() + "/board/list.do");
            return;
        }

        FreeBoardDAO dao = FreeBoardDAO.getInstance();
        UsersDAO usersDao = UsersDAO.getInstance();
        FreeBoardCommentDAO commentDao = FreeBoardCommentDAO.getInstance();
        
        // 1. 조회수 증가
        dao.updateViews(idx); 

        // 2. 게시글 정보 조회
        FreeBoardDTO dto = dao.selectBoard(idx);

        if (dto != null) {
            // 3. 게시글 작성자 이름 조회 및 DTO에 설정
            String writerName = usersDao.selectNameByIdx(dto.getUser_idx());
            dto.setWriterName(writerName != null ? writerName : "탈퇴 회원");

            // 4. 댓글 목록 조회 및 작성자 이름 매핑
            List<FreeBoardCommentDTO> commentList = commentDao.selectList(idx);
            for (FreeBoardCommentDTO commentDto : commentList) {
                String commentWriterName = usersDao.selectNameByIdx(commentDto.getUser_idx());
                commentDto.setWriterName(commentWriterName != null ? commentWriterName : "탈퇴 회원");
            }
            
            // 💡💡 5. 사용자 추천 상태 확인 및 전달 💡💡
            boolean isLiked = false;
            if (loginUser != null) {
                FreeBoardLikeDAO likeDao = FreeBoardLikeDAO.getInstance();
                // 추천 기록이 있으면 likeDao.checkLike()는 0보다 큰 값을 반환
                int likeIdx = likeDao.checkLike(idx, loginUser.getIdx()); 
                isLiked = likeIdx > 0;
            }

            // 6. 조회된 데이터 View에 전달
            req.setAttribute("board", dto);
            req.setAttribute("commentList", commentList);
            req.setAttribute("pageNum", pageNum);
            req.setAttribute("isLiked", isLiked); // 💡 추천 상태 (boolean) 전달
            
            // 7. View(JSP)로 포워드
            req.getRequestDispatcher("/board/view.jsp").forward(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "요청한 게시글을 찾을 수 없습니다.");
        }
    }
}