<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- header.jsp에서 pageTitle을 사용하므로, Servlet에서 req.setAttribute("pageTitle", "자료실 게시글 등록");을 해야 합니다. --%>

<c:if test="${empty sessionScope.loginUser}">
    <script>
        alert('로그인 후 이용 가능합니다.');
        location.href='${pageContext.request.contextPath}/login.jsp';
    </script>
    <c:return/>
</c:if>

    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <div class="mb60 section-title">
                        <h1>자료 등록</h1>
                    </div>
                    
                    <form method="post" action="${pageContext.request.contextPath}/fileboard/write.do" enctype="multipart/form-data">
                        
                        <div class="form-group">
                            <label for="title">제목</label>
                            <input type="text" class="form-control" id="title" name="title" required placeholder="제목을 입력하세요">
                        </div>
                        
                        <div class="form-group">
                            <label for="content">내용</label>
                            <textarea class="form-control" id="content" name="content" rows="10" required placeholder="내용을 입력하세요."></textarea>
                        </div>
                        
                        <div class="form-group">
                            <label for="upload_file">첨부 파일</label>
                            <input type="file" class="form-control" id="upload_file" name="upload_file">
                        </div>
                        
                        <div class="text-right">
                            <button type="submit" class="btn btn-primary">등록</button>
                            <a href="${pageContext.request.contextPath}/fileboard/list.do" class="btn btn-default">목록</a>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    </div>