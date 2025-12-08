package servlet.fileboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.dao.FileBoardDAO;
import model.dto.FileBoardDTO;

@WebServlet("/fileboard/download.do")
public class FileBoardDownloadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // FileBoardWriteServlet과 동일한 업로드 경로 설정
    private static final String UPLOAD_DIR = "uploadFiles";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. 파라미터 받기 (게시글 번호)
        String idxStr = req.getParameter("idx");
        int idx = 0;
        try {
            idx = Integer.parseInt(idxStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/fileboard/list.do");
            return;
        }
        
        // 2. DAO를 통해 파일 정보 조회 (stored_filename, original_filename, filesize)
        FileBoardDAO dao = FileBoardDAO.getInstance();
        FileBoardDTO dto = dao.selectBoard(idx);
        
        if (dto == null || dto.getStored_filename() == null) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('파일 정보가 유효하지 않거나 게시글이 없습니다.'); history.back();</script>");
            return;
        }

        // 3. 파일 경로 설정
        String applicationPath = req.getServletContext().getRealPath("");
        String saveDirectory = applicationPath + File.separator + UPLOAD_DIR;
        String filePath = saveDirectory + File.separator + dto.getStored_filename();
        
        File downloadFile = new File(filePath);
        
        if (!downloadFile.exists() || !downloadFile.canRead()) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('파일이 존재하지 않거나 읽을 수 없습니다.'); history.back();</script>");
            return;
        }

        // 4. HTTP 헤더 설정 (다운로드 준비)
        String originalFileName = dto.getOriginal_filename();
        
        // 파일명 인코딩 처리 (브라우저 호환성)
        // 여기서는 URLEncoder를 사용하여 ISO-8859-1로 인코딩하는 표준 방식을 사용합니다.
        String encodedFileName = URLEncoder.encode(originalFileName, "UTF-8").replaceAll("\\+", "%20");
        
        resp.setContentType("application/octet-stream"); // 이진 데이터 형식
        resp.setContentLengthLong(dto.getFilesize());
        
        // Content-Disposition 설정: 다운로드할 파일명을 브라우저에 알려줌
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

        // 5. 파일 전송 (스트림 복사)
        try (FileInputStream fis = new FileInputStream(downloadFile);
             OutputStream os = resp.getOutputStream()) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            
        } catch (IOException e) {
            System.err.println("파일 다운로드 중 오류 발생: " + e.getMessage());
            // 파일 전송 실패 시, 별도 에러 처리 (로그 기록 등)
        }
    }
}