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
     * 💡 users 테이블을 JOIN하여 작성자 이름(writerName)을 가져옵니다.
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
                whereClause += "WHERE B.title LIKE '%' || ? || '%' OR B.content LIKE '%' || ? || '%' "; 
            } else {
                whereClause += "WHERE B." + searchField + " LIKE '%' || ? || '%' "; 
            }
        }
        
        // 💡 SQL 수정: users 테이블과 JOIN하여 작성자 이름(U.name)을 가져오도록 수정
        String sql = "SELECT * FROM ("
                    + "    SELECT ROWNUM AS RNUM, T.* FROM ("
                    + "        SELECT B.idx, B.user_idx, B.title, B.content, B.postdate, B.views, B.recommend_count AS likes, U.name AS writerName " // 작성자 이름 추가
                    + "        FROM free_board B JOIN users U ON B.user_idx = U.idx " // JOIN 구문 추가
                    + whereClause 
                    + "        ORDER BY B.idx DESC"
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
                    // dto.setContent(rs.getString("content")); // 목록에서는 content는 생략
                    dto.setPostdate(rs.getDate("postdate"));
                    dto.setViews(rs.getInt("views"));
                    dto.setLikes(rs.getInt("likes"));
                    
                    // 💡 작성자 이름 매핑
                    dto.setWriterName(rs.getString("writerName"));
                    
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
     * [2. 전체 게시물 수 조회] 검색 조건을 반영하여 전체 게시글 수를 조회합니다. (수정 불필요)
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
     * [3. 게시글 등록] 새로운 게시글을 DB에 삽입합니다. (시퀀스 이름 통일 완료)
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
     * [4. 조회수 증가] 특정 게시글의 조회수를 1 증가시킵니다. (수정 불필요)
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
     * [5. 상세 게시글 조회] 특정 게시글의 모든 정보를 조회합니다.
     * 💡 users 테이블을 JOIN하여 작성자 이름(writerName)을 가져옵니다.
     * @param idx 게시글 고유 번호
     */
    public FreeBoardDTO selectBoard(int idx) {
        FreeBoardDTO dto = null;
        // 💡 SQL 수정: users 테이블과 JOIN하여 U.name(writerName)을 가져오도록 수정
        String sql = "SELECT B.idx, B.user_idx, B.title, B.content, B.postdate, B.views, B.recommend_count AS likes, U.name AS writerName "
                    + "FROM free_board B LEFT JOIN users U ON B.user_idx = U.idx "
                    + "WHERE B.idx = ?";
        
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
                    
                    // 💡 작성자 이름 매핑
                    dto.setWriterName(rs.getString("writerName"));
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
     * [6. 게시글 수정] 제목과 내용으로 게시글을 업데이트합니다. (수정 불필요)
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
     * [7. 게시글 삭제] 특정 게시글을 DB에서 제거합니다.
     * 💡 ORA-02292 오류 방지를 위해, 해당 게시글에 달린 댓글(free_comment)을 먼저 삭제합니다.
     */
    public boolean deleteBoard(int idx) {
        Connection conn = null;
        PreparedStatement psComment = null;
        PreparedStatement psBoard = null;
        boolean result = false;

        // 1. 댓글 삭제 쿼리: 해당 게시글 IDX(board_idx)를 가진 모든 댓글 삭제
        String sqlDeleteComment = "DELETE FROM free_comment WHERE board_idx = ?";
        // 2. 게시글 삭제 쿼리
        String sqlDeleteBoard = "DELETE FROM free_board WHERE idx = ?"; 

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1단계: 댓글 삭제 (자식 레코드)
            psComment = conn.prepareStatement(sqlDeleteComment);
            psComment.setInt(1, idx);
            psComment.executeUpdate();
            
            // ❌ 이 위치의 psComment.close()를 제거했습니다. ❌
            // if (psComment != null) psComment.close(); 
            // ------------------------------------------

            // 2단계: 게시글 삭제 (부모 레코드)
            psBoard = conn.prepareStatement(sqlDeleteBoard);
            psBoard.setInt(1, idx);
            
            if (psBoard.executeUpdate() == 1) {
                conn.commit(); // 게시글 삭제까지 성공 시 커밋
                result = true;
            } else {
                conn.rollback(); // 실패 시 롤백
            }

        } catch (SQLException e) {
            System.err.println("자유 게시글 삭제 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            // DB 오류 발생 시에도 롤백
            try { if (conn != null) conn.rollback(); } catch(SQLException rollbackE) { rollbackE.printStackTrace(); }
        } finally {
            // 자원 해제 및 AutoCommit 복원
            try { if (psComment != null) psComment.close(); } catch (SQLException e) {} // 💡 psComment 자원 해제를 finally로 이동
            try { if (psBoard != null) psBoard.close(); } catch (SQLException e) {}
            try { 
                if (conn != null) {
                    conn.setAutoCommit(true); // AutoCommit 상태 복원
                    conn.close(); 
                }
            } catch (SQLException e) {}
        }
        
        return result;
    }
}