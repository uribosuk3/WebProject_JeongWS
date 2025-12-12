<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="model.dto.UsersDTO" %> 

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <style>
        table th {
            white-space: nowrap;
            word-break: keep-all;
    </style>
    
    <title>${empty pageTitle ? '커뮤니티 웹 서비스' : pageTitle}</title> 
    
    <link href="<%= request.getContextPath() %>/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
    
    <%-- ========================================================= --%>
    <%-- 네비게이션 (메뉴) 영역 시작 --%>
    <%-- ========================================================= --%>
    <nav class="navbar navbar-default">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="<%= request.getContextPath() %>/index.jsp">Home</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
            <li><a href="<%= request.getContextPath() %>/freeboard/list.do">자유 게시판</a></li>
            <li><a href="<%= request.getContextPath() %>/qnaboard/list.do">Q&A 게시판</a></li>
            <li><a href="<%= request.getContextPath() %>/fileboard/list.do">자료실</a></li>
          </ul>
          
          <ul class="nav navbar-nav navbar-right">
            
            <% 
            // 1. 세션에서 "loginUser" 객체를 가져옵니다.
            Object loginUserObj = session.getAttribute("loginUser");
            
            if (loginUserObj == null) { 
            // 2. 로그아웃 상태: "loginUser" 객체가 세션에 없으면
            %>
                 <li><a href="<%= request.getContextPath() %>/member/login.jsp">로그인</a></li>
                 <li><a href="<%= request.getContextPath() %>/member/register.jsp">회원가입</a></li>
            <% 
            } 
            else { 
                // 3. 로그인 성공 상태: "loginUser" 객체가 세션에 있으면
                String userName = ((UsersDTO) loginUserObj).getName();
            %>
                 <li>
                    <span class="navbar-text">
                        <strong><%= userName %>님</strong>
                    </span>
                 </li>
                 <li><a href="<%= request.getContextPath() %>/member/mypage.jsp">회원정보수정</a></li>
                 <li><a href="<%= request.getContextPath() %>/member/logout.do">로그아웃</a></li> 
            <% 
            } 
            %>
            
          </ul>
    </nav>
    <%-- ========================================================= --%>
    <%-- 네비게이션 영역 끝 --%>
    <%-- ========================================================= --%>