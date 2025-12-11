<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> <%-- 💡 JSTL Functions 라이브러리 추가 --%>

<%@ include file="../common/header.jsp" %>
<%-- header.jsp에서 pageTitle을 사용하므로, Servlet에서 req.setAttribute("pageTitle", "Q&A 답변 작성");을 해야 합니다. --%>

<c:if test="${empty sessionScope.loginUser}">
    <script>
        alert('로그인 후 이용 가능합니다.');
        location.href='${pageContext.request.contextPath}/login.jsp';
    </script>
</c:if>

<c:set var="parent_idx" value="${param.idx}"/>
<%-- 💡 Controller(QnaBoardReplyServlet)에서 원글 정보를 가져와 request에 저장하는 것이 권장되지만, 
     현재 구조 유지를 위해 jsp:useBean을 사용합니다. --%>
<jsp:useBean id="qnaDao" class="model.dao.QnaBoardDAO" scope="application"/>
<c:set var="parentBoard" value="${qnaDao.selectBoard(parent_idx)}"/>

<c:if test="${empty parentBoard}">
    <script>
        alert('존재하지 않는 원본 질문입니다.');
        location.href='${pageContext.request.contextPath}/qna/list.do';
    </script>
    <c:return/>
</c:if>

<%-- 💡 기존 HTML, HEAD, BODY 및 링크 태그는 header/footer.jsp에서 처리됨 --%>

    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <div class="mb60 section-title">
                        <h1>Q&A 답변 작성</h1>
                    </div>
                    
                    <div class="alert alert-info" role="alert">
                        <strong>원본 질문 제목:</strong> ${parentBoard.title}
                        <br>
                        <%-- 💡 fn:substring 사용 가능 --%>
                        <strong>원본 질문 내용:</strong> <span style="white-space: pre-wrap;">${fn:substring(parentBoard.content, 0, 100)}${fn:length(parentBoard.content) > 100 ? '...' : ''}</span>
                    </div>
                    
                    <form method="post" action="${pageContext.request.contextPath}/qna/reply.do">
                        
                        <input type="hidden" name="parent_idx" value="${parent_idx}">
                        
                        <div class="form-group">
                            <label for="title">답변 제목</label>
                            <input type="text" class="form-control" id="title" name="title" required value="RE: ${parentBoard.title}" placeholder="답변 제목을 입력하세요">
                        </div>
                        
                        <div class="form-group">
                            <label for="content">답변 내용</label>
                            <textarea class="form-control" id="content" name="content" rows="10" required placeholder="답변 내용을 입력하세요."></textarea>
                        </div>
                        
                        <div class="text-right">
                            <button type="submit" class="btn btn-primary">답변 등록</button>
                            <a href="${pageContext.request.contextPath}/qna/view.do?idx=${parent_idx}" class="btn btn-default">취소</a>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    </div>
    
<%-- 💡 기존 script 태그 및 닫는 태그 제거하고 공통 Footer 포함 --%>
<%@ include file="../common/footer.jsp" %>