package model.dto;

import java.sql.Date;

public class FileBoardDTO {

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
	public String getOriginal_filename() {
		return original_filename;
	}
	public void setOriginal_filename(String original_filename) {
		this.original_filename = original_filename;
	}
	public String getStored_filename() {
		return stored_filename;
	}
	public void setStored_filename(String stored_filename) {
		this.stored_filename = stored_filename;
	}
	public long getFilesize() {
		return filesize;
	}
	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
	public void setWriterName(String writerName) {
		this.writerName = writerName;
	}

	private int idx;
    private int user_idx;
    private String title;
    private String content;
    private Date postdate;
    private int views;
    private String writerName;
    
    public String getWriterName() {
		return writerName;
	}
	
	// 파일 관련 필드 (FileBoardWriteServlet에서 사용)
    private String original_filename; 
    private String stored_filename;   
    private long filesize;            

    // Getter와 Setter (최소한 서블릿에서 사용하는 것들은 포함되어야 합니다)
    
    // 1. 기본 게시글 속성
//    public int getIdx() { return idx; }
//    public void setIdx(int idx) { this.idx = idx; }
//
//    public int getUser_idx() { return user_idx; }
//    public void setUser_idx(int user_idx) { this.user_idx = user_idx; }
//
//    public String getTitle() { return title; }
//    public void setTitle(String title) { this.title = title; } // 💡 이 메서드가 없어서 에러 발생
//
//    public String getContent() { return content; }
//    public void setContent(String content) { this.content = content; }
//
//    public Date getPostdate() { return postdate; }
//    public void setPostdate(Date postdate) { this.postdate = postdate; }
//
//    public int getViews() { return views; }
//    public void setViews(int views) { this.views = views; }
//    
//    // 2. 파일 속성
//    public String getOriginal_filename() { return original_filename; }
//    public void setOriginal_filename(String original_filename) { this.original_filename = original_filename; }
//
//    public String getStored_filename() { return stored_filename; }
//    public void setStored_filename(String stored_filename) { this.stored_filename = stored_filename; }
//
//    public long getFilesize() { return filesize; }
//    public void setFilesize(long filesize) { this.filesize = filesize; }
//    
//    public void setWriterName(String writerName) {
//		this.writerName = writerName;
//	}
    
    
}