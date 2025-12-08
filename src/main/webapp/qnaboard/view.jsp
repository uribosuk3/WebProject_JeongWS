<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- 💡 Q&A 게시판 상세 보기 화면 (자유게시판 코드를 기반으로 수정) --%>
<%@ include file="../common/header.jsp" %>
<c:set var="board" value="${requestScope.board}" />

<c:if test="${empty board}">
    <script>
        alert('존재하지 않는 게시글입니다.');
        location.href='${pageContext.request.contextPath}/qna/list.do';
    </script>
    <c:return/>
</c:if>

<style>
    /* 💡 CSS는 필요에 따라 header.jsp에서 링크하거나 여기에 직접 작성합니다. */
    .board-view-header { border-bottom: 2px solid #ccc; padding-bottom: 10px; margin-bottom: 20px; }
    .board-view-header h2 { margin-top: 0; }
    .board-info { color: #888; font-size: 0.9em; }
    .board-info span { margin-right: 15px; }
    .board-content { padding: 20px 0; border-bottom: 1px solid #eee; min-height: 200px; white-space: pre-wrap; }
    .comment-section h3 { margin-bottom: 20px; border-bottom: 1px solid #ddd; padding-bottom: 5px; }
    .list-group-item { display: block; }
    .d-flex { display: flex; align-items: center; }
    .ml-2 { margin-left: 0.5rem; }
    /* Q&A 답변 구분을 위한 스타일 */
    .reply-item { background-color: #f7f7f7; border-left: 5px solid #007bff; margin-left: 20px; }
</style>

<div class="space-medium">
    <div class="container">
        <div class="row">
            <div class="col-lg-12">
                
                <div class="mb60 section-title">
                    <h1>Q&A 질문 상세 보기</h1>
                </div>
                
                <div class="board-view">
                    <div class="board-view-header">
                        <h2>${board.title}</h2>
                        <div class="board-info">
                            <span>작성자: **${board.writerName}**</span>
                            <span>작성일: <fmt:formatDate value="${board.postdate}" pattern="yyyy.MM.dd HH:mm"/></span>
                            <span>조회수: ${board.views}</span>
                            <%-- 💡 Q&A는 보통 추천 기능이 없으므로 주석 처리하거나 제거 (여기서는 제거) --%>
                        </div>
                    </div>

                    <div class="board-content">
                        ${board.content}
                    </div>
                </div>
                
                <div class="text-right mt-4">
                    <%-- 💡 1. 로그인 사용자이고, 게시글의 작성자라면 수정/삭제 버튼 노출 --%>
                    <c:if test="${not empty sessionScope.loginUser && sessionScope.loginUser.idx == board.user_idx}">
                        <a href="${pageContext.request.contextPath}/qna/modify.do?idx=${board.idx}&pageNum=${pageNum}" class="btn btn-warning">수정</a>
                        <a href="${pageContext.request.contextPath}/qna/delete.do?idx=${board.idx}&pageNum=${pageNum}" class="btn btn-danger" onclick="return confirm('정말로 삭제하시겠습니까?');">삭제</a>
                    </c:if>

                    <%-- 💡 2. 원글에만 '답변하기' 버튼 노출 (parent_idx가 0인 경우) --%>
                    <c:if test="${board.parent_idx == 0 && not empty sessionScope.loginUser}">
                        <a href="${pageContext.request.contextPath}/qna/reply.do?idx=${board.idx}" class="btn btn-primary">답변하기</a>
                    </c:if>
                    
                    <%-- 💡 3. 목록 링크 --%>
                    <a href="${pageContext.request.contextPath}/qna/list.do?pageNum=${empty pageNum ? '1' : pageNum}" class="btn btn-default">목록으로</a>
                </div>
                
                <hr>
                
                <%-- 💡 4. Q&A 답글 목록 출력 (boardList에서 답글만 필터링 또는 별도 리스트 사용) --%>
                <div class="comment-section">
                    <h3>답글 (${replyList.size()}개)</h3>
                    
                    <div class="list-group mb-4">
                        <c:choose>
                            <c:when test="${empty replyList}">
                                <p class="text-center text-muted">등록된 답변이 없습니다.</p>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="reply" items="${replyList}">
                                    <%-- 💡 답글일 경우 배경색을 다르게 표시 --%>
                                    <div class="list-group-item reply-item"> 
                                        <div class="d-flex w-100 justify-content-between">
                                            <h5 class="mb-1">[답변] ${reply.title}</h5>
                                            <small>
                                                <fmt:formatDate value="${reply.postdate}" pattern="yyyy.MM.dd HH:mm"/>
                                                
                                                <c:if test="${not empty sessionScope.loginUser && sessionScope.loginUser.idx == reply.user_idx}">
                                                    <a href="${pageContext.request.contextPath}/qna/modify.do?idx=${reply.idx}&pageNum=${pageNum}" 
                                                        class="btn btn-sm btn-warning ml-2" style="font-size: 0.75rem;">수정</a>
                                                    <a href="${pageContext.request.contextPath}/qna/delete.do?idx=${reply.idx}&pageNum=${pageNum}" 
                                                        class="btn btn-sm btn-danger ml-2" style="font-size: 0.75rem;" 
                                                        onclick="return confirm('답변을 삭제하시겠습니까?');">삭제</a>
                                                </c:if>
                                            </small>
                                        </div>
                                        <p class="mb-1" style="white-space: pre-wrap;">${reply.content}</p>
                                        <small class="text-muted">작성자: ${reply.writerName}</small>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>