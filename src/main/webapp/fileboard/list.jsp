<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- 💡 JSTL을 사용하여 로그인 상태를 JavaScript에서 사용할 변수로 준비합니다. --%>
<c:set var="isLoggedIn" value="${not empty sessionScope.loginUser}" />

<%-- 1. 공통 헤더 포함 --%>
<%@ include file="../common/header.jsp" %> 

    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <div class="mb60 section-title">
                        <c:choose>
                            <c:when test="${not empty searchWord}">
                                <h1>자료실 게시판 (${totalCount}개) - 검색 결과: "${searchWord}"</h1>
                            </c:when>
                            <c:otherwise>
                                <h1>자료실 게시판 (${totalCount}개)</h1>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th style="width: 10%;">번호</th>
                                <th style="width: 45%;">제목</th>
                                <th style="width: 15%;">작성자</th>
                                <th style="width: 15%;">작성일</th>
                                <th style="width: 10%;">조회</th>
                                <th style="width: 5%;">다운</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty fileboardList}"> 
                                    <tr>
                                        <td colspan="6" class="text-center">등록된 게시글이 없거나 검색 결과가 없습니다.</td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="board" items="${fileboardList}">
                                        <tr>
                                            <td>${board.idx}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/fileboard/view.do?idx=${board.idx}&pageNum=${pageNum}">
                                                    ${board.title}
                                                    <%-- ⭐️ 3. DTO 필드 이름 통일: original_filename 사용 ⭐️ --%>
                                                    <c:if test="${not empty board.original_filename}">
                                                        <span class="glyphicon glyphicon-floppy-save text-primary ml-1" title="파일 첨부됨"></span>
                                                    </c:if>
                                                </a>
                                            </td>
                                            <td>${board.writerName}</td>
                                            <td><fmt:formatDate value="${board.postdate}" pattern="yyyy.MM.dd"/></td>
                                            <td>${board.views}</td>
                                            <td>
                                                <%-- 다운로드 아이콘 표시 및 링크 (original_filename이 있을 경우에만) --%>
                                                <c:if test="${not empty board.original_filename}">
                                                    <a href="${pageContext.request.contextPath}/fileboard/download.do?idx=${board.idx}">
                                                        <span class="glyphicon glyphicon-download-alt text-success" title="다운로드"></span>
                                                    </a>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>

                    <div class="row">
                        <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                            <%-- 🚨 기존 c:if 제거, 버튼은 항상 표시 --%>
                            <a href="#" id="fileboardWriteBtn" class="btn btn-primary">
                                <span class="glyphicon glyphicon-cloud-upload"></span> 자료 올리기
                            </a>
                        </div>
                        <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 text-right">
                            <form method="get" action="${pageContext.request.contextPath}/fileboard/list.do" class="form-inline d-inline-block">
                                <select name="searchField" class="form-control" style="width: auto;">
                                    <option value="title" ${searchField == 'title' ? 'selected' : ''}>제목</option>
                                    <option value="content" ${searchField == 'content' ? 'selected' : ''}>내용</option>
                                    <option value="all" ${searchField == 'all' ? 'selected' : ''}>제목+내용</option>
                                </select>
                                <input type="text" name="searchWord" class="form-control" placeholder="검색어 입력" value="${searchWord}" style="width: 200px;">
                                <button type="submit" class="btn btn-default">검색</button>
                            </form>
                        </div>
                    </div>
                    
                    <%-- 페이징을 위한 검색 파라미터 준비 (경로는 이미 fileboard로 설정됨) --%>
                    <c:set var="searchParam" value=""/>
                    <c:if test="${not empty searchWord}">
                        <c:set var="searchParam" value="&searchField=${searchField}&searchWord=${searchWord}"/>
                    </c:if>

                    <div class="text-center mt-5">
                        <ul class="pagination">
                            <c:if test="${startPage > blockPage}">
                                <li><a href="${pageContext.request.contextPath}/fileboard/list.do?pageNum=${startPage - 1}${searchParam}">이전</a></li>
                            </c:if>
                            
                            <c:forEach var="i" begin="${startPage}" end="${endPage}">
                                <li class="${pageNum == i ? 'active' : ''}">
                                    <a href="${pageContext.request.contextPath}/fileboard/list.do?pageNum=${i}${searchParam}">${i}</a>
                                </li>
                            </c:forEach>
                            
                            <c:if test="${endPage < totalPage}">
                                <li><a href="${pageContext.request.contextPath}/fileboard/list.do?pageNum=${endPage + 1}${searchParam}">다음</a></li>
                            </c:if>
                        </ul>
                    </div>

                </div>
            </div>
        </div>
    </div>
    
<script>
    document.getElementById('fileboardWriteBtn').addEventListener('click', function(e) {
        
        // JSTL 변수 (isLoggedIn)를 JavaScript에서 사용합니다.
        var isLoggedIn = ${isLoggedIn}; 
        
        if (isLoggedIn) {
            // 로그인 상태: 실제 작성 페이지로 이동
            location.href = '${pageContext.request.contextPath}/fileboard/write.do'; 
        } else {
            // 로그아웃 상태: 알림창 띄우고 로그인 페이지로 이동
            e.preventDefault(); // 기본 링크 이동 방지
            alert('글을 작성하려면 로그인해야 합니다.');
            location.href = '${pageContext.request.contextPath}/member/login.jsp'; // 💡 로그인 페이지 경로
        }
    });
</script>

<%-- 2. 공통 푸터 포함 --%>
<%@ include file="../common/footer.jsp" %>