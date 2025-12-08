<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>자유 게시판</title>
    <link href="../css/bootstrap.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>

<body>
    
    <div class="header navbar-fixed-top" style="background-color: #aaaaaa;">
        </div>
    
    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <div class="mb60 section-title">
                        <c:choose>
                            <c:when test="${not empty searchWord}">
                                <h1>자유 게시판 (${totalCount}개) - 검색 결과: "${searchWord}"</h1>
                            </c:when>
                            <c:otherwise>
                                <h1>자유 게시판 (${totalCount}개)</h1>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th style="width: 10%;">번호</th>
                                <th style="width: 50%;">제목</th>
                                <th style="width: 15%;">작성자</th>
                                <th style="width: 15%;">작성일</th>
                                <th style="width: 10%;">조회</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty boardList}">
                                    <tr>
                                        <td colspan="5" class="text-center">등록된 게시글이 없거나 검색 결과가 없습니다.</td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="board" items="${boardList}">
                                        <tr>
                                            <td>${board.idx}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/board/view.do?idx=${board.idx}&pageNum=${pageNum}">
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

                    <div class="row">
                        <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12">
                            <c:if test="${not empty sessionScope.loginUser}">
                                <a href="${pageContext.request.contextPath}/board/write.jsp" class="btn btn-primary">글쓰기</a>
                            </c:if>
                        </div>
                        <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 text-right">
                            <form method="get" action="${pageContext.request.contextPath}/board/list.do" class="form-inline d-inline-block">
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
                    
                    <c:set var="searchParam" value=""/>
                    <c:if test="${not empty searchWord}">
                        <c:set var="searchParam" value="&searchField=${searchField}&searchWord=${searchWord}"/>
                    </c:if>

                    <div class="text-center mt-5">
                        <ul class="pagination">
                            <c:if test="${startPage > blockPage}">
                                <li><a href="${pageContext.request.contextPath}/board/list.do?pageNum=${startPage - 1}${searchParam}">이전</a></li>
                            </c:if>
                            
                            <c:forEach var="i" begin="${startPage}" end="${endPage}">
                                <li class="${pageNum == i ? 'active' : ''}">
                                    <a href="${pageContext.request.contextPath}/board/list.do?pageNum=${i}${searchParam}">${i}</a>
                                </li>
                            </c:forEach>
                            
                            <c:if test="${endPage < totalPage}">
                                <li><a href="${pageContext.request.contextPath}/board/list.do?pageNum=${endPage + 1}${searchParam}">다음</a></li>
                            </c:if>
                        </ul>
                    </div>

                </div>
            </div>
        </div>
    </div>
    
    <div class="footer">
        </div>
    <div class="tiny-footer">
        </div>
    
    <script src="../js/jquery.min.js"></script>
    <script src="../js/bootstrap.min.js"></script>
</body>

</html>