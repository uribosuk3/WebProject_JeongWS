<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="model.dto.UsersDTO" %>
<% 
    // 💡 로그인 여부 확인
    UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");

    // 세션에 loginUser 객체가 없으면 (로그인되지 않았으면) 로그인 페이지로 리다이렉트
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/member/login.jsp");
        return; // 코드 실행 중단
    }

    // 폼에 표시할 현재 사용자 정보를 DTO에서 가져옵니다.
    String currentId = loginUser.getId();
    String currentName = loginUser.getName();
    String currentEmail = loginUser.getEmail();
    String currentPhone = loginUser.getPhone();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원 정보 수정</title>
    <link href="<%= request.getContextPath() %>/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>

<%-- 💡 header.jsp를 포함하여 상단 메뉴와 레이아웃을 가져옵니다. --%>
<%@ include file="/common/header.jsp" %>

<div class="container" style="padding-top: 50px;">
    <div class="row">
        <div class="col-md-6 col-md-offset-3">
            <h2 class="text-center">회원 정보 수정</h2>
            
            <%-- 
                💡 UpdateServlet으로 POST 요청을 보낼 폼입니다.
                나중에 /member/update.do 서블릿을 만들 것입니다.
            --%>
            <form action="<%= request.getContextPath() %>/member/update.do" method="post" class="form-horizontal">
                
                <%-- ID는 수정할 수 없으므로 disabled 처리하고 hidden으로 다시 보냅니다. --%>
                <div class="form-group">
                    <label for="id" class="col-sm-3 control-label">아이디</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" id="id" name="id" 
                               value="<%= currentId %>" disabled>
                        <%-- 실제로 서버에 전송될 ID --%>
                        <input type="hidden" name="id" value="<%= currentId %>"> 
                    </div>
                </div>

                <div class="form-group">
                    <label for="pw" class="col-sm-3 control-label">새 비밀번호</label>
                    <div class="col-sm-9">
                        <input type="password" class="form-control" id="pw" name="pw" 
                               placeholder="변경할 비밀번호를 입력하세요 (필수)">
                    </div>
                </div>

                <div class="form-group">
                    <label for="name" class="col-sm-3 control-label">이름</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" id="name" name="name" 
                               value="<%= currentName %>" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="email" class="col-sm-3 control-label">이메일</label>
                    <div class="col-sm-9">
                        <input type="email" class="form-control" id="email" name="email" 
                               value="<%= currentEmail %>" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="phone" class="col-sm-3 control-label">전화번호</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" id="phone" name="phone" 
                               value="<%= currentPhone %>" required>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-sm-offset-3 col-sm-9">
                        <button type="submit" class="btn btn-primary">정보 수정</button>
                        <button type="button" class="btn btn-default" onclick="location.href='<%= request.getContextPath() %>/index.jsp'">취소</button>
                    </div>
                </div>
                
                <%-- 🚨 수정 실패 시 메시지 표시 (나중에 추가) --%>
                <% if (request.getAttribute("updateMessage") != null) { %>
                    <p class="text-danger text-center"><%= request.getAttribute("updateMessage") %></p>
                <% } %>
            </form>
        </div>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
</body>
</html>