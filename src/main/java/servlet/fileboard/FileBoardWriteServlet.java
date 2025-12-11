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
import jakarta.servlet.http.Part; // 파일 업로드 처리를 위한 Part 객체

import model.dao.FileBoardDAO;
import model.dto.FileBoardDTO;
import model.dto.UsersDTO;

// 💡 파일 업로드 처리를 위한 필수 어노테이션
@MultipartConfig(
    maxFileSize = 1024 * 1024 * 20,  // 20MB
    maxRequestSize = 1024 * 1024 * 20 // 20MB
)
@WebServlet("/fileboard/write.do")
public class FileBoardWriteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 💡 파일을 저장할 서버상의 물리적 경로
    private static final String UPLOAD_DIR = "uploadFiles";
    
    // ----------------------------------------------------
    // GET 요청 처리 (폼 페이지 로드)
    // ----------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            resp.setContentType("text/html; charset=UTF-8");
            String loginPagePath = req.getContextPath() + "/member/login.jsp";
            
            resp.getWriter().println("<script>");
            resp.getWriter().println("    alert('로그인 후 작성 가능합니다.');");
            resp.getWriter().println("    location.href='" + loginPagePath + "';");
            resp.getWriter().println("</script>");
            
            return; 
        }

        req.setAttribute("pageTitle", "자료실 글쓰기");
        req.getRequestDispatcher("/fileboard/write.jsp").forward(req, resp);
    }
    
    // ----------------------------------------------------
    // POST 요청 처리 (글 등록 및 파일 저장)
    // ----------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
            return;
        }

        req.setCharacterEncoding("UTF-8");
        
        // 1. 게시글 데이터 파라미터 받기 (일반 폼 필드)
        String title = req.getParameter("title");
        String content = req.getParameter("content");

        // 2. 파일 파라미터 받기 및 서버에 저장
        String originalFileName = null;
        String storedFileName = null;
        long fileSize = 0;
        String fileType = ""; // ⭐️ 파일 타입을 저장할 변수 추가
        
        Part filePart = req.getPart("upload_file"); // 폼 필드 name="upload_file"
        
        if (filePart != null && filePart.getSize() > 0) {
            
            // a. 파일 메타데이터 추출
            originalFileName = filePart.getSubmittedFileName();
            fileSize = filePart.getSize();
            
            // b. 서버 저장 경로 설정 및 디렉토리 생성
            String applicationPath = req.getServletContext().getRealPath("");
            String uploadPath = applicationPath + File.separator + UPLOAD_DIR;
            
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // 디렉토리가 없으면 생성
            }
            
            // c. 파일명 중복 방지를 위한 UUID 생성 및 파일 타입 추출
            String extension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                extension = originalFileName.substring(dotIndex);
                // ⭐️ 파일 타입 추출 (확장자에서 .을 제외하고 소문자로 변환)
                fileType = originalFileName.substring(dotIndex + 1).toLowerCase(); 
            }
            storedFileName = UUID.randomUUID().toString() + extension;
            
            // d. 파일 저장
            filePart.write(uploadPath + File.separator + storedFileName);
        }

        // 3. DTO 객체 생성 및 데이터 설정
        FileBoardDTO dto = new FileBoardDTO();
        dto.setUser_idx(loginUser.getIdx());
        dto.setTitle(title);
        dto.setContent(content);
        
        // ⭐️⭐️ 핵심 수정 부분: 파일이 없을 경우 NULL 대신 빈 문자열("") 설정 ⭐️⭐️
        if (originalFileName == null) {
            originalFileName = "";
            storedFileName = "";
        }
        // fileSize는 0으로 초기화되었거나 파일이 있으면 크기가 할당됨

        dto.setOriginal_filename(originalFileName);
        dto.setStored_filename(storedFileName);
        dto.setFilesize(fileSize);
        dto.setFile_type(fileType); // ⭐️ DTO에 file_type 설정

        // 4. DAO를 통해 DB에 저장
        FileBoardDAO dao = FileBoardDAO.getInstance();
        boolean result = dao.insertFileBoard(dto);

        // 5. 결과 처리 및 리다이렉트
        if (result) {
            resp.sendRedirect(req.getContextPath() + "/fileboard/list.do");
        } else {
            // DB 저장 실패 시, 서버에 저장된 파일 삭제 로직
            if (storedFileName != null && !storedFileName.isEmpty()) { 
                String applicationPath = req.getServletContext().getRealPath("");
                String uploadPath = applicationPath + File.separator + UPLOAD_DIR;
                File storedFile = new File(uploadPath + File.separator + storedFileName);
                if (storedFile.exists()) {
                    storedFile.delete();
                    System.err.println("DB 등록 실패로 인해 서버 파일 삭제됨: " + storedFileName);
                }
            }
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('게시글 등록 및 파일 저장에 실패했습니다.'); history.back();</script>");
        }
    }
}