package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.dto.FileBoardDTO; 
import util.DBConn; 

public class FileBoardDAO {

    private static FileBoardDAO instance = new FileBoardDAO();
    public static FileBoardDAO getInstance() {
        return instance;
    }
    private FileBoardDAO() {}

    /**
     * [1. 게시글 등록] 제목, 내용, 작성자 정보와 함께 파일 메타데이터를 저장
     */
    public boolean insertFileBoard(FileBoardDTO dto) {
        // ... (기존 insertFileBoard 구현 내용은 유지)
        String sql = "INSERT INTO file_board (idx, user_idx, title, content, postdate, views, original_filename, stored_filename, filesize) "
                   + "VALUES (seq_file_board_idx.NEXTVAL, ?, ?, ?, SYSDATE, 0, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, dto.getUser_idx());
            ps.setString(2, dto.getTitle());
            ps.setString(3, dto.getContent());
            ps.setString(4, dto.getOriginal_filename()); 
            ps.setString(5, dto.getStored_filename());   
            ps.setLong(6, dto.getFilesize());            
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("자료실 게시글 등록 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    /**
     * [2. 전체 게시물 수 조회] (검색 조건 포함)
     */
    public int selectCount(String searchField, String searchWord) { 
        int count = 0;
        // 검색 조건이 있을 경우 WHERE 절을 동적으로 추가
        String sql = "SELECT COUNT(*) FROM file_board";
        
        if (searchWord != null && !searchWord.trim().isEmpty()) {
            sql += " WHERE " + searchField + " LIKE ?";
        }
        
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (searchWord != null && !searchWord.trim().isEmpty()) {
                ps.setString(1, "%" + searchWord + "%");
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1); 
                }
            }
        } catch (SQLException e) {
            System.err.println("자료실 전체 개수 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }
    
    /**
     * [3. 페이징 목록 조회] (검색 조건 반영 및 작성자 이름 JOIN)
     */
    public List<FileBoardDTO> selectList(String searchField, String searchWord, int start, int end) { 
        List<FileBoardDTO> boardList = new ArrayList<>();
        
        // Oracle ROWNUM을 이용한 페이징 SQL
        String sql = "SELECT * FROM ("
                   + "    SELECT ROWNUM rNum, T.* FROM ("
                   + "        SELECT fb.idx, fb.user_idx, fb.title, fb.content, fb.postdate, fb.views, "
                   + "               fb.original_filename, fb.stored_filename, fb.filesize, u.name AS writerName " // writerName 추가
                   + "        FROM file_board fb JOIN users u ON fb.user_idx = u.idx ";
                   
        // 검색 조건 추가
        if (searchWord != null && !searchWord.trim().isEmpty()) {
            sql += " WHERE fb." + searchField + " LIKE ?";
        }
        
        // 정렬 (최신 글이 위로 오도록)
        sql += "        ORDER BY fb.idx DESC"
             + "    ) T"
             + ") WHERE rNum BETWEEN ? AND ?"; // start와 end로 제한
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int paramIndex = 1;
        
        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            
            // 1. 검색어 바인딩
            if (searchWord != null && !searchWord.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchWord + "%");
            }
            
            // 2. 페이징 시작/종료 번호 바인딩
            ps.setInt(paramIndex++, start);
            ps.setInt(paramIndex++, end);
            
            rs = ps.executeQuery();
            
            while (rs.next()) {
                FileBoardDTO dto = new FileBoardDTO();
                dto.setIdx(rs.getInt("idx"));
                dto.setUser_idx(rs.getInt("user_idx"));
                dto.setTitle(rs.getString("title"));
                dto.setPostdate(rs.getDate("postdate"));
                dto.setViews(rs.getInt("views"));
                
                // 작성자 이름 설정 (JOIN으로 가져옴)
                dto.setWriterName(rs.getString("writerName"));
                
                // 파일 존재 여부 확인 (original_filename이 null이 아니면 파일이 첨부됨)
                dto.setOriginal_filename(rs.getString("original_filename"));
                
                boardList.add(dto);
            }
        } 
        catch (SQLException e) {
            System.err.println("자료실 페이징 목록 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return boardList;
    }
    
    /**
     * [4. 상세 게시글 조회] (파일 정보 포함)
     */
    public FileBoardDTO selectBoard(int idx) {
        // ... (기존 selectBoard 구현 내용은 유지)
        FileBoardDTO dto = null;
        String sql = "SELECT idx, user_idx, title, content, postdate, views, original_filename, stored_filename, filesize "
                   + "FROM file_board WHERE idx = ?";
        
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idx);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto = new FileBoardDTO();
                    dto.setIdx(rs.getInt("idx"));
                    dto.setUser_idx(rs.getInt("user_idx"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setPostdate(rs.getDate("postdate"));
                    dto.setViews(rs.getInt("views"));
                    
                    // 파일 정보 설정
                    dto.setOriginal_filename(rs.getString("original_filename"));
                    dto.setStored_filename(rs.getString("stored_filename"));
                    dto.setFilesize(rs.getLong("filesize"));
                }
            }
        } 
        catch (SQLException e) {
            System.err.println("자료실 상세 게시글 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return dto;
    }
    
    /**
     * [5. 게시글 수정] 제목, 내용 및 파일 정보를 업데이트합니다.
     * (첨부 파일이 변경되지 않았다면 파일 필드는 기존 값을 그대로 받습니다.)
     */
    public boolean updateBoard(FileBoardDTO dto) {
        String sql = "UPDATE file_board SET title = ?, content = ?, original_filename = ?, stored_filename = ?, filesize = ? "
                   + "WHERE idx = ? AND user_idx = ?"; // 작성자 일치 조건 추가 (보안 강화)

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, dto.getTitle());
            ps.setString(2, dto.getContent());
            ps.setString(3, dto.getOriginal_filename()); 
            ps.setString(4, dto.getStored_filename());   
            ps.setLong(5, dto.getFilesize());            
            ps.setInt(6, dto.getIdx());
            ps.setInt(7, dto.getUser_idx()); // 작성자 인덱스

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("자료실 게시글 수정 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
    
    /**
     * [6. 게시글 삭제] 특정 게시글을 삭제합니다.
     */
    public boolean deleteBoard(int idx) {
        String sql = "DELETE FROM file_board WHERE idx = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, idx);
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("자료실 게시글 삭제 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
    
    /**
     * [7. 조회수 증가] 특정 게시글의 views 카운트를 1 증가시킵니다.
     * (FileBoardViewServlet에서 사용)
     */
    public boolean updateViews(int idx) {
        String sql = "UPDATE file_board SET views = views + 1 WHERE idx = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setInt(1, idx);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("자료실 게시글 조회수 증가 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
}