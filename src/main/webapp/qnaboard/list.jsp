<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- 💡 공통 헤더 포함 --%>
<%@ include file="../common/header.jsp" %> 

<style>
    /* 답변 글의 들여쓰기를 위한 CSS (선택적) */
    .table td { vertical-align: middle; }
    .label-warning { background-color: #f0ad4e; }
    .label-success { background-color: #5cb85c; }
    .label { padding: .2em .6em .3em; font-size: 75%; font-weight: 700; line-height: 1; color: #fff; text-align: center; white-space: nowrap; vertical-align: baseline; border-radius: .25em; }
</style>

<div class="space-medium">
    <div class="container">
        <div class="row">
            <div class="col-lg-12">
                
                <div class="mb60 section-title">
                    <h1>Q&A 게시판 (${totalCount}개)</h1>
                </div>
                
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th width="8%">번호</th>
                            <th width="10%">상태</th>
                            <th width="*">제목</th>
                            <th width="15%">작성자</th>
                            <th width="12%">작성일</th>
                            <th width="8%">조회</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty qnaboardList}">
                                <tr>
                                    <td colspan="6" class="text-center">등록된 게시글이 없거나 검색 결과가 없습니다.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="board" items="${qnaboardList}">
                                    <tr>
                                        <td>${board.idx}</td>
                                        
                                        <%-- ✅ [수정]: 상태 표시 로직 - 원글(depth==0)에만 상태 표시 --%>
                                        <td>
                                            <c:choose>
                                                <c:when test="${board.depth == 0}">
                                                    <%-- 원글의 reply_state (0:대기, 1:완료) 값에 따라 표시 --%>
                                                    <span class="label 
                                                        ${board.reply_state == 0 ? 'label-warning' : 'label-success'}">
                                                        ${board.reply_state == 0 ? '답변대기' : '답변완료'}
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <%-- 답글(depth > 0)일 경우 상태 칸을 비워둠 --%>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        
                                        <td>
                                            <%-- 💡 [수정]: 계층 구조 표시 및 "ㄴ" 기호 적용 --%>
                                            <c:if test="${board.depth > 0}">
                                                <%-- depth 값에 비례하여 들여쓰기 공간 생성 (depth * 20px) --%>
                                                <span style="display: inline-block; width: ${board.depth * 20}px;"></span> 
                                                
                                                <%-- "ㄴ" 기호와 띄어쓰기 추가 --%>
                                                <span style="margin-right: 5px;">ㄴ</span> 
                                            </c:if>
                                            
                                            <%-- 게시글 제목 링크 (경로 통일) --%>
                                            <a href="${pageContext.request.contextPath}/qnaboard/view.do?idx=${board.idx}&pageNum=${pageNum}">
                                                ${board.title}
                                            </a>
                                        </td>
                                        
                                        <td>${board.writerName}</td>
                                        <td><fmt:formatDate value="${board.postdate}" pattern="yyyy.MM.dd"/></td>
                                        <td>${board.views}</td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>

                <div class="text-right">
                    <a href="${pageContext.request.contextPath}/qnaboard/write.do" class="btn btn-primary">질문 작성</a>
                </div>

            </div>
        </div>
    </div>
</div>

<%-- 💡 공통 푸터 포함 --%>
<%@ include file="../common/footer.jsp" %>