package Servlet.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; 
import java.io.IOException;

import model.dao.UsersDAO;
import model.dto.UsersDTO;

@WebServlet("/auth/register.do") // 💡 /auth/ 경로로 통일
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        // 1. 인코딩 설정 (POST 요청 필수)
        req.setCharacterEncoding("UTF-8");
        
        // 2. 요청 파라미터 추출
        String name = req.getParameter("name");
        String id = req.getParameter("id");
        String pw = req.getParameter("pw");
        String confirmPw = req.getParameter("confirm_pw"); 
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        
        // 3. 유효성 검사 및 데이터 준비
        UsersDAO dao = UsersDAO.getInstance();

        // **유효성 검사 시작:**
        
        // 필수 값 누락 확인
        if (id == null || id.isEmpty() || pw == null || pw.isEmpty() || 
            name == null || name.isEmpty() || phone == null || phone.isEmpty()) {
            
            String errorMessage = "모든 필수 정보를 입력해 주세요.";
            req.setAttribute("error", errorMessage);
            req.getRequestDispatcher("/member/register.jsp").forward(req, resp);
            return;
        }

        // 비밀번호 일치 여부 확인
        if (!pw.equals(confirmPw)) {
            String errorMessage = "비밀번호가 일치하지 않습니다.";
            req.setAttribute("error", errorMessage);
            // 입력값 유지
            req.setAttribute("inputName", name);
            req.setAttribute("inputId", id);
            req.setAttribute("inputEmail", email);
            req.setAttribute("inputPhone", phone);
            
            req.getRequestDispatcher("/member/register.jsp").forward(req, resp);
            return;
        }
        
        // 아이디 중복 확인
        if (dao.isIdDuplicate(id)) {
            String errorMessage = "이미 존재하는 아이디입니다.";
            req.setAttribute("error", errorMessage);
            // 입력값 유지
            req.setAttribute("inputName", name);
            req.setAttribute("inputId", id);
            req.setAttribute("inputEmail", email);
            req.setAttribute("inputPhone", phone);
            
            req.getRequestDispatcher("/member/register.jsp").forward(req, resp);
            return;
        }

        // 4. DTO 생성 및 값 설정
        UsersDTO newUser = new UsersDTO();
        newUser.setId(id);
        newUser.setPw(pw);
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPhone(phone); 
        
        // 5. DB 저장 및 결과 처리
        boolean isSuccess = dao.insertUser(newUser);

        if (isSuccess) {
            // 성공 시, 로그인 페이지로 이동하며 성공 메시지를 세션에 저장
            HttpSession session = req.getSession();
            session.setAttribute("registerSuccessMsg", name + "님, 회원가입이 완료되었습니다. 로그인해 주세요.");
            
            // 💡 /member/login.jsp로 리다이렉트 (ContextPath 사용)
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
        } else {
            // DB 저장 자체 실패 시
            String errorMessage = "회원가입에 실패했습니다. 잠시 후 다시 시도해 주세요.";
            req.setAttribute("error", errorMessage);
            
            // 💡 /member/register.jsp로 포워드
            req.getRequestDispatcher("/member/register.jsp").forward(req, resp);
        }
    }
}