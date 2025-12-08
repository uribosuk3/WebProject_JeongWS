package servlet.fileboard;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.dao.FileBoardDAO;
import model.dao.UsersDAO; // 작성자 이름 조회를 위해 필요
import model.dto.FileBoardDTO;
import model.dto.UsersDTO;

@WebServlet("/fileboard/view.do")
public class FileBoardViewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. 파라미터 받기 (게시글 번호)
        String idxStr = req.getParameter("idx");
        if (idxStr == null || idxStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/fileboard/list.do");
            return;
        }
        
        int idx = 0;
        try {
            idx = Integer.parseInt(idxStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/fileboard/list.do");
            return;
        }

        // 2. DAO 인스턴스 준비
        FileBoardDAO fileDao = FileBoardDAO.getInstance();
        UsersDAO userDao = UsersDAO.getInstance(); 

        // 3. 조회수 증가 (먼저 실행)
        // 🚨 주의: FileBoardDAO에 updateViews(int idx) 메서드가 구현되어 있어야 합니다.
        fileDao.updateViews(idx); 

        // 4. 게시글 정보 조회 (파일 정보 포함)
        FileBoardDTO board = fileDao.selectBoard(idx);

        if (board == null) {
            // 게시글이 존재하지 않으면 목록으로
            resp.sendRedirect(req.getContextPath() + "/fileboard/list.do");
            return;
        }
        
        // 5. 작성자 이름 조회 및 DTO에 설정
        // UsersDAO의 selectUserByIdx(int idx) 메서드를 사용
        UsersDTO writer = userDao.selectUserByIdx(board.getUser_idx());
        if (writer != null) {
            // DTO에 writerName 필드가 있어야 합니다.
            board.setWriterName(writer.getName()); 
        } else {
            board.setWriterName("탈퇴한 사용자"); 
        }

        // 6. View에 전달할 데이터 설정
        req.setAttribute("board", board);
        
        // 7. View로 포워딩
        RequestDispatcher rd = req.getRequestDispatcher("/fileboard/view.jsp");
        rd.forward(req, resp);
    }
}