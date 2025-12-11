<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="../common/header.jsp" %> 

<c:if test="${empty board}">
    <script>
        alert('존재하지 않는 게시글입니다.');
        location.href='${pageContext.request.contextPath}/freeboard/list.do';
    </script>
</c:if>

    <style>
        /* ... 스타일 생략 ... */
        .board-view-header { border-bottom: 2px solid #ccc; padding-bottom: 10px; margin-bottom: 20px; }
        .board-view-header h2 { margin-top: 0; }
        .board-info { color: #888; font-size: 0.9em; }
        .board-info span { margin-right: 15px; }
        .board-content { padding: 20px 0; border-bottom: 1px solid #eee; min-height: 200px; white-space: pre-wrap; }
        .comment-section h3 { margin-bottom: 20px; border-bottom: 1px solid #ddd; padding-bottom: 5px; }
        .list-group-item { display: block; }
        .d-flex { display: flex; align-items: center; }
        .ml-2 { margin-left: 0.5rem; }
        
        /* Q&A 게시판의 테두리 스타일 */
    .board-detail-area {
        border: 1px solid #e0e0e0 !important; /* 실선 테두리 강제 적용 */
        padding: 20px !important;
        border-radius: 5px !important;
        margin-bottom: 30px !important;
        background-color: #ffffff !important;
    }
    /* 게시글 정보 (작성자, 날짜, 조회수) 구분선 명확화 */
    	.board-info {
            color: #333 !important; /* 폰트 색상 강제 */
            font-size: 0.9em !important;
            margin-bottom: 20px !important;
            border-bottom: 1px dashed #ddd !important; /* 점선 하단 테두리 강제 적용 */
            padding-bottom: 10px !important;
            /* Q&A 스타일이 .board-info를 덮어쓰도록 강제 */
        }
	  	.board-info strong {
	       color: #000 !important;
        }
    	.board-content { 
            padding: 0 0 !important; /* 상하 패딩 완전히 제거하여 내용이 우측 상단에 붙도록 강제 */
            border-bottom: none !important; /* 하단 테두리 제거 강제 */
            min-height: 200px;
            white-space: pre-wrap;
        }
    </style>
    
    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    
                    <div class="mb60 section-title">
                        <h1>게시글 상세 보기</h1>
                    </div>
                    
                    <div class="freeboard-view">
                        <div class="freeboard-view-header">
                            <h2>${board.title}</h2> 
                            <div class="board-info">
                                <span>작성자: **${board.writerName}**</span>
                                <span>작성일: <fmt:formatDate value="${board.postdate}" pattern="yyyy.MM.dd HH:mm"/></span>
                                <span>조회수: ${board.views}</span>
                                <span>추천: <span id="board-likes">${board.likes}</span></span>
                            </div>
                        </div>

                        <div class="board-content">
                            <p style="white-space: pre-wrap;">${board.content}</p> 
                        </div>
                    </div>
                    
                    <div class="text-center my-4">
                        <c:choose>
                            <c:when test="${not empty sessionScope.loginUser}">
                                <button id="like-button" 
                                        data-board-idx="${board.idx}" 
                                        onclick="toggleRecommend(this)"
                                        class="btn btn-lg ${isLiked ? 'btn-danger' : 'btn-info'}">
                                    <span class="glyphicon glyphicon-thumbs-up"></span> 
                                    ${isLiked ? '추천 취소' : '추천하기'} 
                                    (<span id="recommend-count">${board.likes}</span>)
                                </button>
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
                            <a href="${pageContext.request.contextPath}/freeboard/edit.do?idx=${board.idx}&pageNum=${pageNum}" class="btn btn-warning">수정</a>
                            <a href="${pageContext.request.contextPath}/freeboard/delete.do?idx=${board.idx}&pageNum=${pageNum}" class="btn btn-danger" onclick="return confirm('정말로 삭제하시겠습니까?');">삭제</a>
                        </c:if>
                        
                        <a href="${pageContext.request.contextPath}/freeboard/list.do?pageNum=${empty pageNum ? '1' : pageNum}" class="btn btn-default">목록으로</a>
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
                                                        <a href="${pageContext.request.contextPath}/freeboard/commentDelete.do?comment_idx=${comment.idx}&freeboard_idx=${board.idx}&pageNum=${pageNum}" 
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
                            <form method="post" action="${pageContext.request.contextPath}/freeboard/commentWrite.do" class="mb-5">
                                <input type="hidden" name="freeboard_idx" value="${board.idx}">
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
                                댓글을 작성하려면 <a href="${pageContext.request.contextPath}/member/login.do">로그인</a>이 필요합니다.
                            </div>
                        </c:if>
                        
                    </div>
                    </div>
            </div>
        </div>
    </div>

<script>
    function toggleRecommend(buttonElement) {
        // 로그인 체크 (JS에서도 한 번 더 체크)
        if (${empty sessionScope.loginUser}) {
            alert('로그인이 필요합니다.');
            location.href = '${pageContext.request.contextPath}/member/login.do';
            return;
        }

        const boardIdx = buttonElement.getAttribute('data-board-idx');

        fetch('${pageContext.request.contextPath}/freeboard/recommend.do', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'idx=' + boardIdx
        })
        .then(response => {
            // HTTP 상태 코드가 200번대가 아니면 에러 처리 (403, 500 등)
            if (!response.ok) {
                // 서버에서 보낸 오류 JSON 메시지를 읽어 에러를 throw
                return response.json().then(errorData => {
                    throw new Error(errorData.message || '서버 응답 오류 발생');
                });
            }
            // 서버에서 직접 만든 JSON 문자열을 파싱
            return response.json();
        })
        .then(data => {
            // JSON 데이터의 'success' 필드 확인
            if (data.success) {
                const recommendCountElement = document.getElementById('recommend-count'); // 버튼 안의 숫자
                const boardLikesElement = document.getElementById('board-likes');       // 헤더의 숫자
                
                // 1. 추천수 업데이트
                if (recommendCountElement) {
                    recommendCountElement.textContent = data.newCount;
                }
                if (boardLikesElement) {
                    boardLikesElement.textContent = data.newCount;
                }

                // 2. 버튼 상태 및 텍스트 업데이트
                if (data.isLiked) {
                    buttonElement.classList.remove('btn-info');
                    buttonElement.classList.add('btn-danger');
                    buttonElement.innerHTML = '<span class="glyphicon glyphicon-thumbs-up"></span> 추천 취소 (' + data.newCount + ')';
                    alert('게시글을 추천했습니다!');
                } else {
                    buttonElement.classList.remove('btn-danger');
                    buttonElement.classList.add('btn-info');
                    buttonElement.innerHTML = '<span class="glyphicon glyphicon-thumbs-up"></span> 추천하기 (' + data.newCount + ')';
                    alert('추천을 취소했습니다.');
                }

            } else {
                // DAO에서 처리 실패 (success: false)
                throw new Error(data.message || '알 수 없는 DB 오류');
            }
        })
        .catch(error => {
            console.error('추천 Fetch 오류:', error);
            // 최종 실패 메시지 출력
            alert('추천 등록 실패: ' + error.message);
        });
    }
</script>
    
<%@ include file="../common/footer.jsp" %>