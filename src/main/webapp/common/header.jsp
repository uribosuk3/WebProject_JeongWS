<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- 💡 JSTL URI를 Jakarta EE 표준으로 변경 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %> 

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <title>${empty pageTitle ? '커뮤니티 웹 서비스' : pageTitle}</title> 
    
    <link href="../css/bootstrap.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body>
    
    <%-- ========================================================= --%>
    <%-- 🌐 네비게이션 (메뉴) 영역 시작 --%>
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
          <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">Project Home</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
            <li><a href="${pageContext.request.contextPath}/board/list.do">자유 게시판</a></li>
            <li><a href="${pageContext.request.contextPath}/qna/list.do">Q&A 게시판</a></li>
            <li><a href="${pageContext.request.contextPath}/fileboard/list.do">자료실</a></li>
          </ul>
          
          <ul class="nav navbar-nav navbar-right">
            <c:choose>
                <c:when test="${not empty sessionScope.loginUser}">
                    <%-- 로그인 상태 --%>
                    <li><a href="#">**${sessionScope.loginUser.name}**님 (${sessionScope.loginUser.id})</a></li>
                    
                    <%-- 💡 수정된 부분: 회원정보수정 링크를 MypageServlet URL로 변경 --%>
                    <li><a href="${pageContext.request.contextPath}/member/mypage.do">회원정보수정</a></li>
                    
                    <li><a href="${pageContext.request.contextPath}/auth/logout.do">로그아웃</a></li>
                </c:when>
                <c:otherwise>
                    <%-- 로그아웃 상태 --%>
                    <li><a href="${pageContext.request.contextPath}/member/login.jsp">로그인</a></li>
                    <li><a href="${pageContext.request.contextPath}/member/register.jsp">회원가입</a></li>
                </c:otherwise>
            </c:choose>
          </ul>
        </div></div>
    </nav>
    <%-- ========================================================= --%>
    <%-- 🌐 네비게이션 영역 끝 --%>
    <%-- ========================================================= --%>