package Servlet.qnaboard;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.dao.QnaBoardDAO; // Q&A DAO 사용
import model.dao.UsersDAO;
import model.dto.QnaBoardDTO; // Q&A DTO 사용

@WebServlet("/qna/list.do")
public class QnaBoardListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {

        // 1. DAO 인스턴스
        QnaBoardDAO dao = QnaBoardDAO.getInstance();
        UsersDAO usersDao = UsersDAO.getInstance();

        // 2. 검색 파라미터 추출 및 설정
        String searchField = req.getParameter("searchField");
        String searchWord = req.getParameter("searchWord");
        
        if (searchField == null || searchField.trim().isEmpty()) {
            searchField = "title"; 
        }
        if (searchWord != null) {
             searchWord = searchWord.trim();
             if (searchWord.isEmpty()) {
                 searchWord = null;
             }
        }

        // --- [페이징 기본 설정] ---
        final int pageSize = 10; 
        final int blockPage = 5;
        
        String pageNumStr = req.getParameter("pageNum");
        int pageNum = 1;
        if (pageNumStr != null && !pageNumStr.isEmpty()) {
            try {
                pageNum = Integer.parseInt(pageNumStr);
            } catch (NumberFormatException e) {
                // 파싱 오류 발생 시 기본값 1로 유지
            }
        }
        
        // 3. 전체 게시물 수 조회 (검색 조건 전달)
        int totalCount = dao.selectCount(searchField, searchWord); 
        
        // 4. 총 페이지 수 계산
        int totalPage = (int) Math.ceil((double) totalCount / pageSize);
        
        // 5. DB 조회를 위한 시작 및 끝 행 번호 계산
        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;
        
        // 6. DB에서 현재 페이지의 게시물 목록 조회 (검색 조건 전달, 계층형 정렬)
        List<QnaBoardDTO> boardList = dao.selectList(searchField, searchWord, start, end); 

        // 7. 작성자 이름 조회 및 DTO에 설정
        for (QnaBoardDTO dto : boardList) {
            String writerName = usersDao.selectNameByIdx(dto.getUser_idx());
            dto.setWriterName(writerName != null ? writerName : "탈퇴 회원");
        }

        // --- [페이지 블록 계산] ---
        int startPage = ((pageNum - 1) / blockPage) * blockPage + 1;
        int endPage = startPage + blockPage - 1;
        
        if (endPage > totalPage) {
            endPage = totalPage;
        }

        // --- [View (JSP)로 데이터 전달] ---
        
        req.setAttribute("searchField", searchField);
        req.setAttribute("searchWord", searchWord); 
        
        req.setAttribute("boardList", boardList); 
        
        // 페이징 관련 변수
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("totalPage", totalPage);
        req.setAttribute("pageNum", pageNum);
        req.setAttribute("pageSize", pageSize);
        
        // 페이지 블록 관련 변수
        req.setAttribute("startPage", startPage);
        req.setAttribute("endPage", endPage);
        req.setAttribute("blockPage", blockPage);
        
        // 8. View(JSP)로 포워드
        req.getRequestDispatcher("/qna/list.jsp").forward(req, resp);
    }
}