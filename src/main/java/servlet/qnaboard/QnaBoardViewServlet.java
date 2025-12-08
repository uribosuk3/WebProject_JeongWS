package Servlet.qnaboard;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.dao.QnaBoardDAO;
import model.dao.UsersDAO; // 작성자 이름 조회를 위해 추가
import model.dto.QnaBoardDTO;
import model.dto.UsersDTO;

@WebServlet("/qna/view.do")
public class QnaBoardViewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. 파라미터 받기 (게시글 번호)
        String idxStr = req.getParameter("idx");
        if (idxStr == null || idxStr.trim().isEmpty()) {
            // 게시글 번호가 없으면 목록으로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/qna/list.do");
            return;
        }
        
        int idx = 0;
        try {
            idx = Integer.parseInt(idxStr);
        } catch (NumberFormatException e) {
            // 숫자가 아니면 목록으로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/qna/list.do");
            return;
        }

        // 2. DAO 인스턴스 준비
        QnaBoardDAO qnaDao = QnaBoardDAO.getInstance();
        UsersDAO userDao = UsersDAO.getInstance(); // 사용자 이름 조회를 위한 DAO

        // 3. 조회수 증가 (먼저 실행)
        qnaDao.updateViews(idx);

        // 4. 게시글 정보 조회
        QnaBoardDTO board = qnaDao.selectBoard(idx);

        if (board == null) {
            // 게시글이 존재하지 않으면 목록으로
            resp.sendRedirect(req.getContextPath() + "/qna/list.do");
            return;
        }
        
        // 5. 작성자 이름 조회 및 DTO에 설정
        UsersDTO writer = userDao.selectUserByIdx(board.getUser_idx());
        if (writer != null) {
            board.setWriterName(writer.getName());
        } else {
            board.setWriterName("탈퇴한 사용자"); // 사용자가 없는 경우
        }

        // 6. View에 전달할 데이터 설정
        req.setAttribute("board", board);
        
        // 7. View로 포워딩
        RequestDispatcher rd = req.getRequestDispatcher("/qna/view.jsp");
        rd.forward(req, resp);
    }
}