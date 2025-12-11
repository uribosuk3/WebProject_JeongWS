package servlet.auth;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.UsersDAO;
import model.dto.UsersDTO;

@WebServlet("/member/update.do")
public class UpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 회원 정보 수정 페이지 요청 (GET):
	 * 보통은 mypage.jsp로 리다이렉트하거나 포워드하지만, 
	 * 이미 mypage.jsp에서 로그인 체크를 하므로 여기서는 POST만 처리합니다.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// GET 요청이 오면 mypage.jsp로 보냅니다.
		resp.sendRedirect(req.getContextPath() + "/member/mypage.jsp");
	}

	/**
	 * 회원 정보 수정 처리 (POST)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		// 1. 인코딩 설정 및 세션 객체 가져오기
		req.setCharacterEncoding("UTF-8");
		HttpSession session = req.getSession();
		
		// 2. 로그인된 사용자 정보 가져오기 (세션 체크)
		UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");
		
		// 로그인 체크: 세션이 없으면 로그인 페이지로 리다이렉트 (이중 체크)
		if (loginUser == null) {
			resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
			return;
		}

		// 3. 폼 파라미터 받기
		// ID는 hidden 필드로 넘어온 기존 ID를 사용합니다.
		String id = req.getParameter("id"); 
		String newPw = req.getParameter("pw"); // 새 비밀번호 (값이 없을 수도 있음)
		String name = req.getParameter("name");
		String email = req.getParameter("email");
		String phone = req.getParameter("phone");

		// 4. DTO 객체 준비
		// DTO에 수정할 정보를 담습니다. (ID는 수정 조건, 나머지는 수정 내용)
		UsersDTO updateDto = new UsersDTO();
		updateDto.setId(id);
		updateDto.setName(name);
		updateDto.setEmail(email);
		updateDto.setPhone(phone);
		
		// 5. 비밀번호 처리: 새 비밀번호가 입력되었을 경우에만 DTO에 설정합니다.
		// (만약 비밀번호 암호화 로직이 있다면 여기서 처리해야 합니다.)
		if (newPw != null && !newPw.trim().isEmpty()) {
			updateDto.setPw(newPw); 
		} else {
			// 새 비밀번호가 입력되지 않았다면 기존 비밀번호를 유지합니다.
			// (DAO에서 이 로직을 처리하도록 구현해야 함)
			updateDto.setPw(loginUser.getPw());
		}

		// 6. DAO를 통한 DB 업데이트
		UsersDAO dao = UsersDAO.getInstance();
		boolean result = dao.updateUser(updateDto);

		if (result) {
			// 7. 업데이트 성공: 세션 정보 갱신 및 리다이렉트
			
			// 💡 DB에서 갱신된 최신 정보를 다시 조회해 세션에 저장하는 것이 안전합니다.
			// (DAO의 updateUser()에서 비밀번호를 업데이트하지 않았을 경우를 대비)
			UsersDTO updatedUser = dao.getUserById(id);
			
			if (updatedUser != null) {
				// 성공적으로 조회되면 세션 갱신
				session.setAttribute("loginUser", updatedUser);
				// 메인 페이지로 이동
				resp.sendRedirect(req.getContextPath() + "/index.jsp");
			} else {
				// 갱신 후 조회 실패는 비정상 상황
				req.setAttribute("updateMessage", "회원 정보는 수정되었으나, 세션 정보 갱신에 실패했습니다.");
				req.getRequestDispatcher("/member/mypage.jsp").forward(req, resp);
			}

		} else {
			// 8. 업데이트 실패: 메시지 전달 후 mypage.jsp로 포워드
			req.setAttribute("updateMessage", "회원 정보 수정에 실패했습니다. 다시 시도해 주세요.");
			req.getRequestDispatcher("/member/mypage.jsp").forward(req, resp);
		}
	}
}