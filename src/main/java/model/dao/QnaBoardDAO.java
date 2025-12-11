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
    
    public boolean insertQuestion(QnaBoardDTO dto) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int newIdx = 0;

        // 1. 시퀀스 NEXTVAL 값 조회
        String sqlSelectSeq = "SELECT seq_qna_board_IDX.NEXTVAL FROM dual";
        
        // 2. 게시글 삽입 쿼리
        // REPLY_STATE 컬럼 추가 및 초기값 0 설정
        String sqlInsert = "INSERT INTO qna_board (IDX, USER_IDX, TITLE, CONTENT, POSTDATE, VIEWS, GNUM, ONUM, DEPTH, REPLY_STATE) "
                         + "VALUES (?, ?, ?, ?, SYSDATE, 0, ?, 0, 0, 0)"; 

        try {
            conn = DBConn.getConnection();
            
            // 1단계: 시퀀스 값 조회
            ps = conn.prepareStatement(sqlSelectSeq);
            rs = ps.executeQuery();
            if (rs.next()) {
                newIdx = rs.getInt(1);
            } else {
                System.err.println("시퀀스 값 조회 실패");
                return false;
            }
            
            if (rs != null) rs.close();
            if (ps != null) ps.close();

            // 2단계: 게시글 삽입
            ps = conn.prepareStatement(sqlInsert);
            
            int i = 1;
            ps.setInt(i++, newIdx);
            ps.setInt(i++, dto.getUser_idx());
            ps.setString(i++, dto.getTitle());
            ps.setString(i++, dto.getContent());
            ps.setInt(i++, newIdx);
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Q&A 질문 등록 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
    
    private void updateOnum(Connection conn, int gnum, int onum) throws SQLException {
        String sql = "UPDATE qna_board SET onum = onum + 1 WHERE gnum = ? AND onum > ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gnum);
            ps.setInt(2, onum);
            ps.executeUpdate();
        }
    }

    public boolean insertReply(QnaBoardDTO parentDto, QnaBoardDTO replyDto) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean result = false;

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false);

            // 1. 순서 번호(onum) 조정
            updateOnum(conn, parentDto.getGnum(), parentDto.getOnum());

            // 2. 답글 삽입 (REPLY_STATE=1로 설정)
            String sql = "INSERT INTO qna_board (IDX, USER_IDX, TITLE, CONTENT, POSTDATE, VIEWS, GNUM, ONUM, DEPTH, REPLY_STATE) "
                       + "VALUES (seq_qna_board_IDX.NEXTVAL, ?, ?, ?, SYSDATE, 0, ?, ?, ?, 1)"; 
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, replyDto.getUser_idx());
            ps.setString(2, replyDto.getTitle());
            ps.setString(3, replyDto.getContent());
            
            ps.setInt(4, parentDto.getGnum());
            ps.setInt(5, parentDto.getOnum() + 1);
            ps.setInt(6, parentDto.getDepth() + 1);

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
     * [4. 페이징 목록 조회] (작성자 이름 및 답변 상태 포함)
     */
    public List<QnaBoardDTO> selectList(String searchField, String searchWord, int start, int end) { 
        List<QnaBoardDTO> boardList = new ArrayList<>();
        
        String whereClause = "";
        if (searchWord != null && !searchWord.trim().isEmpty()) {
            if ("all".equals(searchField)) {
                whereClause += "WHERE B.TITLE LIKE '%' || ? || '%' OR B.CONTENT LIKE '%' || ? || '%' ";
            } else {
                whereClause += "WHERE B." + searchField + " LIKE '%' || ? || '%' ";
            }
        }
        
        String sql = "SELECT * FROM ("
                   + "    SELECT ROWNUM AS RNUM, T.* FROM ("
                   // 💡 REPLY_STATE 추가
                   + "        SELECT B.IDX, B.USER_IDX, B.TITLE, B.CONTENT, B.POSTDATE, B.VIEWS, B.GNUM, B.ONUM, B.DEPTH, B.REPLY_STATE, U.NAME AS WRITERNAME " 
                   + "        FROM qna_board B JOIN users U ON B.USER_IDX = U.IDX " 
                   +         whereClause 
                   + "        ORDER BY B.GNUM DESC, B.ONUM ASC"
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
                    
                    dto.setIdx(rs.getInt("IDX"));
                    dto.setUser_idx(rs.getInt("USER_IDX"));
                    dto.setTitle(rs.getString("TITLE"));
                    dto.setContent(rs.getString("CONTENT"));
                    dto.setPostdate(rs.getDate("POSTDATE"));
                    dto.setViews(rs.getInt("VIEWS"));
                    dto.setGnum(rs.getInt("GNUM"));
                    dto.setOnum(rs.getInt("ONUM"));
                    dto.setDepth(rs.getInt("DEPTH"));
                    
                    // 💡 DB에서 읽어온 REPLY_STATE 값을 그대로 설정
                    dto.setReply_state(rs.getInt("REPLY_STATE"));
                    
                    // 작성자 이름 설정
                    dto.setWriterName(rs.getString("WRITERNAME")); 
                    
                    boardList.add(dto);
                }
            }
            
            System.out.println(">>> Q&A 목록 조회 완료. 조회된 게시글 수: " + boardList.size());
            
        } 
        catch (SQLException e) {
            System.err.println("Q&A 목록 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        return boardList;
    }
    
    /**
     * [5. 상세 게시글 조회] (작성자 이름 및 답변 상태 포함)
     */
    public QnaBoardDTO selectBoard(int idx) {
        QnaBoardDTO dto = null;
        String sql = "SELECT B.IDX, B.USER_IDX, B.TITLE, B.CONTENT, B.POSTDATE, B.VIEWS, B.GNUM, B.ONUM, B.DEPTH, B.REPLY_STATE, U.NAME AS WRITERNAME "
                   + "FROM qna_board B JOIN users U ON B.USER_IDX = U.IDX WHERE B.IDX = ?"; 
        
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idx);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto = new QnaBoardDTO();
                    
                    dto.setIdx(rs.getInt("IDX"));
                    dto.setUser_idx(rs.getInt("USER_IDX"));
                    dto.setTitle(rs.getString("TITLE"));
                    dto.setContent(rs.getString("CONTENT"));
                    dto.setPostdate(rs.getDate("POSTDATE"));
                    dto.setViews(rs.getInt("VIEWS"));
                    dto.setGnum(rs.getInt("GNUM"));
                    dto.setOnum(rs.getInt("ONUM"));
                    dto.setDepth(rs.getInt("DEPTH"));
                    
                    // 작성자 이름 설정
                    dto.setWriterName(rs.getString("WRITERNAME")); 
                    
                    // 💡 DB에서 읽어온 REPLY_STATE 값을 그대로 설정
                    dto.setReply_state(rs.getInt("REPLY_STATE")); 
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
        String sql = "UPDATE qna_board SET TITLE = ?, CONTENT = ?, POSTDATE = SYSDATE WHERE IDX = ?";

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
 // QnaBoardDAO.java 파일

    /**
     * [7. 게시글 삭제] 
     * 🚨 수정: 원글을 삭제할 경우 해당 GNUM을 가진 모든 답글을 함께 삭제합니다.
     */
    public boolean deleteBoard(int idx) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean result = false;
        
        // 1. 삭제할 글의 GNUM과 DEPTH를 조회합니다. (idx를 사용해 selectBoard를 호출)
        QnaBoardDTO targetDto = selectBoard(idx); 
        
        if (targetDto == null) {
            System.err.println("삭제하려는 게시글(IDX: " + idx + ")이 존재하지 않습니다.");
            return false;
        }

        // 원글(DEPTH=0)이면 그룹 전체를 삭제하고, 답글(DEPTH>0)이면 해당 글만 삭제합니다.
        String sql = "";
        if (targetDto.getDepth() == 0) {
            // 💡 원글을 삭제하는 경우: 같은 GNUM을 가진 모든 글(원글 + 답글) 삭제
            sql = "DELETE FROM qna_board WHERE GNUM = ?";
        } else {
            // 💡 답글을 삭제하는 경우: 해당 글만 삭제
            sql = "DELETE FROM qna_board WHERE IDX = ?";
        }
        
        try {
            conn = DBConn.getConnection();
            ps = conn.prepareStatement(sql);

            if (targetDto.getDepth() == 0) {
                // 원글 삭제 (GNUM 기준)
                ps.setInt(1, targetDto.getGnum());
            } else {
                // 답글 삭제 (IDX 기준)
                ps.setInt(1, idx);
            }
            
            // delete 쿼리는 성공 시 삭제된 행의 개수를 반환합니다.
            int deleteCount = ps.executeUpdate(); 
            
            if (deleteCount > 0) {
                result = true;
                System.out.println("Q&A 게시글 삭제 성공 (IDX: " + idx + ", 삭제된 행 수: " + deleteCount + ")");
            } else {
                System.err.println("Q&A 게시글 삭제 실패 (IDX: " + idx + ")");
            }
            
            // 💡 주의: 답글을 삭제할 경우 원글의 REPLY_STATE를 확인하고 업데이트해야 하지만,
            // 현재는 '답변완료'가 된 원글의 답글을 지워도 원글의 상태를 '답변대기'로 되돌리지 않고
            // '답변완료'로 유지하는 것이 일반적입니다. (답변이 더 있거나 관리자가 재답변해야 함)
            
        } catch (SQLException e) {
            System.err.println("Q&A 게시글 삭제 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return result;
    }
    
    /**
     * [8. 조회수 증가] 
     */
    public boolean updateViews(int idx) {
        String sql = "UPDATE qna_board SET VIEWS = VIEWS + 1 WHERE IDX = ?";
        
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
     * [9. 답글(자식 글) 개수 조회]
     */
    public int selectReplyCount(int parent_idx) {
        int count = 0;
        
        QnaBoardDTO parent = selectBoard(parent_idx);
        if (parent == null) return 0; 

        String sql = "SELECT COUNT(*) FROM qna_board WHERE GNUM = ? AND IDX != ?";
        
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
        
        return count;
    }
    
    /**
     * 원글 (부모 글)의 답변 상태를 '답변완료'(1)로 업데이트합니다.
     * @param parentIdx 원글의 IDX
     * @return 업데이트 성공 여부
     */
    public boolean updateReplyState(int parentIdx) {
        String sql = "UPDATE qna_board SET reply_state = 1 WHERE idx = ?";
        
        Connection conn = null; // ⭐️ try-with-resources를 사용하지 않음 (Commit 관리를 위해)
        PreparedStatement ps = null;

        try {
            conn = DBConn.getConnection();
            // 💡 AutoCommit이 false일 경우를 대비해 설정 (선택 사항이지만 안전함)
            // if (conn != null) conn.setAutoCommit(false); 

            ps = conn.prepareStatement(sql);
            ps.setInt(1, parentIdx);
            
            int result = ps.executeUpdate();
            System.out.println("답변 상태 업데이트 시도: IDX=" + parentIdx + ", 결과: " + (result > 0 ? "성공" : "실패"));

            if (result > 0) {
                conn.commit(); // ⭐️ [필수 추가]: 여기서 명시적으로 커밋해야 합니다.
            } else {
                 // 롤백은 선택적이지만, 안전하게 추가
                 // conn.rollback(); 
            }
            
            return result > 0;
        } 
        catch (SQLException e) {
            System.err.println("원글 답변 상태 업데이트 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch(SQLException rollbackE) { rollbackE.printStackTrace(); } // 롤백
            return false;
        } finally {
            // ⭐️ 자원 해제
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
}