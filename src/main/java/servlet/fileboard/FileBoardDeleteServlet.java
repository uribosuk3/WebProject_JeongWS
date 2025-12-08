package servlet.fileboard;

import java.io.File;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.FileBoardDAO;
import model.dto.FileBoardDTO;
import model.dto.UsersDTO;

@WebServlet("/fileboard/delete.do")
public class FileBoardDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "uploadFiles";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. 로그인 및 권한 체크
        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");
        String idxStr = req.getParameter("idx");
        
        if (loginUser == null || idxStr == null || idxStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/fileboard/list.do");
            return;
        }
        
        int idx = Integer.parseInt(idxStr);
        FileBoardDAO dao = FileBoardDAO.getInstance();

        // 2. 게시글 정보 조회 (파일 경로 확인을 위해)
        FileBoardDTO board = dao.selectBoard(idx);

        // 3. 존재 여부 및 작성자 일치 확인
        if (board == null || board.getUser_idx() != loginUser.getIdx()) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('삭제 권한이 없거나 게시글이 존재하지 않습니다.'); history.back();</script>");
            return;
        }
        
        // 4. DB 삭제 처리
        boolean dbResult = dao.deleteBoard(idx);

        // 5. 결과 처리 및 파일 삭제
        if (dbResult) {
            // DB 삭제 성공 시, 서버 파일도 삭제
            String storedFileName = board.getStored_filename();
            
            if (storedFileName != null && !storedFileName.isEmpty()) {
                String applicationPath = req.getServletContext().getRealPath("");
                String uploadPath = applicationPath + File.separator + UPLOAD_DIR;
                File storedFile = new File(uploadPath + File.separator + storedFileName);
                
                if (storedFile.exists()) {
                    storedFile.delete(); // 서버 파일 삭제
                }
            }
            
            resp.sendRedirect(req.getContextPath() + "/fileboard/list.do");
            
        } else {
            // DB 삭제 실패
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('게시글 삭제에 실패했습니다. (DB 오류)'); history.back();</script>");
        }
    }
}