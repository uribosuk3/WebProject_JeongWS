package member;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dao.UsersDAO;
import model.dto.UsersDTO;

@WebServlet("/member/mypage.do") // 💡 /member/mypage.do로 매핑
public class MypageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // GET: 회원 정보 조회 (수정 폼 보여주기)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            // 로그인되어 있지 않으면 로그인 페이지로 리다이렉트
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
            return;
        }

        // 1. 현재 로그인 사용자 정보 가져오기 (세션)
        UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");
        String userId = loginUser.getId();

        // 2. DAO를 통해 최신 사용자 정보 조회
        UsersDAO dao = UsersDAO.getInstance();
        UsersDTO userDetails = dao.getUserById(userId); 
        
        if (userDetails != null) {
            // 3. Request에 담아 mypage.jsp로 포워드
            req.setAttribute("userDetails", userDetails);
            // 💡 mypage.jsp의 실제 위치로 포워드 (예: /member/mypage.jsp)
            req.getRequestDispatcher("/member/mypage.jsp").forward(req, resp);
        } 
        else {
            // 사용자 정보를 찾지 못할 경우 처리
            session.invalidate(); // 세션 무효화
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
        }
    }
    
    // POST: 회원 정보 수정 처리
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
            return;
        }

        // 1. 세션에서 현재 ID 가져오기
        UsersDTO currentLoginUser = (UsersDTO) session.getAttribute("loginUser");
        String id = currentLoginUser.getId();

        // 2. 파라미터 추출
        String pw = req.getParameter("pw");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        
        // 3. DTO 생성 및 업데이트
        UsersDTO updateUser = new UsersDTO();
        updateUser.setId(id);
        updateUser.setPw(pw);
        updateUser.setName(name);
        updateUser.setEmail(email);
        updateUser.setPhone(phone);

        UsersDAO dao = UsersDAO.getInstance();
        boolean isSuccess = dao.updateUser(updateUser); // DAO에 updateUser 메서드 구현 필요

        if (isSuccess) {
            // 4. 세션 정보 업데이트 및 성공 메시지 전달
            // 비밀번호를 제외한 최신 정보로 세션 업데이트 (DB에서 다시 조회하는 것이 안전)
            UsersDTO newLoginUser = dao.getUserById(id);
            session.setAttribute("loginUser", newLoginUser); 
            
            session.setAttribute("updateMsg", "회원 정보가 성공적으로 수정되었습니다.");
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        } else {
            // 실패 시, 다시 마이페이지로 포워드
            req.setAttribute("errorMsg", "정보 수정에 실패했습니다.");
            req.getRequestDispatcher("/member/mypage.jsp").forward(req, resp);
        }
    }
}
