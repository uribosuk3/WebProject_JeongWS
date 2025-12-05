<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="회원가입 페이지입니다.">
    <meta name="keywords" content="로그인, 회원가입">
    <title>회원가입</title> <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Raleway:300,400,600,600i,700" rel="stylesheet">
    <link href="css/font-awesome.min.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">
</head>

<body>
    <div class="header navbar-fixed-top" style="background-color: #aaaaaa;">
        <div class="container">
            <div class="row">
                <div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">
                    <a href="index.jsp"><img src="images/logo.png" alt="Logo"></a>
                </div>
                <div class="col-lg-8 col-md-8 col-sm-12 col-xs-12">
                    <div class="navigation">
<div id="navigation">
    <ul>
        <li class="active"><a href="${pageContext.request.contextPath}/index.jsp">Home</a></li> 

        <c:if test="${not isLogin}">
            <li><a href="${pageContext.request.contextPath}/login.jsp">로그인</a></li>
            <li><a href="${pageContext.request.contextPath}/register.jsp">회원가입</a></li>
        </c:if>

        <c:if test="${isLogin}">
            <li><strong>${loginUser.name}님</strong></li>
            <li><a href="${pageContext.request.contextPath}/mypage.jsp">회원정보수정</a></li>
            <li><a href="${pageContext.request.contextPath}/logout.do">로그아웃</a></li> 
        </c:if>

        <li><a href="${pageContext.request.contextPath}/board/list.do">자유게시판</a></li>
        <li><a href="${pageContext.request.contextPath}/qna/list.do">Q&A 게시판</a></li>
        <li><a href="${pageContext.request.contextPath}/file/list.do">자료실</a></li>
    </ul>
</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-offset-3 col-lg-6 col-md-offset-2 col-md-8 col-sm-12 col-xs-12">
                    <div class="pinside30 outline">
                        
                        <div class="text-center mb60 section-title">
                            <h1>회원가입</h1> </div>
                        
                        <form method="post" action="register.do"> 
                            
                            <div class="form-group">
                                <label class="control-label" for="name">이름</label>
                                <input type="text" class="form-control" name="name" id="name" placeholder="이름을 입력하세요" required>
                            </div>
                            
                            <div class="form-group">
                                <label class="control-label" for="id">아이디</label>
                                <input type="text" class="form-control" name="id" id="id" placeholder="아이디를 입력하세요" required>
                            </div>
                            
                            <div class="form-group">
                                <label class="control-label" for="password">비밀번호</label>
                                <input type="password" class="form-control" name="pw" id="password" placeholder="비밀번호를 입력하세요" required> </div>
                            
                            <div class="form-group">
                                <label class="control-label" for="confirm_password">비밀번호 확인</label>
                                <input type="password" class="form-control" name="confirm_pw" id="confirm_password" placeholder="비밀번호를 다시 입력하세요" required> </div>
                            
                            <div class="form-group">
                                <label class="control-label" for="email">이메일</label>
                                <input type="email" class="form-control" name="email" id="email" placeholder="이메일을 입력하세요" required>
                            </div>

                            <c:if test="${not empty errorMessage}">
                                <p style="color: red; margin-bottom: 15px;">${errorMessage}</p>
                            </c:if>

                            <div class="form-group text-center">
                                <button type="submit" class="btn btn-default">가입하기</button>
                                <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-link">취소</a>
                            </div>
                        </form>
                        
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="footer">
        <div class="container">
            <div class="row">
                <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
                    <div class="footer-widget">
                        <h3 class="widget-title">About us</h3>
                        <p>Phasellus hendrerit mauris vitae odio suscip pimus donec consequat cursus viverra varius natoque penatibus magnis dis parturient.</p>
                    </div>
                </div>
                <div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
                    <div class="footer-widget">
                        <h3 class="widget-title">Our Address</h3>
                        <p>1309 Roosevelt Wilson Lane
                            <br> Colton, CA 92324</p>
                    </div>
                </div>
                <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
                    <div class="footer-widget">
                        <h3 class="widget-title">e-mail Us</h3>
                        <p>info@yourwebsitedomain.com</p>
                    </div>
                </div>
                <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
                    <div class="footer-widget">
                        <h3 class="widget-title">Call us</h3>
                        <p>180-874-5234</p>
                        <p>180-752-3957</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="tiny-footer">
        <div class="container">
            <div class="row">
                <div class="col-lg-7 col-md-7 col-sm-7 col-xs-12">
                    <div class="copyright-content">
                       Shared by <i class="fa fa-love"></i><a href="https://bootstrapthemes.co">BootstrapThemes</a>
                    </div>
                </div>
                <div class="col-lg-5 col-md-5 col-sm-5 col-xs-12">
                    <div class="footer-social">
                        <ul class="listnone">
                            <li> <a href="#"><i class="fa fa-facebook-square"></i></a> </li>
                            <li> <a href="#"><i class="fa fa-twitter-square"></i></a> </li>
                            <li> <a href="#"><i class="fa fa-google-plus-square"></i></a> </li>
                            <li> <a href="#"><i class="fa fa-youtube-square"></i></a> </li>
                            <li> <a href="#"><i class="fa fa-pinterest-square"></i></a> </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="js/jquery.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/menumaker.js"></script>
    <script src="js/navigation.js" type="text/javascript"></script>
</body>

</html>