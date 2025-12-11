<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %> 

<%-- 
    ⭐️⭐️ 기존 쿠키 읽는 로직은 그대로 유지합니다. ⭐️⭐️
    이 로직 덕분에 EL 변수 'savedIdValue'가 생성됩니다.
--%>
<%
    // 쿠키에서 'savedId'를 찾아서 JSP 변수에 저장
    String savedId = "";
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie c : cookies) {
            if (c.getName().equals("savedId")) {
                savedId = c.getValue();
                break;
            }
        }
    }
    // JSP EL에서 사용 가능하도록 요청 속성에 저장
    request.setAttribute("savedIdValue", savedId);
    
    // 💡 페이지 제목 설정 (header.jsp에서 사용)
    request.setAttribute("pageTitle", "로그인");
%>

<%-- 💡 1. 공통 Header 포함 --%>
<%@ include file="../common/header.jsp" %>
    
    <div class="space-medium">
        <div class="container">
            <div class="row">
                <div class="col-lg-offset-3 col-lg-6 col-md-offset-2 col-md-8 col-sm-12 col-xs-12">
                    <div class="pinside30 outline">
                        
                        <div class="text-center mb60 section-title">
                            <h1>로그인</h1>
                        </div>
                        
                        <%-- 회원가입 성공 메시지 처리 --%>
                        <c:if test="${not empty sessionScope.registerSuccessMsg}">
                            <div class="alert alert-success" role="alert" style="font-weight: bold;">
                                ${sessionScope.registerSuccessMsg}
                                <c:remove var="registerSuccessMsg" scope="session"/>
                            </div>
                        </c:if>

                        <%-- 로그인 실패 메시지 처리 --%>
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger" role="alert" style="font-weight: bold;">
                                ${errorMessage}
                            </div>
                        </c:if>
                        
                        <form action="${pageContext.request.contextPath}/member/login.do" method="post"> 
                            
                            <div class="form-group">
                                <label class="control-label" for="id">아이디</label>
                                <input type="text" class="form-control" name="id" id="id" placeholder="아이디를 입력하세요" required 
                                    value="${empty inputId ? savedIdValue : inputId}">
                            </div>
                            
                            <div class="form-group">
                                <label class="control-label" for="password">비밀번호</label>
                                <input type="password" class="form-control" name="pw" id="password" placeholder="비밀번호를 입력하세요" required>
                            </div>
                            
                            <div class="form-group clearfix">
                                <div class="pull-left">
                                    <label class="control-label">
                                        <input type="checkbox" name="save_id" ${not empty savedIdValue ? 'checked' : ''}> 아이디 저장
                                    </label>
                                </div>
                                <div class="pull-right">
                                    <a href="#">아이디/비밀번호 찾기</a> </div>
                            </div>
                            
                            <div class="form-group text-center">
                                <button type="submit" class="btn btn-default">로그인</button>
                                <%-- 💡 3. 회원가입 링크 수정: member/register.jsp 경로 --%>
                                <a href="${pageContext.request.contextPath}/member/register.jsp" class="btn btn-link">회원가입</a>
                            </div>
                        </form>
                     </div>
                </div>
            </div>
        </div>
    </div>
    
<%-- 💡 4. 공통 Footer 포함 --%>
<%@ include file="../common/footer.jsp" %>