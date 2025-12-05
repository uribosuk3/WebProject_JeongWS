package model.dto; // model.dto 패키지에 생성

import java.util.Date;

public class FreeBoardDTO {

    // DB 테이블 컬럼과 1:1 대응되는 필드
    private int idx;
    private int user_idx;
    private String title;
    private String content;
    private Date postdate;
    private int views;
    private int likes;
    
    // 기본 생성자
    public FreeBoardDTO() {}

    // Getter와 Setter (Alt + Shift + S 혹은 Source 메뉴에서 자동 생성)
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
}