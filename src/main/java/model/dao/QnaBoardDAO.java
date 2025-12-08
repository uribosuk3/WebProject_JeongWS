package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.dto.QnaBoardDTO; 
import util.DBConn; 

public class QnaBoardDAO {

    private static QnaBoardDAO instance = new QnaBoardDAO();
    public static QnaBoardDAO getInstance() {
        return instance;
    }
    private QnaBoardDAO() {}

    /**
     * [1. 전체 게시물 수 조회] (검색 조건 포함)
     */
    public int selectCount(String searchField, String searchWord) { 
        int count = 0;
        String sql = "SELECT COUNT(*) FROM qna_board ";
        
        if (searchWord != null && !searchWord.trim().isEmpty()) {
             if ("all".equals(searchField)) {
                sql += "WHERE title LIKE '%' || ? || '%' OR content LIKE '%' || ? || '%' ";
            } else {
                sql += "WHERE " + searchField + " LIKE '%' || ? || '%' ";
            }
        }

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
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
            System.err.println("Q&A 전체 개수 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }
    
    /**
     * [2. 원본 질문 등록] 새로운 질문글을 DB에 삽입합니다.
     */
    public boolean insertQuestion(QnaBoardDTO dto) {
        // idx와 gnum에 같은 시퀀스 값을 넣기 위해 CURRVAL을 사용
        String sql = "INSERT INTO qna_board (idx, user_idx, title, content, postdate, views, gnum, onum, depth) "
                   + "VALUES (seq_qna_board_idx.NEXTVAL, ?, ?, ?, SYSDATE, 0, seq_qna_board_idx.CURRVAL, 0, 0)";

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, dto.getUser_idx());
            ps.setString(2, dto.getTitle());
            ps.setString(3, dto.getContent());
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Q&A 질문 등록 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    /**
     * [3-1. 답글 순서 조정] 답글이 달릴 위치 이후의 모든 글의 onum을 1 증가시킵니다. (트랜잭션 필요)
     */
    private void updateOnum(Connection conn, int gnum, int onum) throws SQLException {
        String sql = "UPDATE qna_board SET onum = onum + 1 WHERE gnum = ? AND onum > ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gnum);
            ps.setInt(2, onum);
            ps.executeUpdate();
        }
    }

    /**
     * [3-2. 답글 등록] 답글을 DB에 삽입합니다. (트랜잭션 필수)
     */
    public boolean insertReply(QnaBoardDTO parentDto, QnaBoardDTO replyDto) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean result = false;

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1. 순서 번호(onum) 조정
            updateOnum(conn, parentDto.getGnum(), parentDto.getOnum());

            // 2. 답글 삽입
            String sql = "INSERT INTO qna_board (idx, user_idx, title, content, postdate, views, gnum, onum, depth) "
                       + "VALUES (seq_qna_board_idx.NEXTVAL, ?, ?, ?, SYSDATE, 0, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, replyDto.getUser_idx());
            ps.setString(2, replyDto.getTitle());
            ps.setString(3, replyDto.getContent());
            
            ps.setInt(4, parentDto.getGnum());          // 그룹 번호는 부모와 동일
            ps.setInt(5, parentDto.getOnum() + 1);      // 순서 번호는 부모의 onum + 1
            ps.setInt(6, parentDto.getDepth() + 1);     // 깊이는 부모의 depth + 1

            if (ps.executeUpdate() == 1) {
                conn.commit();
                result = true;
            } else {
                conn.rollback();
            }

        } catch (SQLException e) {
            System.err.println("Q&A 답글 등록 중 DB 오류 발생: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException rollbackE) { rollbackE.printStackTrace(); }
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
        return result;
    }


    /**
     * [4. 페이징 목록 조회] (검색 조건 및 계층 구조 순서 반영)
     */
    public List<QnaBoardDTO> selectList(String searchField, String searchWord, int start, int end) { 
        List<QnaBoardDTO> boardList = new ArrayList<>();
        
        String whereClause = "";
        if (searchWord != null && !searchWord.trim().isEmpty()) {
            if ("all".equals(searchField)) {
                whereClause += "WHERE title LIKE '%' || ? || '%' OR content LIKE '%' || ? || '%' ";
            } else {
                whereClause += "WHERE " + searchField + " LIKE '%' || ? || '%' ";
            }
        }
        
        String sql = "SELECT * FROM ("
                   + "    SELECT ROWNUM AS RNUM, T.* FROM ("
                   + "        SELECT idx, user_idx, title, content, postdate, views, gnum, onum, depth "
                   + "        FROM qna_board "
                   +         whereClause 
                   + "        ORDER BY gnum DESC, onum ASC" // 그룹은 최신순, 그룹 내에서는 순서번호 오름차순
                   + "    ) T"
                   + ") WHERE RNUM BETWEEN ? AND ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            int parameterIndex = 1;

            if (searchWord != null && !searchWord.trim().isEmpty()) {
                if ("all".equals(searchField)) {
                    ps.setString(parameterIndex++, searchWord);
                    ps.setString(parameterIndex++, searchWord);
                } else {
                    ps.setString(parameterIndex++, searchWord);
                }
            }

            ps.setInt(parameterIndex++, start);
            ps.setInt(parameterIndex, end);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QnaBoardDTO dto = new QnaBoardDTO();
                    
                    dto.setIdx(rs.getInt("idx"));
                    dto.setUser_idx(rs.getInt("user_idx"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setPostdate(rs.getDate("postdate"));
                    dto.setViews(rs.getInt("views"));
                    dto.setGnum(rs.getInt("gnum"));
                    dto.setOnum(rs.getInt("onum"));
                    dto.setDepth(rs.getInt("depth"));
                    
                    boardList.add(dto);
                }
            }
        } 
        catch (SQLException e) {
            System.err.println("Q&A 목록 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        return boardList;
    }
    
    /**
     * [5. 상세 게시글 조회] 
     */
    public QnaBoardDTO selectBoard(int idx) {
        QnaBoardDTO dto = null;
        String sql = "SELECT idx, user_idx, title, content, postdate, views, gnum, onum, depth "
                   + "FROM qna_board WHERE idx = ?";
        
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idx);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto = new QnaBoardDTO();
                    dto.setIdx(rs.getInt("idx"));
                    dto.setUser_idx(rs.getInt("user_idx"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setPostdate(rs.getDate("postdate"));
                    dto.setViews(rs.getInt("views"));
                    dto.setGnum(rs.getInt("gnum"));
                    dto.setOnum(rs.getInt("onum"));
                    dto.setDepth(rs.getInt("depth"));
                }
            }
        } 
        catch (SQLException e) {
            System.err.println("Q&A 상세 게시글 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return dto;
    }
    
    /**
     * [6. 게시글 수정]
     */
    public boolean updateBoard(QnaBoardDTO dto) {
        String sql = "UPDATE qna_board SET title = ?, content = ?, postdate = SYSDATE WHERE idx = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dto.getTitle());
            ps.setString(2, dto.getContent());
            ps.setInt(3, dto.getIdx());
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Q&A 게시글 수정 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * [7. 게시글 삭제] 
     */
    public boolean deleteBoard(int idx) {
        String sql = "DELETE FROM qna_board WHERE idx = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idx);
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Q&A 게시글 삭제 중 DB 오류 발생: " + e.getMessage());
            // 에러 코드가 FK 에러(자식 댓글이 있는 경우)라면 별도 처리 가능
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * [8. 조회수 증가] 
     */
    public boolean updateViews(int idx) {
        String sql = "UPDATE qna_board SET views = views + 1 WHERE idx = ?";
        
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idx);
            
            return ps.executeUpdate() == 1; 

        } 
        catch (SQLException e) {
            System.err.println("Q&A 게시글 조회수 증가 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * [9. 답글(자식 글) 개수 조회] 해당 게시글(parent_idx)에 달린 답글의 총 개수를 조회합니다.
     * (삭제 시 하위 글 존재 여부 확인 용도)
     * @param parent_idx 원본 글의 idx (이는 답글의 gnum과 onum을 판단하는 기준이 됩니다)
     * @return 자식 글의 개수 (0이면 삭제 가능)
     */
    public int selectReplyCount(int parent_idx) {
        int count = 0;
        // 답글의 gnum은 원글의 gnum과 같지만, onum은 0보다 크고 depth는 0보다 커야 합니다.
        // 가장 확실한 방법은, 삭제하려는 게시글의 gnum을 찾아서, 그 gnum을 가진 다른 글의 개수를 세는 것입니다.
        
        // 1. 삭제하려는 글의 gnum, onum, depth를 조회
        QnaBoardDTO parent = selectBoard(parent_idx);
        if (parent == null) return 0; // 글이 없으면 자식도 당연히 없음

        // 2. 해당 그룹(gnum) 내에서, 삭제하려는 글(parent_idx)을 제외한 다른 글의 개수를 셉니다.
        // 만약 count가 0보다 크다면, 그 글은 답글이거나, 자체가 다른 글의 답글임을 의미합니다.
        
        String sql = "SELECT COUNT(*) FROM qna_board WHERE gnum = ? AND idx != ?";
        
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, parent.getGnum());
            ps.setInt(2, parent_idx);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Q&A 답글 개수 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 주의: 이 로직은 원글(depth=0)이 삭제될 때, 그 원글 그룹(gnum)에 속한
        // 다른 답글들이 존재하는지 확인하는 데 사용될 수 있습니다.
        // 보다 정확하게는, 해당 글이 **원글**이고 답글이 달려있다면 삭제를 막아야 합니다.
        
        // 만약 해당 글이 답글(depth > 0)이라면, 그냥 삭제하면 됩니다.
        // 복잡도를 줄이기 위해, 여기서는 '해당 글을 제외한 같은 그룹 내 글의 개수'로 판단합니다.
        
        return count;
    }
}
