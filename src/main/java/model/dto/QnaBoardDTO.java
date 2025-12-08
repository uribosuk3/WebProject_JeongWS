package model.dto;

import java.sql.Date;

// Q&A 게시판은 답글 구조를 위해 gnum, onum, depth 필드가 추가됩니다.
public class QnaBoardDTO {

    private int idx;
    private int user_idx;
    private String title;
    private String content;
    private Date postdate;
    private int views;
    
    // 💡 Q&A 계층 구조 필드
    private int gnum;
    private int onum;
    private int depth;

    // 추가 필드 (DAO에서 조회하여 사용)
    private String writerName; 
    
    // Getter와 Setter
    public int getIdx() { return idx; }
    public void setIdx(int idx) { this.idx = idx; }

    public int getUser_idx() { return user_idx; }
    public void setUser_idx(int user_idx) { this.user_idx = user_idx; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getPostdate() { return postdate; }
    public void setPostdate(Date postdate) { this.postdate = postdate; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public int getGnum() { return gnum; }
    public void setGnum(int gnum) { this.gnum = gnum; }

    public int getOnum() { return onum; }
    public void setOnum(int onum) { this.onum = onum; }

    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }

    public String getWriterName() { return writerName; }
    public void setWriterName(String writerName) { this.writerName = writerName; }
}