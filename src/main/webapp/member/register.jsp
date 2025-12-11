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
    <title>회원가입</title> 
    
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Raleway:300,400,600,600i,700" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/font-awesome.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>

<body>
    <div class="header navbar-fixed-top" style="background-color: #aaaaaa;">
        <div class="container">
            <div class="row">
                <div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">
                    <a href="${pageContext.request.contextPath}/index.jsp"><img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo"></a>
                </div>
                <div class="col-lg-8 col-md-8 col-sm-12 col-xs-12">
                    <div class="navigation">
                        <div id="navigation">
                            <ul>
                                <li class="active"><a href="${pageContext.request.contextPath}/index.jsp">Home</a></li> 

                                <c:if test="${not isLogin}">
                                    <li><a href="${pageContext.request.contextPath}/member/login.jsp">로그인</a></li>
                                    <li><a href="${pageContext.request.contextPath}/member/register.jsp">회원가입</a></li>
                                </c:if>

                                <c:if test="${isLogin}">
                                    <%-- 로그인 시 상단 네비게이션 메뉴 항목 제거됨 --%>
                                </c:if>

                                <li><a href="${pageContext.request.contextPath}/freeboard/list.do">자유게시판</a></li>
                                <li><a href="${pageContext.request.contextPath}/qnaboard/list.do">Q&A 게시판</a></li>
                                <li><a href="${pageContext.request.contextPath}/fileboard/list.do">자료실</a></li>
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
                        
                        <div class="text-center mb60 section-title" style="margin-top: 50px;">
                            <h1>회원가입</h1> </div>
                        
                        <%-- 🚨 수정: onsubmit 속성을 validateForm()으로 변경 --%>
                        <form method="post" action="register.do" onsubmit="return validateForm()"> 
                            
                            <c:if test="${not empty errorMessage}">
                                <div class="alert alert-danger" role="alert" style="font-weight: bold;">
                                    ${errorMessage}
                                </div>
                            </c:if>

                            <div class="form-group">
                                <label class="control-label" for="name">이름</label>
                                <input type="text" class="form-control" name="name" id="name" placeholder="이름을 입력하세요" required value="${inputName}">
                            </div>
                            
                            <div class="form-group">
                                <label class="control-label" for="id">아이디</label>
                                <input type="text" class="form-control" name="id" id="id" placeholder="아이디를 입력하세요" required value="${inputId}">
                                </div>
                            
                            <div class="form-group">
                                <label class="control-label" for="password">비밀번호</label>
                                <input type="password" class="form-control" name="pw" id="password" placeholder="비밀번호를 입력하세요" required> 
                            </div>
                            
                            <div class="form-group">
                                <label class="control-label" for="confirm_password">비밀번호 확인</label>
                                <input type="password" class="form-control" name="confirm_pw" id="confirm_password" placeholder="비밀번호를 다시 입력하세요" required> 
                            </div>
                            
                            <div class="form-group">
                                <label class="control-label" for="email">이메일</label>
                                <input type="email" class="form-control" name="email" id="email" placeholder="이메일을 입력하세요" required value="${inputEmail}">
                            </div>

                            <div class="form-group">
							  <label class="control-label" for="phone1">전화번호</label>
							  <div style="display: flex; align-items: center;">
							      
							      <input type="text" class="form-control" id="phone1" value="010" readonly style="width: 60px; text-align: center; background-color: #f8f9fa;">
							      
							      <span style="margin: 0 5px;">-</span>
							      
							      <input type="text" class="form-control phone-input" id="phone2" name="phone2" placeholder="XXXX" maxlength="4" required style="width: 80px; text-align: center;">
							      
							      <span style="margin: 0 5px;">-</span>
							      
							      <input type="text" class="form-control phone-input" id="phone3" name="phone3" placeholder="XXXX" maxlength="4" required style="width: 80px; text-align: center;">
							      
							  </div>
							  <%-- 🚨 주의: 서버에서 phone2와 phone3를 합쳐서 전화번호를 처리해야 합니다. --%>
							</div>

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
    
    <script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/menumaker.js"></script>
    <script src="${pageContext.request.contextPath}/js/navigation.js" type="text/javascript"></script>

    <%-- 🚨 추가: 비밀번호 일치 확인 및 이메일/전화번호 형식 검사 JavaScript 함수 --%>
    <script type="text/javascript">
        
        function validateEmail(email) {
            // 이메일 형식 (user@domain.com) 검사. @와 .이 필수입니다.
            var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
            return emailPattern.test(email);
        }

     // 🚨 수정된 validatePhone 함수: 3개의 분리된 필드 값을 검사
        function validatePhone(p2, p3) {
            // phone1은 010으로 고정되어 있으므로, p2와 p3의 길이가 4자리인지 확인
            
            // p2와 p3가 모두 4자리 숫자인지 검사하는 정규 표현식
            var pattern = /^\d{4}$/;
            
            // p2와 p3 모두 필수이므로, 둘 다 4자리 숫자인지 확인
            if (pattern.test(p2) && pattern.test(p3)) {
                return true;
            }
            return false;
        }

        function validateForm() {
            var password = document.getElementById("password").value;
            var confirmPassword = document.getElementById("confirm_password").value;
            var email = document.getElementById("email").value;
            
            // 🚨 수정: 분리된 전화번호 필드 값 가져오기
            var phone2 = document.getElementById("phone2").value;
            var phone3 = document.getElementById("phone3").value;
            
            // 1. 비밀번호 일치 검사
            if (password != confirmPassword) {
                alert("비밀번호가 같지 않습니다.");
                document.getElementById("password").value = "";
                document.getElementById("confirm_password").value = "";
                document.getElementById("password").focus();
                return false; 
            }
            
            // 2. 이메일 형식 검사
            if (!validateEmail(email)) {
                alert("이메일 형식이 올바르지 않습니다. (@와 .이 포함되어야 합니다.)");
                document.getElementById("email").focus();
                return false;
            }

            // 3. 전화번호 형식 검사 (phone2, phone3의 입력 길이 및 숫자 여부 검사)
            if (!validatePhone(phone2, phone3)) {
                alert("전화번호 형식이 올바르지 않습니다. (중간 및 끝 4자리는 숫자로 입력해야 합니다.)");
                
                // 불일치 시 필드를 지우고 포커스 이동 (선택 사항)
                document.getElementById("phone2").value = "";
                document.getElementById("phone3").value = "";
                document.getElementById("phone2").focus();
                
                return false;
            }
            
            // 모든 검사를 통과하면 폼 제출을 진행합니다.
            return true; 
        }
    </script>
</body>

</html>