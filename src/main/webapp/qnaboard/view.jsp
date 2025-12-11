<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- 💡 공통 Header 포함 --%>
<%@ include file="../common/header.jsp" %>
<%-- header.jsp에서 pageTitle을 사용하므로, Servlet에서 req.setAttribute("pageTitle", "Q&A 게시글 상세");를 해야 합니다. --%>

<c:set var="board" value="${requestScope.board}" />
<c:set var="commentList" value="${requestScope.commentList}" /> <%-- 서블릿에서 넘긴 댓글 리스트 --%>

<%-- 💡 게시글이 없으면 목록으로 리다이렉트 처리 --%>
<c:if test="${empty board}">
    <script>
        alert('존재하지 않는 게시글입니다.');
        location.href='${pageContext.request.contextPath}/qnaboard/list.do';
    </script>
</c:if>

<style>
    /* 기존 스타일 유지 및 자유게시판 스타일 추가 */
    .board-detail-area {
        border: 1px solid #e0e0e0;
        padding: 20px;
        border-radius: 5px;
        margin-bottom: 30px;
        background-color: #ffffff;
    }
    .board-info {
        color: #333;
        font-size: 0.9em;
        margin-bottom: 20px;
        border-bottom: 1px dashed #ddd;
        padding-bottom: 10px;
    }
    .board-info strong {
        color: #000;
    }
    /* 💡 자유게시판 UI 통일을 위한 스타일 (Bootstrap 기본 클래스 가정) */
    .d-flex { display: flex; align-items: center; }
    .ml-2 { margin-left: 0.5rem; }
    .mb-1 { margin-bottom: 0.25rem !important; }
    .list-group-item { display: block; }
</style>
    
    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <div class="mb60 section-title">
                        <h1>Q&A 질문 상세 보기</h1>
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-lg-12">
                    <div class="board-detail-area">
                        
                        <div class="board-detail-header">
                            <h2>${board.title}</h2>
                        </div>
                        
                        <div class="board-info">
                            <span>작성자: **${board.writerName}**</span> | 
                            <span>작성일: <fmt:formatDate value="${board.postdate}" pattern="yyyy.MM.dd HH:mm"/></span> | 
                            <span>조회수: ${board.views}</span>
                        </div>
                        
                        <div class="board-content">
                            <p style="white-space: pre-wrap;">${board.content}</p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row mb30">
                <div class="col-lg-12 text-right">
                    
                    <%-- 💡 수정/삭제 버튼 노출 시 로그인 여부 및 작성자 일치 여부를 모두 체크 --%>
                    <c:if test="${not empty sessionScope.loginUser && sessionScope.loginUser.idx == board.user_idx}">
                        <a href="${pageContext.request.contextPath}/qnaboard/edit.do?idx=${board.idx}" class="btn btn-warning">수정</a>
                        <a href="${pageContext.request.contextPath}/qnaboard/delete.do?idx=${board.idx}" class="btn btn-danger" onclick="return confirm('정말로 삭제하시겠습니까?');">삭제</a>
                    </c:if>
                    
                    <a href="${pageContext.request.contextPath}/qnaboard/list.do" class="btn btn-primary">목록으로</a>
                </div>
            </div>
            
            <%-- ========================================================= --%>
            <%-- 1. 답변 목록 출력 영역 (자유게시판 양식 적용) --%>
            <%-- 💡 순서 변경 및 클래스 이름 변경: 목록을 먼저 배치 --%>
            <%-- ========================================================= --%>
            <div class="comment-list-area" style="margin-top: 50px;">
                
                <%-- 💡 자유게시판의 h3 스타일 적용 --%>
                <h3 style="border-bottom: 1px solid #ddd; padding-bottom: 5px; margin-bottom: 20px;">
                    답변 (<c:out value="${fn:length(commentList)}" default="0" />개)
                </h3>
                
                <div class="list-group mb-4">
                    <c:choose>
                        <c:when test="${empty commentList}">
                            <p class="text-center text-muted">등록된 답변이 없습니다.</p>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="comment" items="${commentList}">
                                <%-- 💡 자유게시판의 list-group-item 스타일 적용 --%>
                                <div class="list-group-item">
                                    <%-- 💡 자유게시판의 d-flex 레이아웃 적용 --%>
                                    <div class="d-flex w-100 justify-content-between">
                                        <h5 class="mb-1">${comment.writerName}</h5>
                                        <small>
                                            <fmt:formatDate value="${comment.postdate}" pattern="yyyy.MM.dd HH:mm"/>
                                            
                                            <%-- 💡 답변 작성자에게만 삭제 버튼 노출 --%>
                                            <c:if test="${not empty sessionScope.loginUser && sessionScope.loginUser.idx == comment.user_idx}">
                                                <a href="${pageContext.request.contextPath}/qnaboard/commentDelete.do?comment_idx=${comment.idx}&board_idx=${board.idx}&pageNum=${param.pageNum}" 
                                                   class="btn btn-sm btn-danger ml-2" style="font-size: 0.75rem;" 
                                                   onclick="return confirm('답변을 삭제하시겠습니까?');">삭제</a>
                                            </c:if>
                                        </small>
                                    </div>
                                    <p class="mb-1" style="white-space: pre-wrap;">${comment.content}</p>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <%-- 2. 답변 작성 폼 영역 (자유게시판 양식 적용) --%>
            <%-- ========================================================= --%>
            <div class="comment-write-area" style="margin-top: 40px;">
                
                <c:if test="${not empty sessionScope.loginUser}">
                    
                    <%-- 🚨 이전의 <h3>답변 작성</h3> 헤더를 제거합니다. --%>
                    
                    <form action="${pageContext.request.contextPath}/qnaboard/commentWrite.do" method="post" class="mb-5">
                        
                        <input type="hidden" name="boardIdx" value="${board.idx}"> 
                        <input type="hidden" name="pageNum" value="${param.pageNum}"> 
                        
                        <div class="form-group">
                            <%-- 💡 '답변 작성' 레이블을 유지하여 폼의 제목 역할을 하게 합니다. --%>
                            <label for="commentContent">답변 작성</label> 
                            <textarea name="content" class="form-control" id="commentContent" rows="3" required placeholder="${sessionScope.loginUser.name}님, 답변을 남겨주세요."></textarea>
                        </div>
                        
                        <div class="text-right">
                            <button type="submit" class="btn btn-success">답변 등록</button>
                        </div>
                    </form>
                </c:if>
                <c:if test="${empty sessionScope.loginUser}">
                    <div class="alert alert-info text-center">
                        답변을 작성하려면 <a href="${pageContext.request.contextPath}/member/login.do">로그인</a>이 필요합니다.
                    </div>
                </c:if>
            </div>
        </div>
    </div>
    
<%-- 💡 공통 Footer 포함 --%>
<%@ include file="../common/footer.jsp" %>