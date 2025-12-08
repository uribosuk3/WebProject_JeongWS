package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.dto.FreeBoardCommentDTO;
import util.DBConn;

public class FreeBoardCommentDAO {

    private static FreeBoardCommentDAO instance = new FreeBoardCommentDAO();
    public static FreeBoardCommentDAO getInstance() {
        return instance;
    }
    private FreeBoardCommentDAO() {}

    /**
     * [1. 댓글 목록 조회] 특정 게시글(board_idx)의 모든 댓글을 조회합니다.
     * @param board_idx 게시글 고유 번호
     * @return 댓글 목록 리스트
     */
    public List<FreeBoardCommentDTO> selectList(int board_idx) {
        List<FreeBoardCommentDTO> commentList = new ArrayList<>();
        // 최신 댓글이 아래로 오도록 idx ASC 정렬
        String sql = "SELECT idx, board_idx, user_idx, content, postdate "
                   + "FROM free_board_comment WHERE board_idx = ? ORDER BY idx ASC";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, board_idx);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FreeBoardCommentDTO dto = new FreeBoardCommentDTO();
                    dto.setIdx(rs.getInt("idx"));
                    dto.setBoard_idx(rs.getInt("board_idx"));
                    dto.setUser_idx(rs.getInt("user_idx"));
                    dto.setContent(rs.getString("content"));
                    dto.setPostdate(rs.getDate("postdate"));
                    
                    commentList.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("댓글 목록 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return commentList;
    }

    /**
     * [2. 댓글 등록] 새로운 댓글을 DB에 삽입합니다.
     * @param dto 댓글 정보 (board_idx, user_idx, content)
     * @return 성공 여부
     */
    public boolean insertComment(FreeBoardCommentDTO dto) {
        // idx: 시퀀스 사용 (seq_free_board_comment_idx)
        // postdate: SYSDATE
        String sql = "INSERT INTO free_board_comment (idx, board_idx, user_idx, content, postdate) "
                   + "VALUES (seq_free_board_comment_idx.NEXTVAL, ?, ?, ?, SYSDATE)";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dto.getBoard_idx());
            ps.setInt(2, dto.getUser_idx());
            ps.setString(3, dto.getContent());
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("댓글 등록 중 DB 오류 발생: " + e.getMessage());
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
        String sql = "DELETE FROM free_board_comment WHERE idx = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idx);
            
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("댓글 삭제 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * [4. 댓글 정보 조회] 특정 댓글의 정보를 조회합니다. (삭제 권한 확인용)
     * @param idx 댓글 고유 번호
     * @return 댓글 DTO 또는 null
     */
    public FreeBoardCommentDTO selectComment(int idx) {
        FreeBoardCommentDTO dto = null;
        String sql = "SELECT idx, board_idx, user_idx, content, postdate FROM free_board_comment WHERE idx = ?";
        
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idx);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto = new FreeBoardCommentDTO();
                    dto.setIdx(rs.getInt("idx"));
                    dto.setBoard_idx(rs.getInt("board_idx"));
                    dto.setUser_idx(rs.getInt("user_idx"));
                    dto.setContent(rs.getString("content"));
                    dto.setPostdate(rs.getDate("postdate"));
                }
            }
        } catch (SQLException e) {
            System.err.println("댓글 정보 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return dto;
    }
}