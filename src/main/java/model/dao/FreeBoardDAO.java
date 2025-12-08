package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.dto.FreeBoardDTO;
import util.DBConn; 

public class FreeBoardDAO {

    private static FreeBoardDAO instance = new FreeBoardDAO();
    public static FreeBoardDAO getInstance() {
        return instance;
    }
    private FreeBoardDAO() {}

    /**
     * [1. 페이징 목록 조회] 검색 조건을 반영하여 지정된 범위의 게시글을 조회합니다.
     * @param searchField 검색 필드 (title, content, all)
     * @param searchWord 검색 키워드
     * @param start 시작 행 번호
     * @param end 끝 행 번호
     */
    public List<FreeBoardDTO> selectList(String searchField, String searchWord, int start, int end) { 
        List<FreeBoardDTO> boardList = new ArrayList<>();
        
        // WHERE 절을 포함할 수 있도록 동적 쿼리 구성
        String whereClause = "";
        if (searchWord != null && !searchWord.trim().isEmpty()) {
            // "all" 검색 필드는 title과 content를 모두 검색
            if ("all".equals(searchField)) {
                whereClause += "WHERE title LIKE '%' || ? || '%' OR content LIKE '%' || ? || '%' ";
            } else {
                whereClause += "WHERE " + searchField + " LIKE '%' || ? || '%' ";
            }
        }
        
        String sql = "SELECT * FROM ("
                   + "    SELECT ROWNUM AS RNUM, T.* FROM ("
                   + "        SELECT idx, user_idx, title, content, postdate, views, likes "
                   + "        FROM free_board "
                   +         whereClause 
                   + "        ORDER BY idx DESC"
                   + "    ) T"
                   + ") WHERE RNUM BETWEEN ? AND ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            int parameterIndex = 1;

            // 검색어가 있을 경우 매개변수 설정
            if (searchWord != null && !searchWord.trim().isEmpty()) {
                if ("all".equals(searchField)) {
                    ps.setString(parameterIndex++, searchWord);
                    ps.setString(parameterIndex++, searchWord);
                } else {
                    ps.setString(parameterIndex++, searchWord);
                }
            }

            // 페이징 관련 매개변수 설정
            ps.setInt(parameterIndex++, start);
            ps.setInt(parameterIndex, end);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FreeBoardDTO dto = new FreeBoardDTO();
                    
                    dto.setIdx(rs.getInt("idx"));
                    dto.setUser_idx(rs.getInt("user_idx"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setPostdate(rs.getDate("postdate"));
                    dto.setViews(rs.getInt("views"));
                    dto.setLikes(rs.getInt("likes"));
                    
                    boardList.add(dto);
                }
            }
        } 
        catch (SQLException e) {
            System.err.println("자유게시판 검색 목록 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        return boardList;
    }

    /**
     * [2. 전체 게시물 수 조회] 검색 조건을 반영하여 전체 게시글 수를 조회합니다.
     * @param searchField 검색 필드 (title, content, all)
     * @param searchWord 검색 키워드
     */
    public int selectCount(String searchField, String searchWord) { 
        int count = 0;
        String sql = "SELECT COUNT(*) FROM free_board ";
        
        // 검색어가 있을 경우 WHERE 절 추가
        if (searchWord != null && !searchWord.trim().isEmpty()) {
             if ("all".equals(searchField)) {
                sql += "WHERE title LIKE '%' || ? || '%' OR content LIKE '%' || ? || '%' ";
            } else {
                sql += "WHERE " + searchField + " LIKE '%' || ? || '%' ";
            }
        }

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // 검색어가 있을 경우에만 PreparedStatement에 값 설정
            if (searchWord != null && !searchWord.trim().isEmpty()) {
                if ("all".equals(searchField)) {
                    ps.setString(1, searchWord);
                    ps.setString(2, searchWord); 
                } else {
                    ps.setString(1, searchWord);
                }
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1); 
                }
            }
        } catch (SQLException e) {
            System.err.println("자유게시판 전체 개수 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }
    
    /**
     * [3. 게시글 등록] 새로운 게시글을 DB에 삽입합니다. (기존 유지)
     */
    public boolean insertBoard(FreeBoardDTO dto) {
        String sql = "INSERT INTO free_board (idx, user_idx, title, content, postdate, views, likes) "
                   + "VALUES (seq_free_board_idx.NEXTVAL, ?, ?, ?, SYSDATE, 0, 0)";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dto.getUser_idx());
            ps.setString(2, dto.getTitle());
            ps.setString(3, dto.getContent());
            
            return ps.executeUpdate() == 1;

        } 
        catch (SQLException e) {
            System.err.println("게시글 등록 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * [4. 조회수 증가] 특정 게시글의 조회수를 1 증가시킵니다. (기존 유지)
     */
    public boolean updateViews(int idx) {
        String sql = "UPDATE free_board SET views = views + 1 WHERE idx = ?";
        
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idx);
            
            return ps.executeUpdate() == 1; 

        } catch (SQLException e) {
            System.err.println("게시글 조회수 증가 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * [5. 상세 게시글 조회] 특정 게시글의 모든 정보를 조회합니다. (기존 유지)
     * @param idx 게시글 고유 번호
     */
    public FreeBoardDTO selectBoard(int idx) {
        FreeBoardDTO dto = null;
        String sql = "SELECT idx, user_idx, title, content, postdate, views, likes "
                   + "FROM free_board WHERE idx = ?";
        
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idx);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto = new FreeBoardDTO();
                    dto.setIdx(rs.getInt("idx"));
                    dto.setUser_idx(rs.getInt("user_idx"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setPostdate(rs.getDate("postdate"));
                    dto.setViews(rs.getInt("views"));
                    dto.setLikes(rs.getInt("likes"));
                }
            }
        } 
        catch (SQLException e) {
            System.err.println("상세 게시글 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return dto;
    }
    
    /**
     * [6. 게시글 수정] 제목과 내용으로 게시글을 업데이트합니다. (기존 유지)
     * @param dto 수정할 게시글 정보 (idx, title, content)
     */
    public boolean updateBoard(FreeBoardDTO dto) {
        String sql = "UPDATE free_board SET title = ?, content = ?, postdate = SYSDATE WHERE idx = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dto.getTitle());
            ps.setString(2, dto.getContent());
            ps.setInt(3, dto.getIdx());
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("게시글 수정 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * [7. 게시글 삭제] 특정 게시글을 DB에서 제거합니다. (기존 유지)
     * @param idx 삭제할 게시글 고유 번호
     */
    public boolean deleteBoard(int idx) {
        String sql = "DELETE FROM free_board WHERE idx = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idx);
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("게시글 삭제 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}