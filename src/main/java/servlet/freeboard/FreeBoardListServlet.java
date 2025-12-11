package servlet.freeboard; 

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.dao.FreeBoardDAO;
// import model.dao.UsersDAO; // 💡 UsersDAO는 더 이상 필요 없으므로 제거
import model.dto.FreeBoardDTO;

@WebServlet("/freeboard/list.do")
public class FreeBoardListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)    
            throws ServletException, IOException {

        // 1. DAO 인스턴스
        FreeBoardDAO dao = FreeBoardDAO.getInstance();
        // UsersDAO usersDao = UsersDAO.getInstance(); // 💡 작성자 이름 조회 로직 제거로 인해 필요 없음

        // 💡💡 2. 검색 파라미터 추출 및 설정 💡💡
        String searchField = req.getParameter("searchField");
        String searchWord = req.getParameter("searchWord");
        
        // 검색 필드가 없으면 기본값 'title'로 설정
        if (searchField == null || searchField.trim().isEmpty()) {
            searchField = "title"; 
        }
        // 검색어는 공백 제거 후 null이거나 비어있으면 null로 처리
        if (searchWord != null) {
              searchWord = searchWord.trim();
              if (searchWord.isEmpty()) {
                  searchWord = null;
              }
        }

        // --- [페이징 기본 설정] ---
        final int pageSize = 10; // 한 페이지당 게시물 수
        final int blockPage = 5;  // 페이지 블록에 표시할 페이지 수
        
        // 3. 현재 페이지 번호 (pageNum) 파라미터 처리
        String pageNumStr = req.getParameter("pageNum");
        int pageNum = 1;
        if (pageNumStr != null && !pageNumStr.isEmpty()) {
            try {
                pageNum = Integer.parseInt(pageNumStr);
            } catch (NumberFormatException e) {
                // 파싱 오류 발생 시 기본값 1로 유지
            }
        }
        
        // 4. 전체 게시물 수 조회 (검색 조건 전달)
        int totalCount = dao.selectCount(searchField, searchWord); 
        System.out.println("DEBUG: Total Count = " + totalCount); // 💡 이 줄을 추가하여 콘솔 확인
        
        // 5. 총 페이지 수 계산
        int totalPage = (int) Math.ceil((double) totalCount / pageSize);
        
        // 6. DB 조회를 위한 시작 및 끝 행 번호 계산 (RNUM 기준)
        int start = (pageNum - 1) * pageSize + 1;
        int end = pageNum * pageSize;
        
        // 7. DB에서 현재 페이지의 게시물 목록 조회 (검색 조건 전달)
        // 💡 DAO에서 이미 작성자 이름(writerName)을 DTO에 매핑하여 가져옵니다.
        List<FreeBoardDTO> boardList = dao.selectList(searchField, searchWord, start, end); 

        /* // 8. 작성자 이름 조회 및 DTO에 설정
        // 💡💡 이 로직은 FreeBoardDAO.selectList에서 JOIN을 통해 이미 처리했으므로 제거합니다.
        // for (FreeBoardDTO dto : boardList) {
        //     String writerName = usersDao.selectNameByIdx(dto.getUser_idx());
        //     dto.setWriterName(writerName != null ? writerName : "탈퇴 회원");
        // }
        */

        // --- [페이지 블록 계산] ---
        
        int startPage = ((pageNum - 1) / blockPage) * blockPage + 1;
        int endPage = startPage + blockPage - 1;
        
        if (endPage > totalPage) {
            endPage = totalPage;
        }

        // --- [View (JSP)로 데이터 전달] ---
        
        // 💡 JSP로 검색 조건 전달
        req.setAttribute("searchField", searchField);
        req.setAttribute("searchWord", searchWord); 
        
        // 게시물 목록
        req.setAttribute("freeboardList", boardList); 
        
        // 페이징 관련 변수
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("totalPage", totalPage);
        req.setAttribute("pageNum", pageNum);
        req.setAttribute("pageSize", pageSize);
        
        // 페이지 블록 관련 변수
        req.setAttribute("startPage", startPage);
        req.setAttribute("endPage", endPage);
        req.setAttribute("blockPage", blockPage);
        
        // 9. View(JSP)로 포워드 (경로 통일 완료)
        req.getRequestDispatcher("/freeboard/list.jsp").forward(req, resp);
    }
}