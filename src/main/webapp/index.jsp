<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="Are you interior design company and looking for startup your website. Download Free Interior Design Website Templates for you suitable to you.">
    <meta name="keywords" content="interior design, furniture, exterior furniture, furniture company, bootstrap interior design website templates, interior design & furniture website templates">
    <title>Interior Design Website Templates Free Download</title>
    <!-- Bootstrap -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css?family=Raleway:300,400,600,600i,700" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="css/font-awesome.min.css" rel="stylesheet">
    <!-- Style -->
    <link href="css/style.css" rel="stylesheet">
</head>

<body>
    <div class="header navbar-fixed-top" style="background-color: #aaaaaa;">
        <div class="container">
            <div class="row">
                <div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">
                    <a href="index.jsp"><img src="images/logo.png" alt="Interior Design Website Templates Free Download"></a>
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
    <div class="hero-section">
        <div class="container">
            <div class="row">
                <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 text-center">
                    <h1 class="hero-title"><strong>Interior-exterior</strong><br>
          Free Webstite template</h1>
                    <a href="#" class="btn btn-white">REad more <i class="fa fa-plus"></i></a> </div>
            </div>
        </div>
    </div>
    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-offset-2 col-lg-8 col-md-offset-2 col-md-8
               col-sm-12 col-xs-12">
                    <div class="mb60 text-center section-title">
                        <!-- section title start-->
                        <h1>Our Services</h1>
                    </div>
                    <!-- /.section title start-->
                </div>
            </div>
            <div class="row">
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                    <div class="service-block text-center">
                        <!-- service block -->
                        <div class="service-img ">
                            <!-- service img -->
                            <a href="service-detail.jsp" class="imghover"><img src="images/service-pic-1.jpg" class="img-responsive" alt="Interior Design Website Templates Free Download"> </a>
                        </div>
                        <!-- service img -->
                        <div class="service-content">
                            <!-- service content -->
                            <h2><a href="service-detail.jsp" class="title">Interior</a></h2>
                            <p>Phasellus hendrerit mauris vitae odio suscip max pimus donec consequat cursus viverra varius natoque penatibus magnis dis parturient.</p>
                        </div>
                        <!-- service content -->
                    </div>
                    <!-- /.service block -->
                </div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                    <div class="service-block text-center">
                        <!-- service block -->
                        <div class="service-img">
                            <!-- service img -->
                            <a href="service-detail.jsp" class="imghover"><img src="images/service-pic-2.jpg" class="img-responsive" alt="Interior Design Website Templates Free Download"> </a>
                        </div>
                        <!-- service img -->
                        <div class="service-content">
                            <!-- service content -->
                            <h2><a href="service-detail.jsp" class="title">Exterior</a></h2>
                            <p>Donec tempus odio ac dignissim ultricies massa varius natoque penatibus arturient montes, nascetur ridiculus mus aliquam.</p>
                        </div>
                        <!-- service content -->
                    </div>
                    <!-- /.service block -->
                </div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                    <div class="service-block text-center">
                        <!-- service block -->
                        <div class="service-img">
                            <!-- service img -->
                            <a href="service-detail.jsp" class="imghover"><img src="images/service-pic-3.jpg" class="img-responsive" alt="Interior Design Website Templates Free Download"> </a>
                        </div>
                        <!-- service img -->
                        <div class="service-content">
                            <!-- service content -->
                            <h2><a href="service-detail.jsp" class="title">Landscape</a></h2>
                            <p>Vestibulum diam arcu varius natoque penatibus magnis dis parturient ridiculus mus mollisid iaculis necaliquamd antetiam consequat.</p>
                        </div>
                        <!-- service content -->
                    </div>
                    <!-- /.service block -->
                </div>
            </div>
        </div>
    </div>
    <div class="space-medium bg-light">
        <div class="container">
            <div class="row">
                <div class="col-lg-offset-2 col-lg-8 col-md-offset-2 col-md-8
               col-sm-12 col-xs-12">
                    <div class="mb60 text-center section-title">
                        <!-- section title start-->
                        <h1>our recent projects</h1>
                    </div>
                    <!-- /.section title start-->
                </div>
            </div>
            <div class="row">
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                    <div class="project-img mb30">
                        <a href="service-detail.jsp" class="imghover"><img src="images/project-pic-1.jpg" class="img-responsive" alt="Interior Design Website Templates Free Download"></a>
                    </div>
                </div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                    <div class="project-img mb30">
                        <a href="service-detail.jsp" class="imghover"><img src="images/project-pic-2.jpg" class="img-responsive" alt="Interior Design Website Templates Free Download"></a>
                    </div>
                </div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                    <div class="project-img mb30">
                        <a href="service-detail.jsp" class="imghover"><img src="images/project-pic-3.jpg" class="img-responsive" alt="Interior Design Website Templates Free Download"></a>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                    <div class="project-img mb30">
                        <a href="service-detail.jsp" class="imghover"><img src="images/project-pic-4.jpg" class="img-responsive" alt="Interior Design Website Templates Free Download"></a>
                    </div>
                </div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                    <div class="project-img mb30">
                        <a href="service-detail.jsp" class="imghover"><img src="images/project-pic-5.jpg" class="img-responsive" alt="Interior Design Website Templates Free Download"></a>
                    </div>
                </div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
                    <div class="project-img mb30">
                        <a href="service-detail.jsp" class="imghover"><img src="images/project-pic-6.jpg" class="img-responsive" alt="Interior Design Website Templates Free Download"></a>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 text-center  "> <a href="service-detail.jsp" class="btn btn-default">view all projects</a> </div>
            </div>
        </div>
    </div>
    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-offset-2 col-lg-8 col-md-offset-2 col-md-8 col-sm-12 col-xs-12">
                    <div class="section-title mb60 text-center">
                        <!-- section title start-->
                        <h1>Why choose uS?</h1>
                    </div>
                    <!-- /.section title start-->
                </div>
            </div>
            <div class="row">
                <div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
                    <div class="outline pinside30 text-center mb30">
                        <div class="mb30"> <img src="images/creative-ideas.png" class="" alt="Interior Design Website Templates Free Download"> </div>
                        <div class="">
                            <h2>Creative Ideas </h2>
                            <p>Phasellus hendrerit mauris vitae odio suscip max pius donec aconsequat.</p>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
                    <div class="outline pinside30 text-center mb30">
                        <div class="mb30"> <img src="images/modern-technology.png" class="" alt="Interior Design Website Templates Free Download"> </div>
                        <div class="">
                            <h2>Modern technology</h2>
                            <p>Morbi convallis nisl at commodo tem orut libero utnisi lacinia limana.</p>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4 col-md-4 col-sm-6 col-xs-12">
                    <div class="outline pinside30 text-center mb30">
                        <div class="mb30"> <img src="images/design-creativity.png" class="" alt="Interior Design Website Templates Free Download"> </div>
                        <div class="">
                            <h2>Design &amp; Creativity</h2>
                            <p>Sednunc sagit phasellus mitortor con equat hendrerit maue odi.</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="cta-section">
        <div class="container">
            <div class="row">
                <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                    <div class="text-center">
                        <h1 class="cta-title">We are ready to satisfy your<br>
            project <strong>requirements!</strong></h1>
                        <a href="https://easetemplate.com/downloads/interior-exterior/free-website-template/" class="btn btn-default" target="_blank">contact us</a> </div>
                </div>
            </div>
        </div>
    </div>
    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-offset-2 col-lg-8 col-md-offset-2 col-md-8 col-sm-12 col-xs-12">
                    <div class="section-title mb40 text-center">
                        <!-- section title start-->
                        <h1>Our Client Says</h1>
                    </div>
                    <!-- /.section title start-->
                </div>
            </div>
            <div class="row">
                <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                    <div class="testimonial-block bg-light pinside30">
                        <div class="quote-left"> <i class="fa fa-quote-left"></i> </div>
                        <div class="testimonial-content">
                            <p>"They are talented &amp; creative. I have never seen a better designer on the this market. ecommended to all! If you need to build powerfully,contact them immediately ARCHONE design have worked on two stunning projects at our School, We were fortunate to have his vision breathe new life into a small, bare and underused courtyard in the centre of our brand new building. Within 4weeks and his team had transformed a poor quality space into a rich and vibrant courtyard garden, now enjoyed all day, every day by staff and pupils. Both projects have been well managed, within budget and worth every penny of our investment.We greatly look forward to working with him and his team again in the future.”</p>
                            <div class="testimonial-info"> <span class="testimonial-name">- Ruby Burns</span> <span class="testimonial-meta">Market research analyst</span> </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="footer">
        <!-- footer-->
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
                        <!-- social block -->
                        <h3 class="widget-title">Our Address</h3>
                        <p>1309 Roosevelt Wilson Lane
                            <br> Colton, CA 92324</p>
                    </div>
                    <!-- /.social block -->
                </div>
                <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
                    <div class="footer-widget">
                        <!-- newsletter block -->
                        <h3 class="widget-title">e-mail Us</h3>
                        <p>info@yourwebsitedomain.com</p>
                    </div>
                    <!-- newsletter block -->
                </div>
                <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12">
                    <div class="footer-widget">
                        <!-- newsletter block -->
                        <h3 class="widget-title">Call us</h3>
                        <p>180-874-5234</p>
                        <p>180-752-3957</p>
                    </div>
                    <!-- newsletter block -->
                </div>
            </div>
        </div>
    </div>
    <!-- /.footer-->
    <div class="tiny-footer">
        <!-- tiny footer block -->
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
    <!-- /.tiny footer block -->
    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="js/jquery.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="js/bootstrap.min.js"></script>
    <script src="js/menumaker.js"></script>
    <script src="js/navigation.js" type="text/javascript"></script>
</body>

</html>
