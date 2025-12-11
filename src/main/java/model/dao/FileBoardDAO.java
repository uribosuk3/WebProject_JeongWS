package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.dto.FileBoardDTO;
import util.DBConn; // DB 연결 클래스는 유지

public class FileBoardDAO {

    private static FileBoardDAO instance = new FileBoardDAO();
    public static FileBoardDAO getInstance() {
        return instance;
    }
    private FileBoardDAO() {}

    /**
     * [1. 게시글 등록] 제목, 내용, 작성자 정보와 함께 파일 메타데이터를 저장
     * SQL 쿼리를 DB 컬럼명(original_name, saved_name, file_type, filesize)에 맞게 수정
     */
    public boolean insertFileBoard(FileBoardDTO dto) {
        // ⭐️ SQL 쿼리 수정: 컬럼명 변경 및 file_type 추가
        String sql = "INSERT INTO file_board (idx, user_idx, title, content, postdate, views, original_name, saved_name, file_type, filesize) "
                   + "VALUES (seq_file_board_idx.NEXTVAL, ?, ?, ?, SYSDATE, 0, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, dto.getUser_idx());
            ps.setString(2, dto.getTitle());
            ps.setString(3, dto.getContent());
            
            // ⭐️ 바인딩 수정: DB 컬럼명에 맞게 DTO 값 사용 및 file_type 추가
            ps.setString(4, dto.getOriginal_filename()); // original_name 컬럼에 매핑
            ps.setString(5, dto.getStored_filename());   // saved_name 컬럼에 매핑
            ps.setString(6, dto.getFile_type());         // file_type 추가
            ps.setLong(7, dto.getFilesize());            // filesize
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("자료실 게시글 등록 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // ⭐️ 기존의 자원 해제 방식 유지
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    /**
     * [2. 전체 게시물 수 조회] (검색 조건 포함)
     */
    public int selectCount(String searchField, String searchWord) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM file_board";
        
        if (searchWord != null && !searchWord.trim().isEmpty()) {
            sql += " WHERE " + searchField + " LIKE ?";
        }
        
        // try-with-resources 구문은 유지 (자동 close 되므로 깔끔함)
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
     * 쿼리에서 DB 컬럼명(original_name, file_type)을 사용하도록 수정
     */
    public List<FileBoardDTO> selectList(String searchField, String searchWord, int start, int end) {
        List<FileBoardDTO> boardList = new ArrayList<>();
        
        // ⭐️⭐️ SQL 쿼리 수정 (숨겨진 공백 문자 제거) ⭐️⭐️
        String sql = "SELECT * FROM (";
        sql += " SELECT ROWNUM rNum, T.* FROM (";
        sql += " SELECT fb.idx, fb.user_idx, fb.title, fb.content, fb.postdate, fb.views, ";
        sql += " fb.original_name, fb.saved_name, fb.filesize, fb.file_type, u.name AS writerName "; 
        sql += " FROM file_board fb LEFT JOIN users u ON fb.user_idx = u.idx ";
                   
        if (searchWord != null && !searchWord.trim().isEmpty()) {
            sql += " WHERE fb." + searchField + " LIKE ?";
        }
        
        sql += " ORDER BY fb.idx DESC";
        sql += " ) T";
        sql += " ) WHERE rNum BETWEEN ? AND ?";
        // ⭐️⭐️ 수정 끝 ⭐️⭐️
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int paramIndex = 1;
        
        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            
            if (searchWord != null && !searchWord.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + searchWord + "%");
            }
            
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
                
                dto.setWriterName(rs.getString("writerName"));
                
                // ⭐️ 파일 정보 설정 (DB 컬럼명 사용)
                dto.setOriginal_filename(rs.getString("original_name"));
                dto.setStored_filename(rs.getString("saved_name"));
                dto.setFilesize(rs.getLong("filesize"));
                dto.setFile_type(rs.getString("file_type")); // ⭐️ file_type 추가
                
                boardList.add(dto);
            }
        }
        catch (SQLException e) {
            System.err.println("자료실 페이징 목록 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // ⭐️ 기존의 자원 해제 방식 유지
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return boardList;
    }
    
    /**
     * [4. 상세 게시글 조회] (파일 정보 포함)
     * 쿼리에서 DB 컬럼명(original_name, saved_name, file_type, filesize)을 사용하도록 수정
     */
    public FileBoardDTO selectBoard(int idx) {
        FileBoardDTO dto = null;
        // ⭐️ SQL 쿼리 수정: DB 컬럼명 사용 및 file_type 추가
        String sql = "SELECT fb.*, u.name AS writerName "
	               + "FROM file_board fb LEFT JOIN users u ON fb.user_idx = u.idx "
	               + "WHERE fb.idx = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setInt(1, idx);
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                dto = new FileBoardDTO();
                dto.setIdx(rs.getInt("idx"));
                dto.setUser_idx(rs.getInt("user_idx"));
                dto.setTitle(rs.getString("title"));
                dto.setContent(rs.getString("content"));
                dto.setPostdate(rs.getDate("postdate"));
                dto.setViews(rs.getInt("views"));
                dto.setWriterName(rs.getString("writerName"));
                
                // ⭐️ 파일 정보 설정 (DB 컬럼명 사용)
                dto.setOriginal_filename(rs.getString("original_name"));
                dto.setStored_filename(rs.getString("saved_name"));
                dto.setFilesize(rs.getLong("filesize"));
                dto.setFile_type(rs.getString("file_type")); // ⭐️ file_type 추가
            }
        }
        catch (SQLException e) {
            System.err.println("자료실 상세 게시글 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // ⭐️ 기존의 자원 해제 방식 유지
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return dto;
    }
    
    /**
     * [5. 게시글 수정] 제목, 내용 및 파일 정보를 업데이트합니다.
     * 쿼리에서 DB 컬럼명(original_name, saved_name, file_type, filesize)을 사용하도록 수정
     */
    public boolean updateBoard(FileBoardDTO dto) {
        // ⭐️ SQL 쿼리 수정: DB 컬럼명 사용 및 file_type 추가
        String sql = "UPDATE file_board SET title = ?, content = ?, original_name = ?, saved_name = ?, file_type = ?, filesize = ? "
                   + "WHERE idx = ? AND user_idx = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, dto.getTitle());
            ps.setString(2, dto.getContent());
            
            // ⭐️ 바인딩 수정: DB 컬럼명에 맞게 DTO 값 사용 및 file_type 추가
            ps.setString(3, dto.getOriginal_filename()); // original_name 컬럼에 매핑
            ps.setString(4, dto.getStored_filename());   // saved_name 컬럼에 매핑
            ps.setString(5, dto.getFile_type());         // file_type 추가
            ps.setLong(6, dto.getFilesize());
            
            ps.setInt(7, dto.getIdx());
            ps.setInt(8, dto.getUser_idx()); // 작성자 인덱스

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("자료실 게시글 수정 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // ⭐️ 기존의 자원 해제 방식 유지
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
            // ⭐️ 기존의 자원 해제 방식 유지
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
    
    /**
     * [7. 조회수 증가] 특정 게시글의 views 카운트를 1 증가시킵니다.
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
            // ⭐️ 기존의 자원 해제 방식 유지
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
}