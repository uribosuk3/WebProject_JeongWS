package model.dto; // 실제 패키지 경로로 변경해주세요

import java.sql.Date; // 💡 java.sql.Date 타입 사용 (JDBC에서 rs.getDate()와 호환)

public class QnaBoardCommentDTO {

    // qna_board_comment 테이블 컬럼
    private int idx;            // 댓글 고유 번호 (PK)
    private int board_idx;      // 게시글 번호 (FK)
    private int user_idx;       // 작성자 회원 번호 (FK)
    private String content;     // 댓글 내용
    private Date postdate;      // 작성일 (java.sql.Date)

    // 추가 필드 (댓글 목록 출력 시 필요)
    private String writerName;  // 작성자 이름 (DAO에서 JOIN으로 가져옴)

    // 1. 기본 생성자
    public QnaBoardCommentDTO() {}
    
    // 2. Getter와 Setter 메소드
    
    public int getIdx() { return idx; }
    public void setIdx(int idx) { this.idx = idx; }
    
    public int getBoard_idx() { return board_idx; }
    public void setBoard_idx(int board_idx) { this.board_idx = board_idx; }

    public int getUser_idx() { return user_idx; }
    public void setUser_idx(int user_idx) { this.user_idx = user_idx; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    // 💡 날짜 타입 확인: java.sql.Date
    public Date getPostdate() { return postdate; }
    public void setPostdate(Date postdate) { this.postdate = postdate; }

    public String getWriterName() { return writerName; }
    public void setWriterName(String writerName) { this.writerName = writerName; }
}