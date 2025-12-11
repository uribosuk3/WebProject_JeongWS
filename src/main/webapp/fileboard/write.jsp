<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="model.dto.UsersDTO" %>
<% 
    // 로그인 체크는 write.jsp 상단에서 유지하는 것이 좋습니다.
    UsersDTO loginUser = (UsersDTO) session.getAttribute("loginUser");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/member/login.jsp");
        return;
    }
    // DTO import와 로그인 체크는 그대로 유지
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>자료실 게시글 작성</title>
    <link href="<%= request.getContextPath() %>/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>

<%@ include file="/common/header.jsp" %>

<div class="container" style="padding-top: 50px;">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <h2 class="text-center">📚 자료실 게시글 작성</h2>
            <hr>
            
            <form action="<%= request.getContextPath() %>/fileboard/write.do" 
                  method="post" 
                  enctype="multipart/form-data">
                
                <div class="form-group">
                    <label for="title">제목</label>
                    <input type="text" class="form-control" id="title" name="title" 
                           placeholder="제목을 입력하세요" required>
                </div>
                
                <div class="form-group">
                    <label for="content">내용</label>
                    <textarea class="form-control" id="content" name="content" 
                              rows="10" placeholder="내용을 입력하세요" required></textarea>
                </div>
                
                <div class="form-group">
                    <label for="file">첨부 파일</label>
                    <input type="file" class="form-control" id="file" name="file" required>
                    <p class="help-block">최대 20MB까지 업로드 가능합니다.</p>
                </div>
                
                <div class="form-group text-center">
                    <button type="submit" class="btn btn-primary">등록하기</button>
                    <button type="button" class="btn btn-default" onclick="location.href='<%= request.getContextPath() %>/fileboard/list.do'">목록으로</button>
                </div>
            </form>
        </div>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>

</body>
</html>