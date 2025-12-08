<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- 💡 공통 Header 포함 --%>
<%@ include file="../common/header.jsp" %>
<%-- header.jsp에서 pageTitle을 사용하므로, Servlet에서 req.setAttribute("pageTitle", "자료실 게시판 목록");을 해야 합니다. --%>

<div class="space-medium">
    <div class="container">
        <div class="row">
            <div class="col-lg-12">
                <div class="mb60 section-title">
                    <h1>자료실</h1>
                    <p class="text-right">총 게시물: **${requestScope.totalCount}** 건</p>
                </div>
            </div>
        </div>

        <div class="row mb30">
            <div class="col-lg-12">
                <form method="get" action="${pageContext.request.contextPath}/fileboard/list.do" class="form-inline text-center">
                    <select name="searchField" class="form-control">
                        <option value="title" <c:if test="${requestScope.searchField == 'title'}">selected</c:if>>제목</option>
                        <option value="content" <c:if test="${requestScope.searchField == 'content'}">selected</c:if>>내용</option>
                        <option value="writerName" <c:if test="${requestScope.searchField == 'writerName'}">selected</c:if>>작성자</option>
                    </select>
                    <input type="text" name="searchWord" class="form-control" value="${requestScope.searchWord}" placeholder="검색어 입력">
                    <button type="submit" class="btn btn-default">검색</button>
                </form>
            </div>
        </div>

        <div class="row">
            <div class="col-lg-12">
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th style="width:10%;">번호</th>
                            <th style="width:50%;">제목</th>
                            <th style="width:15%;">작성자</th>
                            <th style="width:15%;">작성일</th>
                            <th style="width:10%;">조회수</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty requestScope.boardList}">
                            <tr>
                                <td colspan="5" class="text-center">등록된 게시물이 없습니다.</td>
                            </tr>
                        </c:if>
                        <c:forEach var="board" items="${requestScope.boardList}">
                            <tr>
                                <td>${board.idx}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/fileboard/view.do?idx=${board.idx}">
                                        ${board.title}
                                        <c:if test="${not empty board.original_filename}">
                                            <span class="glyphicon glyphicon-paperclip" title="첨부 파일 있음"></span>
                                        </c:if>
                                    </a>
                                </td>
                                <td>${board.writerName}</td>
                                <td><fmt:formatDate value="${board.postdate}" pattern="yyyy.MM.dd"/></td>
                                <td>${board.views}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        
        <div class="row">
            <div class="col-lg-6">
                <c:if test="${not empty sessionScope.loginUser}">
                    <%-- 💡 fileboard/write.jsp 대신 fileboard/write.do 서블릿 호출 권장 --%>
                    <a href="${pageContext.request.contextPath}/fileboard/write.do" class="btn btn-primary">글쓰기</a>
                </c:if>
            </div>
            <div class="col-lg-6 text-right">
                <nav>
                    <ul class="pagination pagination-sm">
                        ${requestScope.pagingStr} 
                    </ul>
                </nav>
            </div>
        </div>

    </div>
</div>

<%-- 💡 공통 Footer 포함 --%>
<%@ include file="../common/footer.jsp" %>