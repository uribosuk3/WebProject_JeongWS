package servlet.fileboard;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.dao.FileBoardDAO;
import model.dto.FileBoardDTO;
import util.BoardPage; // 페이징 처리를 위한 유틸리티 클래스 (이전에 구현되었다고 가정)

@WebServlet("/fileboard/list.do")
public class FileBoardListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        FileBoardDAO dao = FileBoardDAO.getInstance();

        // --- 1. 검색 및 페이징 파라미터 처리 ---
        String searchField = req.getParameter("searchField");
        String searchWord = req.getParameter("searchWord");
        String pageNum = req.getParameter("pageNum");

        // 검색어 필터링
        if (searchWord == null) {
            searchWord = "";
        }
        if (searchField == null || searchField.isEmpty()) {
            searchField = "title"; // 기본 검색 필드
        }

        // --- 2. 게시글 전체 개수 조회 ---
        int totalCount = dao.selectCount(searchField, searchWord); 
        
        // --- 3. 페이징 설정 값 계산 ---
        // (이 값들은 프로젝트 환경에 따라 변경될 수 있습니다.)
        int pageSize = 10;      // 한 페이지에 출력할 게시물 수
        int blockPage = 5;      // 한 블록에 표시할 페이지 번호 수
        
        int totalPage = (int) Math.ceil((double) totalCount / pageSize); // 전체 페이지 수
        
        int currentPage = 1;
        if (pageNum != null && !pageNum.isEmpty()) {
            currentPage = Integer.parseInt(pageNum);
        }
        
        // --- 4. DB 조회에 필요한 시작/종료 번호 계산 (Oracle ROWNUM 기준) ---
        int start = (currentPage - 1) * pageSize + 1; // 시작 ROWNUM
        int end = currentPage * pageSize;             // 종료 ROWNUM

        // --- 5. 목록 조회 ---
        List<FileBoardDTO> boardList = dao.selectList(searchField, searchWord, start, end);
        
        // --- 6. View에 전달할 데이터 설정 ---
        req.setAttribute("boardList", boardList);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("searchField", searchField);
        req.setAttribute("searchWord", searchWord);
        
        // --- 7. 페이징 HTML 코드 생성 ---
        String pagingStr = BoardPage.pagingStr(totalCount, pageSize, blockPage, currentPage, 
                                               req.getContextPath() + "/fileboard/list.do", 
                                               searchField, searchWord);

        req.setAttribute("pagingStr", pagingStr);

        // 8. View로 포워딩
        req.getRequestDispatcher("/fileboard/list.jsp").forward(req, resp);
    }
}