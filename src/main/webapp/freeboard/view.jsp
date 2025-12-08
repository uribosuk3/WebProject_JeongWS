<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- 💡 1. 공통 Header 포함 --%>
<%@ include file="../common/header.jsp" %> 
<%-- header.jsp에서 pageTitle을 사용하므로, ViewServlet에서 req.setAttribute("pageTitle", board.title + " - 게시글 상세");를 해야 합니다. --%>

<c:if test="${empty board}">
    <script>
        alert('존재하지 않는 게시글입니다.');
        location.href='${pageContext.request.contextPath}/board/list.do';
    </script>
    <c:return/>
</c:if>

<%-- 💡 2. <HTML>, <HEAD>, <body> 시작 태그 및 Header 관련 DIV 제거 --%>

    <style>
        /* 💡 3. CSS 파일의 경로는 header.jsp에서 처리하고, 개별 스타일만 남깁니다. */
        .board-view-header { border-bottom: 2px solid #ccc; padding-bottom: 10px; margin-bottom: 20px; }
        .board-view-header h2 { margin-top: 0; }
        .board-info { color: #888; font-size: 0.9em; }
        .board-info span { margin-right: 15px; }
        .board-content { padding: 20px 0; border-bottom: 1px solid #eee; min-height: 200px; white-space: pre-wrap; }
        .comment-section h3 { margin-bottom: 20px; border-bottom: 1px solid #ddd; padding-bottom: 5px; }
        .list-group-item { display: block; }
        .d-flex { display: flex; align-items: center; }
        .ml-2 { margin-left: 0.5rem; }
    </style>
    
    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    
                    <div class="mb60 section-title">
                        <h1>게시글 상세 보기</h1>
                    </div>
                    
                    <div class="board-view">
                        <div class="board-view-header">
                            <h2>${board.title}</h2>
                            <div class="board-info">
                                <span>작성자: **${board.writerName}**</span>
                                <span>작성일: <fmt:formatDate value="${board.postdate}" pattern="yyyy.MM.dd HH:mm"/></span>
                                <span>조회수: ${board.views}</span>
                                <span>추천: ${board.likes}</span>
                            </div>
                        </div>

                        <div class="board-content">
                            ${board.content}
                        </div>
                    </div>
                    
                    <div class="text-center my-4">
                        <c:choose>
                            <c:when test="${not empty sessionScope.loginUser}">
                                <a href="${pageContext.request.contextPath}/board/like.do?idx=${board.idx}&pageNum=${pageNum}" 
                                   class="btn btn-lg ${isLiked ? 'btn-danger' : 'btn-info'}">
                                    <span class="glyphicon glyphicon-thumbs-up"></span> 
                                    ${isLiked ? '추천 취소' : '추천하기'} (${board.likes})
                                </a>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-lg btn-default disabled" title="로그인이 필요합니다">
                                    <span class="glyphicon glyphicon-thumbs-up"></span> 
                                    추천 (${board.likes})
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="text-right mt-4">
                        <c:if test="${not empty sessionScope.loginUser && sessionScope.loginUser.idx == board.user_idx}">
                            <a href="${pageContext.request.contextPath}/board/edit.do?idx=${board.idx}&pageNum=${pageNum}" class="btn btn-warning">수정</a>
                            <a href="${pageContext.request.contextPath}/board/delete.do?idx=${board.idx}&pageNum=${pageNum}" class="btn btn-danger" onclick="return confirm('정말로 삭제하시겠습니까?');">삭제</a>
                        </c:if>
                        
                        <a href="${pageContext.request.contextPath}/board/list.do?pageNum=${empty pageNum ? '1' : pageNum}" class="btn btn-default">목록으로</a>
                    </div>
                    
                    <hr>
                    
                    <div class="comment-section">
                        <h3>댓글 (${commentList.size()}개)</h3>
                        
                        <div class="list-group mb-4">
                            <c:choose>
                                <c:when test="${empty commentList}">
                                    <p class="text-center text-muted">등록된 댓글이 없습니다.</p>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="comment" items="${commentList}">
                                        <div class="list-group-item">
                                            <div class="d-flex w-100 justify-content-between">
                                                <h5 class="mb-1">${comment.writerName}</h5>
                                                <small>
                                                    <fmt:formatDate value="${comment.postdate}" pattern="yyyy.MM.dd HH:mm"/>
                                                    
                                                    <c:if test="${not empty sessionScope.loginUser && sessionScope.loginUser.idx == comment.user_idx}">
                                                        <a href="${pageContext.request.contextPath}/comment/delete.do?comment_idx=${comment.idx}&board_idx=${board.idx}&pageNum=${pageNum}" 
                                                           class="btn btn-sm btn-danger ml-2" style="font-size: 0.75rem;" 
                                                           onclick="return confirm('댓글을 삭제하시겠습니까?');">삭제</a>
                                                    </c:if>
                                                </small>
                                            </div>
                                            <p class="mb-1">${comment.content}</p>
                                        </div>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <c:if test="${not empty sessionScope.loginUser}">
                            <form method="post" action="${pageContext.request.contextPath}/comment/write.do" class="mb-5">
                                <input type="hidden" name="board_idx" value="${board.idx}">
                                <input type="hidden" name="pageNum" value="${pageNum}"> 
                                
                                <div class="form-group">
                                    <label for="commentContent">댓글 작성</label>
                                    <textarea class="form-control" id="commentContent" name="content" rows="3" required placeholder="${sessionScope.loginUser.name}님, 댓글을 남겨주세요."></textarea>
                                </div>
                                <div class="text-right">
                                    <button type="submit" class="btn btn-success">댓글 등록</button>
                                </div>
                            </form>
                        </c:if>
                        <c:if test="${empty sessionScope.loginUser}">
                            <div class="alert alert-info text-center">
                                댓글을 작성하려면 <a href="${pageContext.request.contextPath}/login.jsp">로그인</a>이 필요합니다.
                            </div>
                        </c:if>
                        
                    </div>
                    </div>
            </div>
        </div>
    </div>
    
<%-- 💡 4. <script> 태그 및 </body>, </html> 닫는 태그 제거하고 공통 Footer 포함 --%>
<%@ include file="../common/footer.jsp" %>