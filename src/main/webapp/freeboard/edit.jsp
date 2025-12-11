<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="../common/header.jsp" %>
<c:set var="board" value="${requestScope.board}" />

<c:if test="${empty board}">
    <script>
        alert('잘못된 접근입니다.');
        location.href='${pageContext.request.contextPath}/freeboard/list.do';
    </script>
</c:if>

<div class="space-medium">
    <div class="container">
        <div class="row">
            <div class="col-lg-12">
                <div class="mb60 section-title">
                    <h1>게시글 수정</h1>
                </div>
                
                <form method="post" action="${pageContext.request.contextPath}/freeboard/edit.do">
                    
                    <input type="hidden" name="idx" value="${board.idx}">
                    
                    <div class="form-group">
                        <label for="title">제목</label>
                        <input type="text" class="form-control" id="title" name="title" required value="${board.title}">
                    </div>
                    
                    <div class="form-group">
                        <label for="content">내용</label>
                        <textarea class="form-control" id="content" name="content" rows="10" required>${board.content}</textarea>
                    </div>
                    
                    <div class="text-right">
                        <button type="submit" class="btn btn-warning">수정 완료</button>
                        <a href="${pageContext.request.contextPath}/freeboard/view.do?idx=${board.idx}" class="btn btn-default">취소</a>
                    </div>
                </form>

            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>