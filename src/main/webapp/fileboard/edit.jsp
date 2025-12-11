<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="fileboard" value="${requestScope.board}" />
<c:if test="${empty board}">
    <c:redirect url="${pageContext.request.contextPath}/fileboard/list.do"/>
</c:if>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>자료실 게시글 수정</title>
    <link href="../css/bootstrap.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body>
    
    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <div class="mb60 section-title">
                        <h1>자료 수정</h1>
                    </div>
                    
                    <form method="post" action="${pageContext.request.contextPath}/fileboard/edit.do" enctype="multipart/form-data">
                        
                        <input type="hidden" name="idx" value="${board.idx}">
                        <input type="hidden" name="old_stored_filename" value="${board.stored_filename}">
                        <input type="hidden" name="old_original_filename" value="${board.original_filename}">
                        <input type="hidden" name="old_filesize" value="${board.filesize}">
                        
                        <div class="form-group">
                            <label for="title">제목</label>
                            <input type="text" class="form-control" id="title" name="title" required value="${board.title}">
                        </div>
                        
                        <div class="form-group">
                            <label for="content">내용</label>
                            <textarea class="form-control" id="content" name="content" rows="10" required>${board.content}</textarea>
                        </div>
                        
                        <div class="form-group">
                            <label>현재 첨부 파일</label>
                            <c:if test="${not empty board.original_filename}">
                                <p class="form-control-static">
                                    ${board.original_filename} (<fmt:formatNumber value="${board.filesize / 1024.0 / 1024.0}" pattern="0.00"/> MB)
                                    </p>
                            </c:if>
                            <c:if test="${empty board.original_filename}">
                                <p class="form-control-static">첨부된 파일 없음</p>
                            </c:if>
                        </div>
                        
                        <div class="form-group">
                            <label for="new_upload_file">새 파일 업로드 (기존 파일 교체)</label>
                            <input type="file" class="form-control" id="new_upload_file" name="new_upload_file">
                        </div>
                        
                        <div class="text-right">
                            <button type="submit" class="btn btn-warning">수정 완료</button>
                            <a href="${pageContext.request.contextPath}/fileboard/view.do?idx=${board.idx}" class="btn btn-default">취소</a>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    </div>
    
    <script src="../js/jquery.min.js"></script>
    <script src="../js/bootstrap.min.js"></script>
</body>
</html>