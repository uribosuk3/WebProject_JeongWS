<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${empty sessionScope.loginUser}">
    <script>
        alert('로그인 후 이용 가능합니다.');
        location.href='${pageContext.request.contextPath}/login.jsp';
    </script>
    <c:return/>
</c:if>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Q&A 질문 등록</title>
    <link href="../css/bootstrap.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body>
    
    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <div class="mb60 section-title">
                        <h1>Q&A 질문 등록</h1>
                    </div>
                    
                    <form method="post" action="${pageContext.request.contextPath}/qna/write.do">
                        
                        <div class="form-group">
                            <label for="title">제목</label>
                            <input type="text" class="form-control" id="title" name="title" required placeholder="질문 제목을 입력하세요">
                        </div>
                        
                        <div class="form-group">
                            <label for="content">내용</label>
                            <textarea class="form-control" id="content" name="content" rows="10" required placeholder="질문 내용을 입력하세요."></textarea>
                        </div>
                        
                        <div class="text-right">
                            <button type="submit" class="btn btn-primary">질문 등록</button>
                            <a href="${pageContext.request.contextPath}/qna/list.do" class="btn btn-default">목록으로</a>
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