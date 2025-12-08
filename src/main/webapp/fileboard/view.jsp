<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- 💡 공통 Header 포함 --%>
<%@ include file="../common/header.jsp" %>
<%-- header.jsp에서 pageTitle을 사용하므로, Servlet에서 req.setAttribute("pageTitle", "자료실 게시글 상세");를 해야 합니다. --%>

<c:set var="board" value="${requestScope.board}" />

<%-- 💡 게시글이 없으면 목록으로 리다이렉트 처리 추가 --%>
<c:if test="${empty board}">
    <script>
        alert('존재하지 않는 게시글입니다.');
        location.href='${pageContext.request.contextPath}/fileboard/list.do';
    </script>
    <c:return/>
</c:if>

<style>
    /* 개별 스타일만 남김 */
    .board-detail-area {
        border: 1px solid #e0e0e0;
        padding: 20px;
        border-radius: 5px;
        margin-bottom: 30px;
        background-color: #ffffff;
    }
    .board-info {
        color: #888; 
        font-size: 0.9em;
        margin-bottom: 20px;
        border-bottom: 1px dashed #ddd;
        padding-bottom: 10px;
    }
</style>
    
    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <div class="mb60 section-title">
                        <h1>자료 상세 보기</h1>
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
                            작성자: **${board.writerName}** | 
                            작성일: <fmt:formatDate value="${board.postdate}" pattern="yyyy.MM.dd HH:mm"/> | 
                            조회수: ${board.views}
                        </div>
                        
                        <div class="board-content">
                            <p style="white-space: pre-wrap;">${board.content}</p>
                        </div>
                        
                        <c:if test="${not empty board.original_filename}">
                            <div class="alert alert-success" style="margin-top: 20px;">
                                <span class="glyphicon glyphicon-download-alt"></span> 
                                첨부 파일: <strong>${board.original_filename}</strong> 
                                (<fmt:formatNumber value="${board.filesize / 1024.0 / 1024.0}" pattern="0.00"/> MB)
                                <a href="${pageContext.request.contextPath}/fileboard/download.do?idx=${board.idx}" class="btn btn-success btn-xs pull-right">다운로드</a>
                            </div>
                        </c:if>
                        
                    </div>
                </div>
            </div>

            <div class="row mb30">
                <div class="col-lg-12 text-right">
                    
                    <%-- 💡 수정/삭제 버튼 노출 시 로그인 여부 및 작성자 일치 여부를 모두 체크해야 합니다. --%>
                    <c:if test="${not empty sessionScope.loginUser && sessionScope.loginUser.idx == board.user_idx}">
                        <a href="${pageContext.request.contextPath}/fileboard/modify.do?idx=${board.idx}" class="btn btn-warning">수정</a>
                        <a href="${pageContext.request.contextPath}/fileboard/delete.do?idx=${board.idx}" class="btn btn-danger" onclick="return confirm('정말로 삭제하시겠습니까?');">삭제</a>
                    </c:if>
                    
                    <a href="${pageContext.request.contextPath}/fileboard/list.do" class="btn btn-primary">목록으로</a>
                </div>
            </div>
            
        </div>
    </div>
    
<%-- 💡 공통 Footer 포함 --%>
<%@ include file="../common/footer.jsp" %>