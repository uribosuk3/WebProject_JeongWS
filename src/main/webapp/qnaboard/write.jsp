<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- 💡 공통 헤더 포함 --%>
<%@ include file="../common/header.jsp" %> 

<%-- 1. Controller에서 전달받은 원글 DTO (답변 모드일 때만 존재)를 변수로 설정합니다. --%>
<c:set var="parent" value="${requestScope.parentBoard}" />
<%-- 2. 답변 모드인지 확인하는 플래그를 설정합니다. --%>
<c:set var="isReply" value="${not empty parent}" />

<style>
    .board-form-container { max-width: 900px; margin: 0 auto; padding: 30px; border: 1px solid #ddd; border-radius: 5px; background-color: #fff; }
</style>

<div class="space-medium">
    <div class="container">
        <div class="row">
            <div class="col-lg-12">
                
                <div class="mb60 section-title">
                    <c:choose>
                        <c:when test="${isReply}">
                            <h1>Q&A 답변 작성</h1>
                        </c:when>
                        <c:otherwise>
                            <h1>Q&A 질문 작성</h1>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="board-form-container">
                    
                    <%-- 💡 폼 액션 분기: 답변 모드면 reply.do, 신규 작성 모드면 write.do --%>
                    <form method="post" action="${pageContext.request.contextPath}/qnaboard/${isReply ? 'reply.do' : 'write.do'}">
                        
                        <%-- 💡 답변 모드일 때만 원글의 IDX를 서버로 다시 보내기 위한 필수 HIDDEN 필드 --%>
                        <c:if test="${isReply}">
                            <input type="hidden" name="parent_idx" value="${parent.idx}">
                            
                            <%-- 💡 답변 대상 정보 표시 --%>
                            <div class="form-group">
                                <label>답변 대상 질문</label>
                                <input type="text" class="form-control" 
                                       value="${parent.title} (작성자: ${parent.writerName})" readonly>
                            </div>
                        </c:if>

                        <div class="form-group">
                            <label for="title">제목</label>
                            <%-- 💡 답변 모드일 경우 제목에 'RE: '를 기본값으로 설정 --%>
                            <input type="text" id="title" name="title" class="form-control" 
                                   value="${isReply ? 'RE: ' : ''}" required>
                        </div>

                        <div class="form-group">
                            <label for="content">내용</label>
                            <textarea id="content" name="content" class="form-control" rows="10" required></textarea>
                        </div>
                        
                        <div class="text-right">
                            <%-- 💡 버튼 텍스트도 모드에 따라 변경 --%>
                            <button type="submit" class="btn btn-primary">
                                ${isReply ? '답변 등록' : '질문 등록'}
                            </button>
                            <%-- 취소 버튼은 항상 목록으로 돌아가거나 이전 페이지로 이동 --%>
                            <a href="${pageContext.request.contextPath}/qnaboard/list.do" class="btn btn-default">취소 / 목록</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%-- 💡 공통 푸터 포함 --%>
<%@ include file="../common/footer.jsp" %>