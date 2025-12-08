<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- 💡 공통 헤더/푸터 적용을 위해 기존의 HTML, <head>, <body> 및 헤더/푸터 DIV 태그를 제거하고 include로 대체 --%>
<%-- header.jsp에서 pageTitle을 사용하므로, Servlet에서 req.setAttribute("pageTitle", "새 게시글 작성");을 해야 합니다. --%>

<c:set var="loginUser" value="${sessionScope.loginUser}" />
<c:if test="${empty loginUser}">
    <script>
        alert('글을 작성하려면 로그인해야 합니다.');
        location.href='${pageContext.request.contextPath}/login.jsp';
    </script>
    <c:return/>
</c:if>

    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <div class="mb60 section-title">
                        <h1>새 게시글 작성</h1>
                        <c:if test="${not empty errorMsg}">
                            <p style="color: red; font-weight: bold;">${errorMsg}</p>
                        </c:if>
                    </div>

                    <form method="post" action="${pageContext.request.contextPath}/board/write.do">
                        
                        <input type="hidden" name="user_idx" value="${loginUser.idx}">
                        
                        <div class="form-group">
                            <label for="title">제목</label>
                            <input type="text" class="form-control" id="title" name="title" required value="${param.title}">
                        </div>

                        <div class="form-group">
                            <label for="content">내용</label>
                            <textarea class="form-control" id="content" name="content" rows="10" required>${param.content}</textarea>
                        </div>
                        
                        <div class="text-right">
                            <button type="submit" class="btn btn-primary">등록하기</button>
                            <a href="${pageContext.request.contextPath}/board/list.do" class="btn btn-default">목록으로</a>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    </div>
