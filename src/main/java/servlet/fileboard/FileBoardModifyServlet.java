package servlet.fileboard;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import model.dao.FileBoardDAO;
import model.dto.FileBoardDTO;
import model.dto.UsersDTO;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 15
)
@WebServlet("/fileboard/modify.do")
public class FileBoardModifyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "uploadFiles";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. 로그인 및 권한 체크 (이전에 구현한 QnABoardModifyServlet과 동일 로직)
        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");
        
        String idxStr = req.getParameter("idx");
        if (loginUser == null || idxStr == null || idxStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/fileboard/list.do");
            return;
        }
        
        int idx = Integer.parseInt(idxStr);
        FileBoardDAO dao = FileBoardDAO.getInstance();
        FileBoardDTO board = dao.selectBoard(idx);

        if (board == null || board.getUser_idx() != loginUser.getIdx()) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('수정 권한이 없거나 게시글이 존재하지 않습니다.'); history.back();</script>");
            return;
        }

        // 2. View로 데이터 전달
        req.setAttribute("board", board);
        req.getRequestDispatcher("/fileboard/modify.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");
        if (loginUser == null) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }
        
        req.setCharacterEncoding("UTF-8");
        
        // 1. 파라미터 받기 (idx, title, content, 기존 파일 정보)
        int idx = Integer.parseInt(req.getParameter("idx"));
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        
        // Hidden 필드로 기존 파일 정보를 받음
        String oldStoredFileName = req.getParameter("old_stored_filename");
        String oldOriginalFileName = req.getParameter("old_original_filename");
        long oldFilesize = 0;
        try { oldFilesize = Long.parseLong(req.getParameter("old_filesize")); } catch(Exception e) {}
        
        // 2. 새로운 파일 처리
        String newOriginalFileName = null;
        String newStoredFileName = null;
        long newFilesize = 0;
        Part filePart = req.getPart("new_upload_file"); 

        String applicationPath = req.getServletContext().getRealPath("");
        String uploadPath = applicationPath + File.separator + UPLOAD_DIR;
        
        if (filePart != null && filePart.getSize() > 0) {
            // 새 파일이 업로드된 경우: 기존 파일 삭제 후 새 파일 저장
            
            // 2-a. 기존 서버 파일 삭제
            if (oldStoredFileName != null && !oldStoredFileName.isEmpty()) {
                File oldFile = new File(uploadPath + File.separator + oldStoredFileName);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
            
            // 2-b. 새 파일 저장 및 메타데이터 설정
            newOriginalFileName = filePart.getSubmittedFileName();
            newFilesize = filePart.getSize();
            String extension = "";
            int dotIndex = newOriginalFileName.lastIndexOf('.');
            if (dotIndex > 0) { extension = newOriginalFileName.substring(dotIndex); }
            newStoredFileName = UUID.randomUUID().toString() + extension;
            filePart.write(uploadPath + File.separator + newStoredFileName);
            
        } else {
            // 새 파일이 없는 경우: 기존 파일 정보 유지
            newOriginalFileName = oldOriginalFileName;
            newStoredFileName = oldStoredFileName;
            newFilesize = oldFilesize;
        }

        // 3. DTO에 데이터 설정
        FileBoardDTO dto = new FileBoardDTO();
        dto.setIdx(idx);
        dto.setUser_idx(loginUser.getIdx()); // 권한 확인을 위해 DTO에 user_idx 설정
        dto.setTitle(title);
        dto.setContent(content);
        dto.setOriginal_filename(newOriginalFileName);
        dto.setStored_filename(newStoredFileName);
        dto.setFilesize(newFilesize);

        // 4. DAO를 통해 DB 업데이트
        FileBoardDAO dao = FileBoardDAO.getInstance();
        boolean result = dao.updateBoard(dto);

        // 5. 결과 처리
        if (result) {
            resp.sendRedirect(req.getContextPath() + "/fileboard/view.do?idx=" + idx);
        } else {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('게시글 수정에 실패했습니다.'); history.back();</script>");
            // 참고: DB 업데이트 실패 시, 새롭게 저장된 파일도 롤백 (삭제)하는 로직이 추가되면 더 완벽함.
        }
    }
}