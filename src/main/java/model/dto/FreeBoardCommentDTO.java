package model.dto;

import java.sql.Date;

public class FreeBoardCommentDTO {

    private int idx;         // 댓글 고유 번호 (PK, 시퀀스)
    private int board_idx;   // 댓글이 달린 게시글 번호 (FK)
    private int user_idx;    // 댓글 작성자 고유 번호 (FK)
    private String content;  // 댓글 내용
    private Date postdate;   // 댓글 작성일
    
    // 확장 필드 (작성자 이름 표시용)
    private String writerName; 

    // Getter와 Setter (필수)
    
    public int getIdx() { return idx; }
    public void setIdx(int idx) { this.idx = idx; }

    public int getBoard_idx() { return board_idx; }
    public void setBoard_idx(int board_idx) { this.board_idx = board_idx; }

    public int getUser_idx() { return user_idx; }
    public void setUser_idx(int user_idx) { this.user_idx = user_idx; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getPostdate() { return postdate; }
    public void setPostdate(Date postdate) { this.postdate = postdate; }

    public String getWriterName() { return writerName; }
    public void setWriterName(String writerName) { this.writerName = writerName; }
}