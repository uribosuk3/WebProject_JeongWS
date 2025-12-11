package servlet.freeboard;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.FreeBoardDAO;
import model.dto.FreeBoardDTO;
import model.dto.UsersDTO; 

@WebServlet("/freeboard/write.do")
public class FreeBoardWriteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // 💡 GET 요청 처리: 글쓰기 폼을 보여줍니다. (추가할 부분)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 로그인 체크 (write.jsp에서도 체크하지만, 서블릿에서 한 번 더 체크하는 것이 좋습니다)
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // write.jsp로 포워딩하여 폼 페이지를 사용자에게 표시합니다.
        req.getRequestDispatcher("/freeboard/write.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)    
            throws ServletException, IOException {
        
        // ... (기존 doPost 로직은 그대로 유지)
        req.setCharacterEncoding("UTF-8");
        
        // 1. 세션에서 로그인된 사용자 정보 (idx) 확인
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");
        int user_idx = loginUser.getIdx();
        
        // 2. 폼 데이터 추출
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        
        // 3. 유효성 검사
        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            req.setAttribute("errorMsg", "제목과 내용을 모두 입력해 주세요.");
            req.getRequestDispatcher("/freeboard/write.jsp").forward(req, resp);
            return;
        }

        // 4. DTO 생성 및 값 설정
        FreeBoardDTO dto = new FreeBoardDTO();
        dto.setUser_idx(user_idx); 
        dto.setTitle(title);
        dto.setContent(content);
        
        FreeBoardDAO dao = FreeBoardDAO.getInstance();
        
        // 5. DB 저장
        boolean isSuccess = dao.insertBoard(dto);

        if (isSuccess) {
            resp.sendRedirect(req.getContextPath() + "/freeboard/list.do");
        } else {
            req.setAttribute("errorMsg", "게시글 등록에 실패했습니다. 다시 시도해 주세요.");
            req.getRequestDispatcher("/freeboard/write.jsp").forward(req, resp);
        }
    }
}