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
</c:if>

<%@ include file="/common/header.jsp" %>

    <div class="container" style="padding-top: 50px;">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <h2 class="text-center">새 게시글 작성</h2>
            <hr>
            
            <form action="write.do" method="post">
                
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
                
                <div class="form-group text-center">
                    <button type="submit" class="btn btn-primary">등록하기</button>
                    <button type="button" class="btn btn-default" onclick="location.href='list.do'">목록으로</button>
                </div>
            </form>
        </div>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>