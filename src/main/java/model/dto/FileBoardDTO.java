package model.dto;

import java.sql.Date;

public class FileBoardDTO {

    private int idx;
    private int user_idx;
    private String title;
    private String content;
    private Date postdate;
    private int views;
    private String writerName;
    private int downcount;
    
	// 파일 관련 필드 (DB 컬럼과 매핑)
    private String original_filename; 
    private String stored_filename;   
    private long filesize;            
    
    // ⭐️⭐️ 새로 추가된 필드: 파일 확장자 (타입별 출력 프로젝트 목표를 위해 필수) ⭐️⭐️
    private String file_type;

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

	public String getWriterName() {
		return writerName;
	}

	public void setWriterName(String writerName) {
		this.writerName = writerName;
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

	public String getFile_type() {
		return file_type;
	}

	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}   
	
	public int getDowncount() {
		return downcount;
	}

	public void setDowncount(int downcount) {
		this.downcount = downcount;
	}
}