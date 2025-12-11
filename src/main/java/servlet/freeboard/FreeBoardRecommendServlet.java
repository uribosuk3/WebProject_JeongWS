package servlet.freeboard;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// GSON 임포트 제거됨
import model.dao.RecommendDAO;
import model.dto.UsersDTO;

@WebServlet("/freeboard/recommend.do")
public class FreeBoardRecommendServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Content-Type을 JSON으로 설정
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        
        Map<String, Object> result;
        String jsonResponse = ""; // JSON 문자열을 저장할 변수

        // 1. 로그인 인증
        HttpSession session = req.getSession(false);
        UsersDTO loginUser = (session != null) ? (UsersDTO) session.getAttribute("loginUser") : null;

        if (loginUser == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
            // JSON 문자열 직접 생성
            jsonResponse = "{\"success\":false, \"message\":\"로그인이 필요합니다.\"}";
            out.print(jsonResponse);
            out.flush();
            return;
        }

        // 2. 파라미터 추출
        String boardIdxStr = req.getParameter("idx");
        int board_idx = 0;
        
        try {
            board_idx = Integer.parseInt(boardIdxStr);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            jsonResponse = "{\"success\":false, \"message\":\"잘못된 게시글 번호입니다.\"}";
            out.print(jsonResponse);
            out.flush();
            return;
        }
        
        // 3. DAO 호출 및 좋아요 처리
        RecommendDAO dao = RecommendDAO.getInstance();
        // type 'free', 게시글 ID, 사용자 ID 전달
        result = dao.toggleLike("free", board_idx, loginUser.getIdx());
        
        // 4. 응답 전송 및 JSON 문자열 생성
        if ((boolean)result.get("success")) {
            // 성공 응답 (200 OK)
            
            // Map에서 데이터를 가져와 JSON 문자열로 포맷팅
            boolean isLiked = (boolean) result.get("isLiked");
            int newCount = (int) result.get("newCount");
            
            // 🚨 String.format을 이용한 JSON 문자열 생성 (GSON 없이 구현하는 핵심)
            jsonResponse = String.format(
                "{\"success\":true, \"isLiked\":%b, \"newCount\":%d, \"message\":\"처리 완료\"}", 
                isLiked, newCount
            );
            
            out.print(jsonResponse);
        } else {
            // DB 처리 실패 시 500 Internal Server Error
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            
            // 실패 JSON 문자열 직접 생성
            jsonResponse = "{\"success\":false, \"message\":\"DB 처리 중 오류가 발생했습니다.\"}";
            out.print(jsonResponse);
        }
        out.flush();
    }
}