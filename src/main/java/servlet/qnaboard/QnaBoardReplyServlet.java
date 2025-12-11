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

@WebServlet("/qnaboard/reply.do")
public class QnaBoardReplyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * GET 요청 처리: 답변 작성 폼 페이지를 보여줍니다.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // 1. 로그인 여부 확인
        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            // 로그인 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
            return;
        }

        // 2. 원글의 IDX를 받음
        String parentIdxStr = req.getParameter("idx");
        
        int parent_idx = 0;
        try {
            parent_idx = Integer.parseInt(parentIdxStr);
        } catch (NumberFormatException e) {
            // idx가 없거나 유효하지 않으면 목록으로
            resp.sendRedirect(req.getContextPath() + "/qnaboard/list.do");
            return;
        }

        // 3. 원글 정보 조회 (답변 페이지에 원글 제목 등을 보여주기 위해 필요)
        QnaBoardDAO dao = QnaBoardDAO.getInstance();
        QnaBoardDTO parentDto = dao.selectBoard(parent_idx);
        
        if (parentDto == null) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('원본 질문이 존재하지 않습니다.'); history.back();</script>");
            return;
        }
        
        // 4. 원글 정보를 request 영역에 저장하여 JSP로 전달
        req.setAttribute("parentBoard", parentDto);
        
        // 5. 답변 작성 폼 페이지로 포워드
        req.getRequestDispatcher("/qnaboard/write.jsp").forward(req, resp);
    }


    /**
     * POST 요청 처리: 답변 데이터를 받아 DB에 삽입합니다.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {

        // 1. 로그인 여부 및 사용자 정보 확인
        HttpSession session = req.getSession();
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
            return;
        }

        // 2. 파라미터 받기 (제목, 내용, 원글 idx)
        req.setCharacterEncoding("UTF-8");
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String parentIdxStr = req.getParameter("parent_idx");
        
        int parent_idx = 0;
        try {
            parent_idx = Integer.parseInt(parentIdxStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/qnaboard/list.do");
            return;
        }

        QnaBoardDAO dao = QnaBoardDAO.getInstance();

        // 3. 원본(부모) 게시글 정보 조회
        QnaBoardDTO parentDto = dao.selectBoard(parent_idx);

        if (parentDto == null) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('원본 질문이 존재하지 않습니다.'); history.back();</script>");
            return;
        }
        
        // 4. 답글 DTO 생성 및 데이터 설정
        QnaBoardDTO replyDto = new QnaBoardDTO();
        replyDto.setUser_idx(loginUser.getIdx()); // 작성자 IDX 설정
        replyDto.setTitle(title);
        replyDto.setContent(content);

        // 5. DAO를 통해 답글 등록 (DAO 내부에서 onum 조정 및 트랜잭션 처리)
        boolean result = dao.insertReply(parentDto, replyDto);

        // 6. 결과 처리 및 리다이렉트
        if (result) {
        	// ✅ [핵심]: 답변 성공 시 원글의 상태를 '답변완료'로 업데이트
            dao.updateReplyState(parentDto.getIdx());
            // 성공: 목록 페이지로 이동
            resp.sendRedirect(req.getContextPath() + "/qnaboard/list.do");
        } else {
            // 실패: 에러 메시지 출력 후 등록 페이지 유지
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("<script>alert('답변 등록에 실패했습니다. 다시 시도해 주세요.'); history.back();</script>");
        }
    }
}