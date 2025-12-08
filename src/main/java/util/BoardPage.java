package util;

/**
 * 게시판 페이징 처리를 위한 유틸리티 클래스
 */
public class BoardPage {

    /**
     * 페이징 링크 HTML 코드를 생성하는 정적 메서드
     * @param totalCount 전체 게시물 수
     * @param pageSize 한 페이지당 게시물 수
     * @param blockPage 한 블록당 페이지 번호 수
     * @param currentPage 현재 페이지 번호
     * @param listUrl 목록 페이지 URL (예: /fileboard/list.do)
     * @param searchField 검색 필드 (예: title)
     * @param searchWord 검색어
     * @return 페이징 HTML 문자열
     */
    public static String pagingStr(int totalCount, int pageSize, int blockPage,
                                   int currentPage, String listUrl, 
                                   String searchField, String searchWord) {
        
        String pagingStr = "";
        
        // 1. 전체 페이지 수 계산
        int totalPage = (int) (Math.ceil(((double) totalCount / pageSize)));
        
        // 2. 현재 페이지 블록의 시작 페이지 번호 계산
        // 예: blockPage=5, currentPage가 6이면 startPage는 6
        int startPage = (((currentPage - 1) / blockPage) * blockPage) + 1;
        
        // 3. 현재 페이지 블록의 마지막 페이지 번호 계산
        int endPage = startPage + blockPage - 1;

        // 4. 마지막 페이지 번호가 전체 페이지 수보다 크면 조정
        if (endPage > totalPage) {
            endPage = totalPage;
        }

        // 검색 파라미터 쿼리 문자열 준비
        String searchParam = "";
        if (searchWord != null && !searchWord.trim().isEmpty()) {
            searchParam = "&searchField=" + searchField + "&searchWord=" + searchWord;
        }
        
        // --- 5. [첫 페이지], [이전 블록] 링크 추가 ---
        if (startPage > 1) {
            // [첫 페이지]
            pagingStr += "<li><a href=\"" + listUrl + "?pageNum=1" + searchParam + "\">&laquo;</a></li>"; 
            
            // [이전 블록] (startPage - 1)
            pagingStr += "<li><a href=\"" + listUrl + "?pageNum=" + (startPage - 1) + searchParam + "\">...</a></li>";
        }
        
        // --- 6. 페이지 번호 링크 추가 ---
        for (int i = startPage; i <= endPage; i++) {
            if (i == currentPage) {
                // 현재 페이지: <a> 태그 대신 <span> 태그로 표시 (클릭 불가)
                pagingStr += "<li class='active'><span>" + i + "</span></li>";
            } else {
                // 다른 페이지
                pagingStr += "<li><a href=\"" + listUrl + "?pageNum=" + i + searchParam + "\">" + i + "</a></li>";
            }
        }
        
        // --- 7. [다음 블록], [마지막 페이지] 링크 추가 ---
        if (endPage < totalPage) {
            // [다음 블록] (endPage + 1)
            pagingStr += "<li><a href=\"" + listUrl + "?pageNum=" + (endPage + 1) + searchParam + "\">...</a></li>";

            // [마지막 페이지]
            pagingStr += "<li><a href=\"" + listUrl + "?pageNum=" + totalPage + searchParam + "\">&raquo;</a></li>";
        }

        // Bootstrap 스타일의 <ul> 태그로 감싸지 않았으므로, <li> 태그만 반환
        return pagingStr;
    }
}