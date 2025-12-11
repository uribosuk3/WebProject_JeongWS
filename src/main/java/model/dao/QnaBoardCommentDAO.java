package model.dao; // 실제 패키지 경로로 변경해주세요

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.dto.QnaBoardCommentDTO; // QnaBoardCommentDTO 사용
import util.DBConn; // DB 연결 유틸리티 사용

public class QnaBoardCommentDAO {
    // 싱글톤 패턴
    private static QnaBoardCommentDAO instance = new QnaBoardCommentDAO();
    public static QnaBoardCommentDAO getInstance() {
        return instance;
    }
    private QnaBoardCommentDAO() {}

    /**
     * [1. 댓글 목록 조회] 특정 게시글(board_idx)의 모든 댓글을 조회합니다.
     * 💡 users 테이블을 JOIN하여 작성자 이름(writerName)을 가져옵니다.
     * @param board_idx Q&A 게시글 고유 번호
     * @return 댓글 목록 리스트
     */
    public List<QnaBoardCommentDTO> selectList(int board_idx) {
        List<QnaBoardCommentDTO> commentList = new ArrayList<>();
        
        // SQL: qna_board_comment와 users를 JOIN
        String sql = "SELECT C.idx, C.board_idx, C.user_idx, C.content, C.postdate, U.name AS writerName "
                    + "FROM qna_board_comment C JOIN users U ON C.user_idx = U.idx " 
                    + "WHERE C.board_idx = ? ORDER BY C.idx ASC"; // idx 대신 postdate ASC도 가능

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, board_idx);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QnaBoardCommentDTO dto = new QnaBoardCommentDTO();
                    dto.setIdx(rs.getInt("idx"));
                    dto.setBoard_idx(rs.getInt("board_idx"));
                    dto.setUser_idx(rs.getInt("user_idx"));
                    dto.setContent(rs.getString("content"));
                    dto.setPostdate(rs.getDate("postdate"));
                    
                    // 작성자 이름 매핑 (QnaBoardCommentDTO에 writerName 필드가 있어야 함)
                    dto.setWriterName(rs.getString("writerName")); 
                    
                    commentList.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("[QNA 댓글] 목록 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return commentList;
    }

    /**
     * [2. 댓글 등록] 새로운 댓글을 DB에 삽입합니다.
     * 💡 시퀀스 이름은 seq_qna_board_comment_idx를 사용합니다.
     * @param dto 댓글 정보 (board_idx, user_idx, content)
     * @return 성공 여부
     */
    public boolean insertComment(QnaBoardCommentDTO dto) {
        
        // SQL: Q&A 댓글 시퀀스 사용
        String sql = "INSERT INTO qna_board_comment (idx, board_idx, user_idx, content, postdate) "
                    + "VALUES (seq_qna_board_comment_idx.NEXTVAL, ?, ?, ?, SYSDATE)"; 

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dto.getBoard_idx());
            ps.setInt(2, dto.getUser_idx());
            ps.setString(3, dto.getContent());
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("[QNA 댓글] 등록 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * [3. 댓글 삭제] 특정 댓글을 삭제합니다.
     * @param idx 삭제할 댓글 고유 번호
     * @return 성공 여부
     */
    public boolean deleteComment(int idx) {
        String sql = "DELETE FROM qna_board_comment WHERE idx = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idx);
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("[QNA 댓글] 삭제 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * [4. 댓글 정보 조회] 특정 댓글의 정보를 조회합니다. (권한 확인용)
     * @param idx 댓글 고유 번호
     * @return 댓글 DTO 또는 null
     */
    public QnaBoardCommentDTO selectComment(int idx) {
        QnaBoardCommentDTO dto = null;
        String sql = "SELECT idx, board_idx, user_idx, content, postdate FROM qna_board_comment WHERE idx = ?";
        
        // 이하는 FreeBoardCommentDAO에서 복사한 selectComment 로직과 동일합니다.
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idx);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto = new QnaBoardCommentDTO();
                    dto.setIdx(rs.getInt("idx"));
                    dto.setBoard_idx(rs.getInt("board_idx"));
                    dto.setUser_idx(rs.getInt("user_idx")); // 💡 권한 확인을 위해 user_idx를 가져오는 것이 중요
                    dto.setContent(rs.getString("content"));
                    dto.setPostdate(rs.getDate("postdate"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[QNA 댓글] 단일 정보 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return dto;
    }
}