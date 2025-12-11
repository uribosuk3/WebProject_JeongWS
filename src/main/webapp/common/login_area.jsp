<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<div class="login-box-container outline pinside-reset"> 
    
    <h5 class="text-center no-wrap-title" style="margin-bottom: 20px; color: #333;">회원 서비스 이용을 위해 로그인하세요</h5>
    
    <div class="login-style-box" style="margin-bottom: 20px;">
        <a href="${pageContext.request.contextPath}/member/login.jsp" class="btn btn-block primary-btn">
            로그인하기
        </a>
    </div>
    
    <div class="text-center small-links minimal-spacing no-wrap-links"> 
        <a href="${pageContext.request.contextPath}/member/login.jsp">아이디 찾기</a> 
        <span style="color: #ccc;">|</span>
        <a href="${pageContext.request.contextPath}/member/login.jsp">비밀번호 찾기</a>
        <span style="color: #ccc;">|</span>
        <a href="${pageContext.request.contextPath}/member/register.jsp">회원가입</a>
    </div>
    
</div>

<style>
/* 💡 login_area 전용 스타일 */
.login-box-container {
    background-color: #ffffff;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    
    /* 🚨 최종 해결: 컨테이너를 벗어나는 내용을 숨겨서 삐져나옴 방지 */
    overflow-x: hidden; 
    
    /* 최대 너비 설정 유지 */
    max-width: 350px; 
}

/* 🚨 1. 외부 패딩 최소화 클래스 (공간 확보) */
.pinside-reset {
    padding: 15px !important; 
}

/* 🚨 2. 제목 (h5) : 한 줄 유지 + 말줄임표 처리 */
.no-wrap-title {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    display: block; 
}

/* 🚨 3. 링크 그룹 (div) : 한 줄 유지 (어떤 너비에서도 줄바꿈 방지) */
.no-wrap-links {
    white-space: nowrap; /* 줄바꿈을 강제로 막음 */
    /* overflow: hidden; 은 상위 컨테이너에 적용되어 삐져나옴이 방지됨 */
}

/* 4. 링크 스타일 */
.small-links {
    font-size: 14px;
}
.small-links a {
    color: #555;
    text-decoration: none;
    /* 링크 간 여백 최소화 */
    padding: 0 0px; 
    margin: 0 3px; 
}
.small-links a:hover {
    text-decoration: underline;
    color: #007bff;
}

/* 5. 로그인 버튼 스타일 (이전과 동일) */
.primary-btn {
    background-color: #007bff;
    border-color: #007bff;
    color: white;
    font-weight: bold;
    padding: 12px 0;
    font-size: 18px;
    display: block; 
    text-decoration: none; 
}
.primary-btn:hover {
    background-color: #0056b3;
    border-color: #0056b3;
    color: white;
}
</style>