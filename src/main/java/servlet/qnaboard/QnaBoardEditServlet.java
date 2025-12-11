package servlet.qnaboard;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.QnaBoardDAO;
import model.dto.QnaBoardDTO;
import model.dto.UsersDTO;

@WebServlet("/qnaboard/edit.do")
public class QnaBoardEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * GET 요청 처리: 게시글 수정 폼 페이지를 보여줍니다.
     * URL: /qnaboard/edit.do?idx=123
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. 로그인 여부 확인 (수정은 로그인 필수)
        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
            return;
        }

        // 2. 수정할 게시글의 IDX 받기
        String idxStr = req.getParameter("idx");
        int idx = 0;
        try {
            idx = Integer.parseInt(idxStr);
        } catch (NumberFormatException e) {
            // IDX가 없거나 유효하지 않으면 목록으로
            resp.sendRedirect(req.getContextPath() + "/qnaboard/list.do");
            return;
        }

        QnaBoardDAO dao = QnaBoardDAO.getInstance();
        // 3. 기존 게시글 정보 조회
        QnaBoardDTO boardDto = dao.selectBoard(idx);

        if (boardDto == null) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('게시글이 존재하지 않습니다.'); history.back();</script>");
            return;
        }
        
        // 4. 권한 확인: 작성자 본인만 수정 가능
        if (loginUser.getIdx() != boardDto.getUser_idx()) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('수정 권한이 없습니다.'); history.back();</script>");
            return;
        }

        // 5. 게시글 정보를 request 영역에 저장하고 수정 폼으로 포워드
        req.setAttribute("board", boardDto);
        req.getRequestDispatcher("/qnaboard/edit.jsp").forward(req, resp);
    }


    /**
     * POST 요청 처리: 수정된 데이터를 받아 DB에 반영합니다.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {

        // 1. 로그인 사용자 정보 확인 (doGet과 동일하지만, 보안상 다시 확인)
        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
            return;
        }

        // 2. 파라미터 받기 (idx, 제목, 내용)
        req.setCharacterEncoding("UTF-8");
        String idxStr = req.getParameter("idx");
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        
        int idx = 0;
        try {
            idx = Integer.parseInt(idxStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/qnaboard/list.do");
            return;
        }

        QnaBoardDAO dao = QnaBoardDAO.getInstance();

        // 3. 기존 글의 작성자 정보 확인 (2차 권한 확인)
        QnaBoardDTO existingDto = dao.selectBoard(idx);
        if (existingDto == null || loginUser.getIdx() != existingDto.getUser_idx()) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('수정 권한이 없거나 게시글이 존재하지 않습니다.'); history.back();</script>");
            return;
        }
        
        // 4. DTO에 수정 데이터 설정
        QnaBoardDTO updateDto = new QnaBoardDTO();
        updateDto.setIdx(idx);
        updateDto.setTitle(title);
        updateDto.setContent(content);

        // 5. DAO를 통해 DB 업데이트 실행
        boolean result = dao.updateBoard(updateDto);

        // 6. 결과 처리 및 리다이렉트
        if (result) {
            // 성공: 수정된 글의 상세 보기 페이지로 리다이렉트
            // 💡 주의: 페이지 번호 유지가 필요하다면 파라미터로 넘겨줘야 합니다. (여기서는 생략)
            resp.sendRedirect(req.getContextPath() + "/qnaboard/view.do?idx=" + idx);
        } else {
            // 실패: 에러 메시지 출력 후 수정 페이지 유지
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('게시글 수정에 실패했습니다. 다시 시도해 주세요.'); history.back();</script>");
        }
    }
}