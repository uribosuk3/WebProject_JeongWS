package model.dto;

import java.sql.Date; // SQL Date 타입을 사용합니다.

public class FreeBoardDTO {
    
    // DB 테이블 컬럼과 1:1 매칭되는 필드
    private int idx;          // 게시글 고유 번호 (PK, 시퀀스)
    private int user_idx;     // 작성자 고유 번호 (FK)
    private String title;     // 제목
    private String content;   // 내용
    private Date postdate;    // 작성일
    private int views;        // 조회수
    private int likes;        // 추천수

    // JOIN/확장 기능을 위한 필드
    private String writerName; // 💡 작성자 이름 (Users 테이블에서 조회)

    // 기본 생성자
    public FreeBoardDTO() {}

    // 모든 필드를 포함하는 생성자 (필요 시 사용)
    public FreeBoardDTO(int idx, int user_idx, String title, String content, Date postdate, int views, int likes, String writerName) {
        this.idx = idx;
        this.user_idx = user_idx;
        this.title = title;
        this.content = content;
        this.postdate = postdate;
        this.views = views;
        this.likes = likes;
        this.writerName = writerName;
    }

    // Getter와 Setter
    
    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getUser_idx() {
        return user_idx;
    }

    public void setUser_idx(int user_idx) {
        this.user_idx = user_idx;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPostdate() {
        return postdate;
    }

    public void setPostdate(Date postdate) {
        this.postdate = postdate;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    // 💡 작성자 이름 Getter/Setter
    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }
}