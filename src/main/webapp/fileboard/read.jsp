<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- DTO 객체가 request 영역에 "dto"라는 이름으로 저장되어 있다고 가정합니다. --%>

<div class="file-attachment">
    <h3>첨부 파일</h3>
    
    <c:set var="fileType" value="${dto.file_type}" />
    <c:set var="storedName" value="${dto.stored_filename}" />
    <c:set var="originalName" value="${dto.original_filename}" />
    
    <%-- 파일이 첨부되었는지 확인 (DB에 파일 이름이 저장되어 있는지) --%>
    <c:choose>
        <c:when test="${empty storedName || storedName eq ''}">
            <p>첨부된 파일이 없습니다.</p>
        </c:when>
        <c:otherwise>
            <div class="file-preview">
                
                <c:choose>
                    
                    <%-- 1. 이미지 (png, gif, jpg, jpeg) 출력 --%>
                    <c:when test="${fileType eq 'jpg' || fileType eq 'jpeg' || fileType eq 'png' || fileType eq 'gif'}">
                        <h4>[이미지 미리보기]</h4>
                        <img src="${pageContext.request.contextPath}/uploadFiles/${storedName}" alt="${originalName}" style="max-width: 100%; height: auto;">
                    </c:when>
                    
                    <%-- 2. 동영상 (mp4, webm, avi) 출력 --%>
                    <c:when test="${fileType eq 'mp4' || fileType eq 'webm' || fileType eq 'avi'}">
                        <h4>[동영상 재생]</h4>
                        <video controls style="max-width: 100%; height: auto;">
                            <source src="${pageContext.request.contextPath}/uploadFiles/${storedName}" type="video/${fileType}">
                            죄송합니다. 귀하의 브라우저는 비디오 태그를 지원하지 않습니다.
                        </video>
                    </c:when>
                    
                    <%-- 3. 음원 (mp3, wav, ogg) 출력 --%>
                    <c:when test="${fileType eq 'mp3' || fileType eq 'wav' || fileType eq 'ogg'}">
                        <h4>[음원 재생]</h4>
                        <audio controls>
                            <source src="${pageContext.request.contextPath}/uploadFiles/${storedName}" type="audio/${fileType}">
                            죄송합니다. 귀하의 브라우저는 오디오 태그를 지원하지 않습니다.
                        </audio>
                    </c:when>
                    
                    <%-- 4. 그 외 파일 (기타 문서, 압축 파일 등) --%>
                    <c:otherwise>
                        <h4>[미리보기 불가]</h4>
                        <p>이 파일 형식(${fileType})은 웹에서 직접 미리보기를 지원하지 않습니다.</p>
                    </c:otherwise>
                
                </c:choose>
                </div>
            
            <p class="download-link">
                첨부 파일:
                <a href="${pageContext.request.contextPath}/fileboard/download.do?sName=${storedName}&oName=${originalName}">
                    ${originalName}
                </a> 
                (<span class="filesize">${dto.filesize} bytes</span>)
            </p>
        </c:otherwise>
    </c:choose>

</div>